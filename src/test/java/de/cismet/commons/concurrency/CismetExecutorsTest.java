package de.cismet.commons.concurrency;

import de.cismet.commons.concurrency.CismetConcurrency.CismetThreadFactory;
import de.cismet.commons.concurrency.CismetConcurrency.LoggingAbortPolicy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * These test depends on proper timing and may cause problems sometimes when run on machines that have some kind of
 * scheduling issues (too slow, bad scheduling, etc...).
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0
 */
public class CismetExecutorsTest
{

    /**
     * Test of newCachedLimitedThreadPool method, of class CismetExecutors.
     */
    @Test
    public void testNewCachedLimitedThreadPool_ensureParallelExec() throws Exception
    {        
        final SecurityManager s = System.getSecurityManager();
        final ThreadGroup parent = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();

        final ThreadGroup threadGroup = new ThreadGroup(parent, "thegroup");
        ExecutorService es = CismetExecutors.newCachedLimitedThreadPool(
                3,
                new CismetThreadFactory(threadGroup, "default", null),
                new LoggingAbortPolicy());

        es.submit(getRunnable(2000));
        es.submit(getRunnable(2000));
        es.submit(getRunnable(2000));

        es.shutdown();
        // allow overhead
        assertTrue(es.awaitTermination(2500, TimeUnit.MILLISECONDS));
        
        es = CismetExecutors.newCachedLimitedThreadPool(
                3,
                new CismetThreadFactory(threadGroup, "default", null),
                new LoggingAbortPolicy());

        es.submit(getRunnable(2000));
        es.submit(getRunnable(2000));
        es.submit(getRunnable(2000));
        es.submit(getRunnable(2000));
        es.submit(getRunnable(2000));

        es.shutdown();
        // allow overhead
        assertTrue(es.awaitTermination(4500, TimeUnit.MILLISECONDS));
    }
    
    @Test
    public void testNewCachedLimitedThreadPool_ensureReject() throws Exception
    {        
        final SecurityManager s = System.getSecurityManager();
        final ThreadGroup parent = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();

        final ThreadGroup threadGroup = new ThreadGroup(parent, "thegroup");
        final ExecutorService es = CismetExecutors.newCachedLimitedThreadPool(
                3,
                new CismetThreadFactory(threadGroup, "default", null),
                new LoggingAbortPolicy());

        es.submit(getRunnable(2000));
        es.submit(getRunnable(2000));
        es.submit(getRunnable(2000));
        es.submit(getRunnable(2000));
        es.submit(getRunnable(2000));
        try
        {
            // should reject
            es.submit(getRunnable(2000));
            fail("expected RejectedExecutionException");
        }catch(final RejectedExecutionException e)
        {
            // expected
        } finally {
            es.shutdown();
        }
    }
    
    private Runnable getRunnable(final int sleepMillis) {
        return new Runnable()
        {

            @Override
            public void run()
            {
                try
                {
                    Thread.sleep(sleepMillis);
                }catch(final Exception e)
                {
                    
                }
            }
        };
    }
}
