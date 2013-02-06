/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.commons.ref;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public class TimedSoftReferenceTest {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * DOCUMENT ME!
     */
    @Before
    public void setUp() {
    }

    /**
     * DOCUMENT ME!
     */
    @After
    public void tearDown() {
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String getCurrentMethodName() {
        return new Throwable().getStackTrace()[1].getMethodName();
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    @Test
    public void testGet() throws Exception {
        System.out.println("TEST " + getCurrentMethodName());

        final TimedSoftReference tsr = new TimedSoftReference(new Object(), 200);
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
