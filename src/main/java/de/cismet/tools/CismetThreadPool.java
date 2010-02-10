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
 * @author srichter
 */
public final class CismetThreadPool {

    public static final int NUMBER_OF_PROCESSORS = Runtime.getRuntime().availableProcessors();
    private static final ExecutorService WORKER_POOL = Executors.newCachedThreadPool();

    /**
     * Executes the given runnable in the applications common cached threadpool.
     * Notice: It is smarter to pass plain Runnables instead of Threads as
     * arguments, as one purpose of this pool is minimizing thread creation
     * overhead.
     * @param command
     */
    public static final void execute(Runnable command) {
        WORKER_POOL.execute(command);
    }

    public static final Future<?> submit(Runnable command) {
        return WORKER_POOL.submit(command);
    }

    public static final List<Runnable> shutdownNow() {
        return WORKER_POOL.shutdownNow();
    }

    public static final void shutdown() {
        WORKER_POOL.shutdown();
    }
}
