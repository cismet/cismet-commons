package de.cismet.commons.ref;



import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.BeforeClass;
import static org.junit.Assert.*;

/**
 *
 * @author martin.scholl@cismet.de
 */
public class TimedSoftReferenceTest
{

    @BeforeClass
    public static void setUpClass() throws Exception
    {
    }

    @AfterClass
    public static void tearDownClass() throws Exception
    {
    }

    @Before
    public void setUp()
    {
    }

    @After
    public void tearDown()
    {
    }

    private String getCurrentMethodName()
    {
        return new Throwable().getStackTrace()[1].getMethodName();
    }

    @Test
    public void testGet() throws Exception
    {
        System.out.println("TEST " + getCurrentMethodName());
        
        TimedSoftReference tsr = new TimedSoftReference(new Object(), 200);
        Thread.currentThread().sleep(100);
        assertNotNull("timed soft reference cleared too early", tsr.get());
        Thread.currentThread().sleep(100);
        assertNotNull("timed soft reference cleared too early", tsr.get());
        Thread.currentThread().sleep(100);
        assertNotNull("timed soft reference cleared too early", tsr.get());
        Thread.currentThread().sleep(100);
        assertNotNull("timed soft reference cleared too early", tsr.get());
        Thread.currentThread().sleep(100);
        assertNotNull("timed soft reference cleared too early", tsr.get());
        Thread.currentThread().sleep(100);
        assertNotNull("timed soft reference cleared too early", tsr.get());
        Thread.currentThread().sleep(300);
        assertNull("timed soft reference not cleared yet", tsr.get());
    }
}