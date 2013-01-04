/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.tools;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

import de.cismet.commons.concurrency.CismetConcurrency;

/**
 * Cached ThreadPool for all (Swing)Workers in one cismet application.
 *
 * @author      srichter
 * @version     $Revision$, $Date$
 * @deprecated  either use {@link CismetConcurrency#getDefaultExecutor()} or create your own {@link ExecutorService}
 *              using a {@link ThreadFactory} created by an instance of {@link CismetConcurrency}.
 */
@Deprecated
public final class CismetThreadPool {

    //~ Static fields/initializers ---------------------------------------------

    public static final int NUMBER_OF_PROCESSORS = Runtime.getRuntime().availableProcessors();
    private static final ExecutorService WORKER_POOL = Executors.newCachedThreadPool();
    private static final ExecutorService SINGLE_WORKER_POOL = Executors.newSingleThreadExecutor();

    //~ Methods ----------------------------------------------------------------

    /**
     * Executes the given runnable in the applications common cached threadpool. Notice: It is smarter to pass plain
     * Runnables instead of Threads as arguments, as one purpose of this pool is minimizing thread creation overhead.
     *
     * @param       command  DOCUMENT ME!
     *
     * @deprecated  it should not be allowed for a "general-purpose" pool to increase in size infinitely and thus may be
     *              able to break an application just because of allowing to many threads to run concurrently.
     */
    public static void execute(final Runnable command) {
        WORKER_POOL.execute(command);
    }

    /**
     * Executes the given runnable in the applications threadpool. All threads, which will be started with this method
     * will be run sequentially.
     *
     * @param       command  DOCUMENT ME!
     *
     * @deprecated  if there is a need for an execution queue it is not likely that it's focus has to be global.
     *              Additionally it is better to think about the purpose of the sequential execution and then to narrow
     *              it down to the tasks that are really dependent on each other (because that's the whole point of
     *              sequential execution). Maybe other solutions for the issue may be even more suited and/or elegant,
     *              too.
     */
    public static void executeSequentially(final Runnable command) {
        SINGLE_WORKER_POOL.execute(command);
    }

    /**
     * DOCUMENT ME!
     *
     * @param       command  DOCUMENT ME!
     *
     * @return      DOCUMENT ME!
     *
     * @deprecated  it should not be allowed for a "general-purpose" pool to increase in size infinitely and thus may be
     *              able to break an application just because of allowing to many threads to run concurrently.
     */
    public static Future<?> submit(final Runnable command) {
        return WORKER_POOL.submit(command);
    }

    /**
     * DOCUMENT ME!
     *
     * @return      DOCUMENT ME!
     *
     * @deprecated  shutdown immediately renders this global pool unusable. It can never be revived. Thus any call to
     *              this operation is dangerous for any piece of code that depends on this implementation.
     */
    public static List<Runnable> shutdownNow() {
        final List<Runnable> list = SINGLE_WORKER_POOL.shutdownNow();
        list.addAll(WORKER_POOL.shutdownNow());
        return list;
    }

    /**
     * DOCUMENT ME!
     *
     * @deprecated  shutdown immediately renders this global pool unusable. It can never be revived. Thus any call to
     *              this operation is dangerous for any piece of code that depends on this implementation.
     */
    public static void shutdown() {
        SINGLE_WORKER_POOL.shutdown();
        WORKER_POOL.shutdown();
    }
}
