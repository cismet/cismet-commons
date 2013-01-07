/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.tools;

import org.apache.log4j.Logger;

import java.util.*;

/**
 * Calculates values and cached it for a given time period.
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class CalculationCache<KEY extends Object, VALUE extends Object> {

    //~ Static fields/initializers ---------------------------------------------

    protected static final Logger LOG = Logger.getLogger(CalculationCache.class);

    //~ Instance fields --------------------------------------------------------

    protected final Timer t = new Timer(true);
    protected final List<KEY> processingQueries = Collections.synchronizedList(new ArrayList<KEY>());
    protected final Map<KEY, VALUE> cache = Collections.synchronizedMap(new HashMap<KEY, VALUE>());
    protected final Map<KEY, Exception> exceptionCache = Collections.synchronizedMap(new HashMap<KEY, Exception>());
    protected Calculator<KEY, VALUE> calc;
    protected long timeToCacheResults = 10000;
    protected long timeToCacheExceptions = 2000;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new CalculationCache object.
     *
     * @param  calc  the calculator that should be used to calculate values of unknown keys
     */
    public CalculationCache(final Calculator<KEY, VALUE> calc) {
        this.calc = calc;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Calculates the value for the given key, if required and caches it.
     *
     * @param   key  link query
     *
     * @return  value
     *
     * @throws  Exception  if any <code>Exception</code> is in {@link #exceptionCache}
     */
    public VALUE calcValue(final KEY key) throws Exception {
        VALUE result = cache.get(key);

        if (result == null) {
            final Exception e = exceptionCache.get(key);

            if (e != null) {
                throw e;
            } else if (processingQueries.contains(key)) {
                while ((cache.get(key) == null) && processingQueries.contains(key)) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException ex) {
                        // nothing to do
                    }
                }

                return calcValue(key);
            } else {
                processingQueries.add(key);

                try {
                    result = calc.calculate(key);

                    if (result != null) {
                        cache.put(key, result);
                        final TimerTask tt = new TimerTask() {

                                @Override
                                public void run() {
                                    cache.remove(key);
                                }
                            };

                        t.schedule(tt, timeToCacheResults);
                    }

                    return result;
                } catch (final Exception ex) {
                    if (!(ex instanceof InterruptedException)) {
                        exceptionCache.put(key, ex);

                        final TimerTask tt = new TimerTask() {

                                @Override
                                public void run() {
                                    exceptionCache.remove(key);
                                }
                            };

                        t.schedule(tt, timeToCacheExceptions);
                    }
                    throw ex;
                } finally {
                    processingQueries.remove(key);
                }
            }
        } else {
            return result;
        }
    }

    /**
     * Get the time in milliseconds, the result should be cached
     *
     * @return  the time in milliseconds
     */
    public long getTimeToCacheResults() {
        return timeToCacheResults;
    }

    /**
     * set the time in milliseconds, the result should be cached.
     *
     * @param  timeToCacheResults  the timeToCacheResults to set
     */
    public void setTimeToCacheResults(final long timeToCacheResults) {
        this.timeToCacheResults = timeToCacheResults;
    }

    /**
     * get the time in milliseconds, exceptions, which were thrown during the calculation of a value, should be
     *          cached
     *
     * @return  the time in milliseconds
     */
    public long getTimeToCacheExceptions() {
        return timeToCacheExceptions;
    }

    /**
     * set the time in milliseconds, exceptions, which were thrown during the calculation of a value, should be cached.
     *
     * @param  timeToCacheExceptions  the timeToCacheException to set
     */
    public void setTimeToCacheExceptions(final long timeToCacheExceptions) {
        this.timeToCacheExceptions = timeToCacheExceptions;
    }
}
