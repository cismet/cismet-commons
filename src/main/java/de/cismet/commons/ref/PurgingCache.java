/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.commons.ref;

import org.apache.log4j.Logger;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import de.cismet.tools.Calculator;

/**
 * This class provides a simple key-value cache. The cache makes use of {@link TimedSoftReference}s to store the values
 * to keys. Thus the values expire after a certain time if they have not already been collected before. The cache allows
 * to define a custom time interval a value will be cached at most. It additionally provides means to purge stale
 * entries from the cache. An entry is considered stale if an existing key does not map to a value at all (most likely
 * because it has been collected before). Purge actions take place at a fixed interval which is can also be controlled.
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0
 */
// NOTE: we do a separate implementation (not using the CalculationCache) as we don't want the users of this
// implementation (the one of CalculationCache) to be confronted with changed behaviour
public final class PurgingCache<K, V> {

    //~ Static fields/initializers ---------------------------------------------

    /** The default key purge interval of the cache, 5 minutes. */
    public static final long DEFAULT_KEY_PURGE_INTERVAL = 300000L;
    /** The default value purge interval of the cache, 30 seconds. */
    public static final long DEFAULT_VALUE_PURGE_INTERVAL = 30000L;
    private static Logger LOG = Logger.getLogger(PurgingCache.class);

    //~ Instance fields --------------------------------------------------------

    private final transient Timer purgeTimer;

    private final transient Map<K, SoftReference> cache;
    private final transient ReentrantReadWriteLock cacheLock;

    private final transient Calculator<K, V> initialiser;

    private transient long keyPurgeInterval;
    private transient long valuePurgeInterval;
    private transient boolean cacheNullValues;
    private transient TimerTask purgeTask;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new PurgingCache object. Using this constructor is similar to using
     * {@link #PurgingCache(de.cismet.tools.Calculator, long, long) } with {@link #DEFAULT_KEY_PURGE_INTERVAL} as <code>
     * keyPurgeInterval</code> and {@link #DEFAULT_VALUE_PURGE_INTERVAL} as <code>valuePurgeInterval</code>.
     *
     * @param  initialiser  the {@link Calculator} instance that is able to create a value for a given key
     */
    public PurgingCache(final Calculator<K, V> initialiser) {
        this(initialiser, DEFAULT_KEY_PURGE_INTERVAL, DEFAULT_VALUE_PURGE_INTERVAL);
    }

    /**
     * Creates a new PurgingCache object. Using this constructor is similar to using
     * {@link #PurgingCache(de.cismet.tools.Calculator, long, long) } with {@link #DEFAULT_KEY_PURGE_INTERVAL} as <code>
     * keyPurgeInterval</code>.
     *
     * @param  initialiser         the {@link Calculator} instance that is able to create a value for a given key
     * @param  valuePurgeInterval  the time in milliseconds that a cached value will be cached at most or <code>0</code>
     *                             if purging of values (after a fixed amount of time) shall be disabled
     */
    public PurgingCache(final Calculator<K, V> initialiser, final long valuePurgeInterval) {
        this(initialiser, DEFAULT_KEY_PURGE_INTERVAL, valuePurgeInterval);
    }

    /**
     * Creates a new PurgingCache object. Using this constructor is similar to using
     * {@link #PurgingCache(de.cismet.tools.Calculator, long, long, boolean) } with <code>false</code> as <code>
     * cacheNullValues</code>.
     *
     * @param  initialiser         the {@link Calculator} instance that is able to create a value for a given key
     * @param  keyPurgeInterval    the time in milliseconds that lies between two purge actions or <code>0</code> if
     *                             purging of keys shall be disabled
     * @param  valuePurgeInterval  the time in milliseconds that a cached value will be cached at most or <code>0</code>
     *                             if purging of values (after a fixed amount of time) shall be disabled
     *
     * @see    Calculator
     * @see    #setKeyPurgeInterval(long)
     * @see    #setValuePurgeInterval(long)
     */
    public PurgingCache(final Calculator<K, V> initialiser,
            final long keyPurgeInterval,
            final long valuePurgeInterval) {
        this(initialiser, keyPurgeInterval, valuePurgeInterval, false);
    }

    /**
     * Creates a new PurgingCache object.
     *
     * @param  initialiser         the {@link Calculator} instance that is able to create a value for a given key
     * @param  keyPurgeInterval    the time in milliseconds that lies between two purge actions or <code>0</code> if
     *                             purging of keys shall be disabled
     * @param  valuePurgeInterval  the time in milliseconds that a cached value will be cached at most or <code>0</code>
     *                             if purging of values (after a fixed amount of time) shall be disabled
     * @param  cacheNullValues     whether or not <code>null</code> values shall be cached
     *
     * @see    Calculator
     * @see    #setKeyPurgeInterval(long)
     * @see    #setValuePurgeInterval(long)
     * @see    #setCacheNullValues(boolean)
     */
    public PurgingCache(final Calculator<K, V> initialiser,
            final long keyPurgeInterval,
            final long valuePurgeInterval,
            final boolean cacheNullValues) {
        this.initialiser = initialiser;

        cache = new HashMap<K, SoftReference>();
        cacheLock = new ReentrantReadWriteLock(false);
        purgeTimer = new Timer("PurgingCache-purge-timer"); // NOI18N

        setKeyPurgeInterval(keyPurgeInterval);
        setValuePurgeInterval(valuePurgeInterval);
        setCacheNullValues(cacheNullValues);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Gets the cached value for a given key. If there exists a cached value it is returned, otherwise the
     * {@link Calculator} instance provided in the constructor is responsible for creating a new value for the given
     * key. The maximal time a value is cached is the current value returned by {@link #getValuePurgeInterval()}.
     * However, it is not guaranteed that the value stays that long in the cache as it makes use of
     * {@link SoftReference}s to store the value.<br/>
     * <br/>
     * <b>NOTE:</b> <code>null</code> keys are currently not supported.
     *
     * @param   <T>  any instance of <code>K</code>
     * @param   key  the key to fetch a cached value for
     *
     * @return  the value corresponding to the key (may be <code>null</code>)
     *
     * @throws  IllegalArgumentException  if the given key is <code>null</code>
     * @throws  CacheException            if the <code>Calculator</code> raises an exception during value creation
     *
     * @see     #setCacheNullValues(boolean)
     */
    public <T extends K> V get(final T key) {
        if (key == null) {
            throw new IllegalArgumentException("null keys currently not supported"); // NOI18N
        }

        Lock lock = cacheLock.readLock();
        lock.lock();

        try {
            Reference ref = cache.get(key);

            Object value = null;

            if (ref != null) {
                try {
                    value = ref.get();
                } catch (Exception e) {
                    LOG.info(String.format("error in PurgeCache. So the value for key %s must be reloaded.", key), e);
                }
            }

            if (value == null) {
                // lock upgrade
                lock.unlock();
                lock = cacheLock.writeLock();
                lock.lock();

                // maybe another thread accomplished to do the initialisation in the meantime
                ref = cache.get(key);
                value = (ref == null) ? null : ref.get();

                if (value == null) {
                    try {
                        value = initialiser.calculate(key);
                    } catch (final Exception ex) {
                        throw new CacheException("cannot compute value for key: " + key, ex); // NOI18N
                    }

                    if ((value == null) && cacheNullValues) {
                        value = new NullValue();
                    }

                    final SoftReference sr;
                    if (valuePurgeInterval == 0) {
                        sr = new SoftReference(value);
                    } else {
                        sr = new TimedSoftReference(value, valuePurgeInterval);
                    }

                    cache.put(key, sr);
                }
            }

            return (value instanceof NullValue) ? null : (V)value;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Getter for the current <code>keyPurgeInterval</code>.
     *
     * @return  the time in milliseconds that lies between two purge actions or <code>0</code> if purging of keys is
     *          currently disabled.
     *
     * @see     #setKeyPurgeInterval(long)
     */
    public long getKeyPurgeInterval() {
        synchronized (purgeTimer) {
            return keyPurgeInterval;
        }
    }

    /**
     * Setter for the <code>keyPurgeInterval</code>. Every <code>keyPurgeInterval</code> milliseconds the cache will
     * check for stale entries and remove them. The change takes effect immediately. A new purge action will take place
     * in <code>keyPurgeInterval</code> milliseconds which means that any purge action that should have taken place is
     * canceled. The value is expected to be in milliseconds. Any value below or equal to <code>0</code> will disable
     * purging of keys.
     *
     * @param  keyPurgeInterval  the time in milliseconds that lies between two purge actions
     */
    public void setKeyPurgeInterval(final long keyPurgeInterval) {
        synchronized (purgeTimer) {
            if (purgeTask != null) {
                // we don't care about the result
                purgeTask.cancel();
            }

            if (keyPurgeInterval > 0) {
                this.keyPurgeInterval = keyPurgeInterval;

                purgeTask = new TimerTask() {

                        @Override
                        public void run() {
                            purgeCache();
                        }
                    };

                purgeTimer.scheduleAtFixedRate(purgeTask, keyPurgeInterval, keyPurgeInterval);
            } else {
                // we don't care about the old task anymore if the purge interval has been set to infinity
                purgeTask = null;
                this.keyPurgeInterval = 0;
            }
        }
    }

    /**
     * Getter for the <code>valuePurgeInterval</code> that is currently used for newly initialised values.
     *
     * @return  the current <code>valuePurgeInterval</code> in milliseconds or <code>0</code> if purging of values is
     *          currently disabled.
     *
     * @see     #setValuePurgeInterval(long)
     */
    public long getValuePurgeInterval() {
        return valuePurgeInterval;
    }

    /**
     * Setter for the <code>valuePurgeInterval</code>. The value that is set here will be used if any <b>new</b>
     * reference to a value is created (during {@link #get(java.lang.Object)}. This implies that changing this value
     * does <b>not</b> affect any existing (cached) references. The value is expected to be in milliseconds. Any value
     * below or equal to <code>0</code> will disable purging of values.
     *
     * @param  valuePurgeInterval  the time in milliseconds after that a cached value will remain referenced if it is
     *                             not accessed at all
     */
    public void setValuePurgeInterval(final long valuePurgeInterval) {
        if (valuePurgeInterval > 0) {
            this.valuePurgeInterval = valuePurgeInterval;
        } else {
            this.valuePurgeInterval = 0;
        }
    }

    /**
     * Getter for <code>cacheNullValues</code> that indicates whether <code>null</code> values calculated for a key will
     * be cached or not.
     *
     * @return  <code>true</code> if caching of <code>null</code> values is enabled, <code>false</code>otherwise
     */
    public boolean isCacheNullValues() {
        return cacheNullValues;
    }

    /**
     * Setter for <code>cacheNullValues</code>. The value that is set here will be used if any <b>new</b> reference to a
     * value is created (during {@link #get(java.lang.Object)}. This implies that changing this value does <b>not</b>
     * affect any existing (cached) references. If the parameter value is <code>false</code> and the value that is
     * initially calculated during <code>get()</code> is <code>null</code> nothing will be cached which implies that any
     * successive call to <code>get()</code> with the same key will cause the value to be calculated again. If the
     * parameter value is <code>true</code> any <code>null</code> value will be treated like any other 'real' reference
     * and thus will be cached and returned immediately for any successive call to <code>get()</code> with the same key.
     *
     * @param  cacheNullValues  <code>true</code> if caching of <code>null</code> values shall be enabled, <code>
     *                          false</code> otherwise
     */
    public void setCacheNullValues(final boolean cacheNullValues) {
        this.cacheNullValues = cacheNullValues;
    }

    /**
     * Removes all entries from the cache regardless of any purge interval setting.
     */
    public void clear() {
        final Lock wLock = cacheLock.writeLock();
        wLock.lock();

        try {
            cache.clear();
        } finally {
            wLock.unlock();
        }
    }

    /**
     * Removes all entries that do not contain any actual value anymore.
     */
    private void purgeCache() {
        final Lock wLock = cacheLock.writeLock();
        wLock.lock();

        try {
            final Iterator<K> it = cache.keySet().iterator();
            while (it.hasNext()) {
                final K key = it.next();
                final Reference ref = cache.get(key);
                if ((ref == null) || (ref.get() == null)) {
                    it.remove();
                }
            }
        } finally {
            wLock.unlock();
        }
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * Private helper class to be used for a value if the caching of <code>null</code> values is enabled and the <code>
     * Calculator</code> calculated <code>null</code> as value for the given key.
     *
     * @version  1.0
     */
    private static final class NullValue {
    }
}
