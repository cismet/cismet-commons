/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.commons.ref;

import java.lang.ref.Reference;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import de.cismet.tools.Calculator;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
// NOTE: we do a separate implementation (not using the CalculationCache) as we don't want the users of this
// implementation to be confronted with changed behaviour
public final class PurgingCache<K, V> {

    //~ Static fields/initializers ---------------------------------------------

    public static final long DEFAULT_KEY_PURGE_INTERVAL = 300000L;
    public static final long DEFAULT_VALUE_PURGE_INTERVAL = 30000L;

    //~ Instance fields --------------------------------------------------------

    private final transient Timer purgeTimer;

    private final transient Map<K, TimedSoftReference<V>> cache;
    private final transient ReentrantReadWriteLock cacheLock;

    private final transient Calculator<K, V> initialiser;

    private transient long keyPurgeInterval;
    private transient long valuePurgeInterval;
    private transient TimerTask purgeTask;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new PurgingCache object.
     *
     * @param  initialiser  DOCUMENT ME!
     */
    public PurgingCache(final Calculator<K, V> initialiser) {
        this(initialiser, DEFAULT_KEY_PURGE_INTERVAL, DEFAULT_VALUE_PURGE_INTERVAL);
    }

    /**
     * Creates a new PurgingCache object.
     *
     * @param  initialiser       DOCUMENT ME!
     * @param  keyPurgeInterval  DOCUMENT ME!
     */
    public PurgingCache(final Calculator<K, V> initialiser, final long keyPurgeInterval) {
        this(initialiser, keyPurgeInterval, DEFAULT_VALUE_PURGE_INTERVAL);
    }

    /**
     * Creates a new PurgingCache object.
     *
     * @param  initialiser         DOCUMENT ME!
     * @param  keyPurgeInterval    DOCUMENT ME!
     * @param  valuePurgeInterval  DOCUMENT ME!
     */
    public PurgingCache(final Calculator<K, V> initialiser,
            final long keyPurgeInterval,
            final long valuePurgeInterval) {
        this.initialiser = initialiser;

        cache = new HashMap<K, TimedSoftReference<V>>();
        cacheLock = new ReentrantReadWriteLock(false);
        purgeTimer = new Timer("PurgingCache-purge-timer"); // NOI18N

        setKeyPurgeInterval(keyPurgeInterval);
        setValuePurgeInterval(valuePurgeInterval);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   key  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  CacheException  DOCUMENT ME!
     */
    public V get(final K key) {
        Lock lock = cacheLock.readLock();
        lock.lock();

        try {
            Reference<V> ref = cache.get(key);

            V value = (ref == null) ? null : ref.get();

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

                    cache.put(key, new TimedSoftReference<V>(value, valuePurgeInterval));
                }
            }

            return value;
        } finally {
            lock.unlock();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public long getKeyPurgeInterval() {
        return keyPurgeInterval;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  keyPurgeInterval  DOCUMENT ME!
     */
    public void setKeyPurgeInterval(final long keyPurgeInterval) {
        if (keyPurgeInterval > 0) {
            this.keyPurgeInterval = keyPurgeInterval;

            if (purgeTask != null) {
                // we don't care about the result
                purgeTask.cancel();
            }

            purgeTask = new TimerTask() {

                    @Override
                    public void run() {
                        purgeCache();
                    }
                };

            purgeTimer.scheduleAtFixedRate(purgeTask, keyPurgeInterval, keyPurgeInterval);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public long getValuePurgeInterval() {
        return valuePurgeInterval;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  valuePurgeInterval  DOCUMENT ME!
     */
    public void setValuePurgeInterval(final long valuePurgeInterval) {
        if (valuePurgeInterval > 0) {
            this.valuePurgeInterval = valuePurgeInterval;
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void purgeCache() {
        final Lock wLock = cacheLock.writeLock();
        wLock.lock();

        try {
            final Iterator<K> it = cache.keySet().iterator();
            while (it.hasNext()) {
                final K key = it.next();
                final Reference<V> ref = cache.get(key);
                if ((ref == null) || (ref.get() == null)) {
                    it.remove();
                }
            }
        } finally {
            wLock.unlock();
        }
    }
}
