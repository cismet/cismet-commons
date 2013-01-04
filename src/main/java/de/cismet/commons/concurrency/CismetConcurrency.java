/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.commons.concurrency;

import org.apache.log4j.Logger;

import java.lang.Thread.UncaughtExceptionHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import de.cismet.tools.configuration.ShutdownHook;

/**
 * Utility class that provides some concurrency tools that should be used when concurrency shall take place within
 * cismet applications.
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0
 */
public final class CismetConcurrency implements ShutdownHook {

    //~ Static fields/initializers ---------------------------------------------

    /** LOGGER. */
    private static final transient Logger LOG = Logger.getLogger(CismetConcurrency.class);

    private static final transient Map<String, CismetConcurrency> INSTANCES;
    private static final transient ReentrantLock INITLOCK;

    static {
        INSTANCES = new HashMap<String, CismetConcurrency>(3);
        INITLOCK = new ReentrantLock();
    }

    //~ Instance fields --------------------------------------------------------

    private final transient ThreadGroup threadGroup;
    private final transient ExecutorService defaultExecutor;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new <code>CismetConcurrency</code> object.
     *
     * @param  group  the name of the {@link ThreadGroup} that will be created for this <code>CismetConcurrency</code>
     *                instance
     */
    private CismetConcurrency(final String group) {
        final SecurityManager s = System.getSecurityManager();
        final ThreadGroup parent = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();

        this.threadGroup = new ThreadGroup(parent, group);
        this.defaultExecutor = CismetExecutors.newCachedLimitedThreadPool(
                30,
                createThreadFactory("default"), // NOI18N
                new LoggingAbortPolicy());
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Creates a new <code>CismetConcurrency</code> instance for the given group or uses an already existing one.
     *
     * @param   group  the name of the group whose instance shall be returned
     *
     * @return  an instance of <code>CismetConcurrency</code> for the given group or <code>null</code> if the group is
     *          <code>null</code>
     */
    public static CismetConcurrency getInstance(final String group) {
        if (group == null) {
            return null;
        }

        // we prefer the lock implementation over synchronised because some benchmarks indicate, that it is faster
        INITLOCK.lock();
        try {
            CismetConcurrency cc = INSTANCES.get(group);
            if (cc == null) {
                cc = new CismetConcurrency(group);
            }

            return cc;
        } finally {
            INITLOCK.unlock();
        }
    }

    /**
     * Getter for the default {@link ExecutorService} for cismet applications. Note that this executor has a upper limit
     * how many tasks can be run in parallel and how many wait for the execution. This means that an exception is thrown
     * if the executor is fully loaded.
     *
     * @return  the default <code>ExecutorService</code>
     *
     * @see     CismetExecutors#newCachedLimitedThreadPool(int, java.util.concurrent.ThreadFactory,
     *          java.util.concurrent.RejectedExecutionHandler)
     */
    public ExecutorService getDefaultExecutor() {
        return defaultExecutor;
    }

    /**
     * The {@link ThreadGroup} that is used for the default executor as well as should be used for every thread/executor
     * created for the application group.
     *
     * @return  the <code>ThreadGroup</code> of this <code>CismetConcurrency</code>
     *
     * @see     #getInstance(java.lang.String)
     */
    public ThreadGroup getThreadGroup() {
        return threadGroup;
    }

    /**
     * Getter for the group name that was used to retrieve this <code>CismetConcurrency</code> instance.
     *
     * @return  the group name
     *
     * @see     #getInstance(java.lang.String)
     */
    public String getGroup() {
        return threadGroup.getName();
    }

    /**
     * Calls {@link #createThreadFactory(java.lang.String, java.lang.Thread.UncaughtExceptionHandler)} with a <code>null
     * UncaughtExceptionHandler</code>.
     *
     * @param   prefix  the prefix for every thread created by this factory
     *
     * @return  a new <code>ThreadFactory</code>
     *
     * @see     #createThreadFactory(java.lang.String, java.lang.Thread.UncaughtExceptionHandler)
     */
    public ThreadFactory createThreadFactory(final String prefix) {
        return createThreadFactory(prefix, null);
    }

    /**
     * Creates a new {@link CismetThreadFactory} using the {@link ThreadGroup} of this <code>CismetConcurrency</code>
     * instance, the given prefix and the given {@link UncaughtExceptionHandler}.
     *
     * @param   prefix      the prefix for every thread created by this factory
     * @param   excHandler  an <code>UncaughtExceptionHandler</code> for the threads created by the new <code>
     *                      ThreadFactory</code>
     *
     * @return  a new <code>ThreadFactory</code>
     *
     * @see     CismetThreadFactory
     */
    public ThreadFactory createThreadFactory(final String prefix, final Thread.UncaughtExceptionHandler excHandler) {
        return new CismetThreadFactory(threadGroup, prefix, excHandler);
    }

    @Override
    public void applicationFinished() {
        defaultExecutor.shutdown();
        try {
            if (!defaultExecutor.awaitTermination(20, TimeUnit.SECONDS)) {
                LOG.warn(
                    "the default executor could not be terminated within 20 seconds, thus there may be locked tasks that prevent a proper application shutdown"); // NOI18N
            }
        } catch (final InterruptedException ex) {
            if (!defaultExecutor.isTerminated()) {
                LOG.warn(
                    "could not await termination of default executor, there may still be tasks running, that prevent the application to properly shutdown",       // NOI18N
                    ex);
            }
        }
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * Very similar to the {@link Executors#defaultThreadFactory()} implementation. All threads created by this factory
     * will be contained in the given {@link ThreadGroup} and the thread's names will have the follow pattern:<br/>
     * <br/>
     * '<i>&lt;given_prefix&gt;</i>-pool-<i>&lt;global_factory_number&gt;</i>-thread-<i>&lt;created_threads_count&gt;</i>'
     * <br/>
     * <br/>
     * e.g. <code>myCoolPrefix-pool-3-thread-27</code><br/>
     * <br/>
     * Additionally every thread created is not a daemon thread, has {@link Thread#NORM_PRIORITY} priority and uses the
     * given {@link UncaughtExceptionHandler} as exception handler.
     *
     * @version  1.0
     */
    public static final class CismetThreadFactory implements ThreadFactory {

        //~ Static fields/initializers -----------------------------------------

        private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);

        //~ Instance fields ----------------------------------------------------

        private final transient ThreadGroup threadGroup;
        private final transient String prefix;
        private final transient AtomicInteger createCount;
        private final transient Thread.UncaughtExceptionHandler excHandler;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new SudplanThreadFactory object.
         *
         * @param  threadGroup  the <code>ThreadGroup</code> for every thread created by this factory
         * @param  prefix       the prefix for every thread's name created by this factory
         * @param  excHandler   the {@link UncaughtExceptionHandler} for every thread created by this factory
         */
        public CismetThreadFactory(final ThreadGroup threadGroup,
                final String prefix,
                final Thread.UncaughtExceptionHandler excHandler) {
            this.threadGroup = threadGroup;
            this.prefix = prefix + "-pool-" + POOL_NUMBER.getAndIncrement() + "-thread-"; // NOI18N
            this.createCount = new AtomicInteger(1);
            this.excHandler = excHandler;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public Thread newThread(final Runnable r) {
            final Thread t = new Thread(threadGroup, r, prefix + createCount.getAndIncrement(), 0);

            t.setDaemon(false);
            t.setPriority(Thread.NORM_PRIORITY);
            t.setUncaughtExceptionHandler(excHandler);

            return t;
        }
    }

    /**
     * An {@link AbortPolicy} that logs a warn message (using log4j) before doing the actual handling.
     *
     * @version  1.0
     * @see      AbortPolicy
     */
    public static final class LoggingAbortPolicy extends AbortPolicy {

        //~ Static fields/initializers -----------------------------------------

        /** LOGGER. */
        private static final transient Logger LOG = Logger.getLogger(LoggingAbortPolicy.class);

        //~ Methods ------------------------------------------------------------

        @Override
        public void rejectedExecution(final Runnable r, final ThreadPoolExecutor e) {
            LOG.warn("rejecting execution of runnable: [runnable=" + r + "|executor=" + e + "]"); // NOI18N

            super.rejectedExecution(r, e);
        }
    }
}
