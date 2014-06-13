/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.commons.concurrency;

import java.lang.Thread.UncaughtExceptionHandler;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * This class is a copy of {@link Executors} except that the {@link ExecutorService}es created by this class will inform
 * an {@link UncaughtExceptionHandler} in case of a call to any of the <code>ExecutorService</code>' <code>submit</code>
 * operations. The default <code>Executors</code> will not inform an <code>UncaughtExceptionHandler</code> because it
 * assumes that a 'submitter' is responsible for calling {@link Future#get()} to retrieve the execution result which
 * results in an {@link ExecutionException} in case of an error during execution. However, this may result in uncaught
 * and thus 'invisible' exceptions and hence it gets very hard to trace errors if the <code>get()</code> operation will
 * never be called, e.g. because the <code>Future</code> is not directly used by the 'submitter' but delegated to
 * another operation or propagated up the call-stack as a return value, etc.<br/>
 * <br/>
 * However, this class can provide an additional type of <code>ExecutorService</code>: The 'CachedLimitedThreadPool': It
 * behaves like an ordinary 'CachedThreadPool' thus increasing and shrinking in size with respect to the current
 * workload but unlike the 'CachedThreadPool' it can get full, too, similar to the 'FixedThreadPool'.<br/>
 * <br/>
 * <b>NOTE:</b> {@link ScheduledExecutorService} not supported yet, but will be added soon.<br/>
 * <br/>
 * <b>IMPORTANT:</b> Do not use any of these <code>ExecutorService</code>s if you are not sure if an <code>
 * UncaughtExceptionHandler</code> may somehow interfere with the handling that will be done in case of an <code>
 * ExecutionException</code>! Also keep in mind that it may occur that an exception is handled twice: via <code>
 * get()</code> or the resulting <code>ExecutionException</code>, respectively, if called at all and via the <code>
 * UncaughtExceptionHandler</code> if registered.
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0
 */
// TODO: scheduled thread pool executor
public final class CismetExecutors {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new CismetExecutors object.
     *
     * @throws  AssertionError  DOCUMENT ME!
     */
    private CismetExecutors() {
        throw new AssertionError();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Calls
     * {@link #newCachedLimitedThreadPool(int, java.util.concurrent.ThreadFactory, java.util.concurrent.RejectedExecutionHandler)}
     * with a <code>null ThreadFactory</code> and a <code>null RejectedExecutionHandler</code>.
     *
     * @param   maxThreads  max amount of threads this {@link ExecutorService} will ever create
     *
     * @return  the new <code>ExecutorService</code>
     *
     * @see     #newCachedLimitedThreadPool(int, java.util.concurrent.ThreadFactory,
     *          java.util.concurrent.RejectedExecutionHandler)
     */
    public static ExecutorService newCachedLimitedThreadPool(final int maxThreads) {
        return newCachedLimitedThreadPool(maxThreads, null, null);
    }

    /**
     * Calls
     * {@link #newCachedLimitedThreadPool(int, java.util.concurrent.ThreadFactory, java.util.concurrent.RejectedExecutionHandler)}
     * with a <code>null RejectedExecutionHandler</code>.
     *
     * @param   maxThreads     max amount of threads this {@link ExecutorService} will ever create
     * @param   threadFactory  the <code>ThreadFactory</code> that will be responsible for the creation of new threads
     *
     * @return  the new <code>ExecutorService</code>
     *
     * @see     #newCachedLimitedThreadPool(int, java.util.concurrent.ThreadFactory,
     *          java.util.concurrent.RejectedExecutionHandler)
     */
    public static ExecutorService newCachedLimitedThreadPool(final int maxThreads, final ThreadFactory threadFactory) {
        return newCachedLimitedThreadPool(maxThreads, threadFactory, null);
    }

    /**
     * Creates a new {@link ExecutorService} that is dynamic like a <code>CachedThreadPool</code> but has an upper bound
     * of threads executed in parallel. The <code>ExecutorService</code> has the following properties:<br/>
     *
     * <ul>
     *   <li>a minimum of <code>0</code> threads if there are no tasks</li>
     *   <li>a maximum of <code>maxThreads</code></li>
     *   <li>a maximum of <code>(maxThreads + 1) * 2 / 3</code> tasks queued for execution</li>
     *   <li>threads will die after 180 seconds if they are idle</li>
     *   <li>an {@link AbortPolicy} as {@link RejectedExecutionHandler} if the given handler is <code>null</code></li>
     * </ul>
     * <br/>
     * This means that this thread pool will not accept more than <code>maxThreads + ((maxThreads + 1) * 2 / 3)</code>
     * at once for execution. Any supernumerous threads are rejected! By default the <code>AbortPolicy</code> kicks in
     * then which means that an {@link RejectedExecutionException} is thrown.
     *
     * @param   maxThreads     max amount of threads this {@link ExecutorService} will ever create
     * @param   threadFactory  the <code>ThreadFactory</code> that will be responsible for the creation of new threads
     * @param   rejectHandler  the <code>RejectedExecutionHandler</code> that will handle rejected executions, the
     *                         <code>AbortPolicy</code> by default.
     *
     * @return  the new <code>ExecutorService</code>
     *
     * @see     ThreadPoolExecutor
     */
    public static ExecutorService newCachedLimitedThreadPool(final int maxThreads,
            final ThreadFactory threadFactory,
            final RejectedExecutionHandler rejectHandler) {
        final UEHThreadPoolExecutor e = new UEHThreadPoolExecutor(
                maxThreads,
                maxThreads,
                180, // shrink in size after 3 minutes again
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>((maxThreads + 1) * 2 / 3, true),
                threadFactory,
                (rejectHandler == null) ? new AbortPolicy() : rejectHandler);
        e.allowCoreThreadTimeOut(true);

        return e;
    }

    /**
     * @see  Executors#newFixedThreadPool(int)
     */
    public static ExecutorService newFixedThreadPool(final int nThreads) {
        return new UEHThreadPoolExecutor(
                nThreads,
                nThreads,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(),
                Executors.defaultThreadFactory());
    }

    /**
     * @see  Executors#newFixedThreadPool(int, java.util.concurrent.ThreadFactory)
     */
    public static ExecutorService newFixedThreadPool(final int nThreads, final ThreadFactory threadFactory) {
        return new UEHThreadPoolExecutor(
                nThreads,
                nThreads,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(),
                threadFactory);
    }

    /**
     * @see  Executors#newSingleThreadExecutor()
     */
    public static ExecutorService newSingleThreadExecutor() {
        return new DelegatedExecutorService(new UEHThreadPoolExecutor(
                    1,
                    1,
                    0L,
                    TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>(),
                    Executors.defaultThreadFactory()));
    }

    /**
     * @see  Executors#newSingleThreadExecutor(java.util.concurrent.ThreadFactory)
     */
    public static ExecutorService newSingleThreadExecutor(final ThreadFactory threadFactory) {
        return new DelegatedExecutorService(new UEHThreadPoolExecutor(
                    1,
                    1,
                    0L,
                    TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>(),
                    threadFactory));
    }

    /**
     * @see  Executors#newCachedThreadPool()
     */
    public static ExecutorService newCachedThreadPool() {
        return new UEHThreadPoolExecutor(
                0,
                Integer.MAX_VALUE,
                60L,
                TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(),
                Executors.defaultThreadFactory());
    }

    /**
     * @see  Executors#newCachedThreadPool(java.util.concurrent.ThreadFactory)
     */
    public static ExecutorService newCachedThreadPool(final ThreadFactory threadFactory) {
        return new UEHThreadPoolExecutor(
                0,
                Integer.MAX_VALUE,
                60L,
                TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(),
                threadFactory);
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * Ensures that a <code>SingleThreadExecutor</code> will stay that way.
     *
     * @version  $Revision$, $Date$
     */
    private static class DelegatedExecutorService extends AbstractExecutorService {

        //~ Instance fields ----------------------------------------------------

        private final ExecutorService e;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new DelegatedExecutorService object.
         *
         * @param  executor  DOCUMENT ME!
         */
        DelegatedExecutorService(final ExecutorService executor) {
            e = executor;
        }

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @param  command  DOCUMENT ME!
         */
        @Override
        public void execute(final Runnable command) {
            e.execute(command);
        }

        @Override
        public void shutdown() {
            e.shutdown();
        }

        @Override
        public List<Runnable> shutdownNow() {
            return e.shutdownNow();
        }

        @Override
        public boolean isShutdown() {
            return e.isShutdown();
        }

        @Override
        public boolean isTerminated() {
            return e.isTerminated();
        }

        @Override
        public boolean awaitTermination(final long timeout, final TimeUnit unit) throws InterruptedException {
            return e.awaitTermination(timeout, unit);
        }

        @Override
        public Future<?> submit(final Runnable task) {
            return e.submit(task);
        }

        @Override
        public <T> Future<T> submit(final Callable<T> task) {
            return e.submit(task);
        }

        @Override
        public <T> Future<T> submit(final Runnable task, final T result) {
            return e.submit(task, result);
        }

        @Override
        public <T> List<Future<T>> invokeAll(final Collection<? extends Callable<T>> arg0) throws InterruptedException {
            return e.invokeAll(arg0);
        }

        @Override
        public <T> List<Future<T>> invokeAll(final Collection<? extends Callable<T>> arg0,
                final long arg1,
                final TimeUnit arg2) throws InterruptedException {
            return e.invokeAll(arg0, arg1, arg2);
        }

        @Override
        public <T> T invokeAny(final Collection<? extends Callable<T>> arg0) throws InterruptedException,
            ExecutionException {
            return e.invokeAny(arg0);
        }

        @Override
        public <T> T invokeAny(final Collection<? extends Callable<T>> arg0, final long arg1, final TimeUnit arg2)
                throws InterruptedException, ExecutionException, TimeoutException {
            return e.invokeAny(arg0, arg1, arg2);
        }
    }

    /**
     * Extension of the {@link ThreadPoolExecutor} that keeps track of the executed tasks and informs the
     * {@link UncaughtExceptionHandler} associated with the executing thread in case of an exception during task
     * execution.
     *
     * @version  1.0
     */
    public static final class UEHThreadPoolExecutor extends ThreadPoolExecutor {

        //~ Instance fields ----------------------------------------------------

        private final transient Map<Runnable, Thread> runnableToThreadMap;

        //~ Constructors -------------------------------------------------------

        /**
         * @see  ThreadPoolExecutor#ThreadPoolExecutor(int, int, long, java.util.concurrent.TimeUnit,
         *       java.util.concurrent.BlockingQueue, java.util.concurrent.ThreadFactory)
         */
        public UEHThreadPoolExecutor(final int corePoolSize,
                final int maximumPoolSize,
                final long keepAliveTime,
                final TimeUnit unit,
                final BlockingQueue<Runnable> workQueue,
                final ThreadFactory threadFactory) {
            this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, new AbortPolicy());
        }

        /**
         * @see  ThreadPoolExecutor#ThreadPoolExecutor(int, int, long, java.util.concurrent.TimeUnit,
         *       java.util.concurrent.BlockingQueue, java.util.concurrent.ThreadFactory,
         *       java.util.concurrent.RejectedExecutionHandler)
         */
        public UEHThreadPoolExecutor(final int corePoolSize,
                final int maximumPoolSize,
                final long keepAliveTime,
                final TimeUnit unit,
                final BlockingQueue<Runnable> workQueue,
                final ThreadFactory threadFactory,
                final RejectedExecutionHandler rejectHandler) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, rejectHandler);

            this.runnableToThreadMap = new HashMap<Runnable, Thread>();
        }

        //~ Methods ------------------------------------------------------------

        @Override
        protected void beforeExecute(final Thread t, final Runnable r) {
            super.beforeExecute(t, r);

            runnableToThreadMap.put(r, t);
        }

        @Override
        protected void afterExecute(final Runnable r, final Throwable t) {
            super.afterExecute(r, t);

            final Thread thread = runnableToThreadMap.remove(r);

            assert thread != null : "expected associated thread"; // NOI18N

            if ((t == null) && (r instanceof Future)) {
                Throwable thrown = null;
                try {
                    ((Future)r).get();
                } catch (final InterruptedException ex) {
                    Thread.currentThread().interrupt();
                } catch (final ExecutionException ex) {
                    thrown = ex.getCause();
                } catch (final Throwable tw) {
                    thrown = tw;
                }

                if (thrown != null) {
                    final Thread.UncaughtExceptionHandler handler = thread.getUncaughtExceptionHandler();
                    if (handler == null) {
                        final Thread.UncaughtExceptionHandler groupHandler = thread.getThreadGroup();
                        if (groupHandler == null) {
                            final Thread.UncaughtExceptionHandler defHandler = Thread
                                        .getDefaultUncaughtExceptionHandler();
                            if (defHandler != null) {
                                defHandler.uncaughtException(thread, thrown);
                            }
                        } else {
                            groupHandler.uncaughtException(thread, thrown);
                        }
                    } else {
                        handler.uncaughtException(thread, thrown);
                    }
                }
            }
        }
    }
}
