/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.tools;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Cached ThreadPool for all (Swing)Workers in one cismet application.
 *
 * @author   srichter
 * @version  $Revision$, $Date$
 */
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
     * @param  command  given runnable
     */
    public static void execute(final Runnable command) {
        WORKER_POOL.execute(command);
    }

    /**
     * Executes the given runnable in the applications threadpool. All threads, which will be started with this method
     * will be run sequentially.
     *
     * @param  command  given runnable
     */
    public static void executeSequentially(final Runnable command) {
        SINGLE_WORKER_POOL.execute(command);
    }

    /**
     * Submits the given runnable in the applications common cached threadpool.
     *
     * @param   command  given runnable
     *
     * @return  a Future representing pending completion of the runnable
     */
    public static Future<?> submit(final Runnable command) {
        return WORKER_POOL.submit(command);
    }

    /**
     * Attempts to stop all actively executing tasks, halts the
     * processing of waiting tasks, and returns a list of the tasks
     * that were awaiting execution.
     *
     * @return  list
     */
    public static List<Runnable> shutdownNow() {
        final List<Runnable> list = SINGLE_WORKER_POOL.shutdownNow();
        list.addAll(WORKER_POOL.shutdownNow());
        return list;
    }

    /**
     * Initiates an orderly shutdown in which previously submitted
     * tasks are executed, but no new tasks will be accepted.
     */
    public static void shutdown() {
        SINGLE_WORKER_POOL.shutdown();
        WORKER_POOL.shutdown();
    }
}
