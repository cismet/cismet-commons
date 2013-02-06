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

import java.lang.reflect.Field;

import java.util.HashMap;

import de.cismet.tools.Calculator;

import static org.junit.Assert.*;

/**
 * These test depends on proper timing and may cause problems sometimes when run on machines that have some kind of
 * scheduling issues (too slow, bad scheduling, etc...).
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public class PurgingCacheTest {

    //~ Instance fields --------------------------------------------------------

    private int initCalls;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new PurgingCacheTest object.
     */
    public PurgingCacheTest() {
    }

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
        initCalls = 0;
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

        final PurgingCache pc = new PurgingCache(new Calculator() {

                    @Override
                    public String calculate(final Object input) throws Exception {
                        initCalls++;

                        return "value_" + input;
                    }
                }, 1000, 200);

        Object o1 = pc.get("1");
        assertEquals("value_1", o1);
        Thread.currentThread().sleep(300);
        o1 = pc.get("1");
        assertEquals("value_1", o1);
        assertEquals(2, initCalls);

        initCalls = 0;
        Thread.currentThread().sleep(300);

        o1 = pc.get("1");
        Object o2 = pc.get("2");
        assertEquals("value_1", o1);
        assertEquals("value_2", o2);
        assertEquals(2, initCalls);
        o1 = pc.get("1");
        o2 = pc.get("2");
        assertEquals("value_1", o1);
        assertEquals("value_2", o2);
        assertEquals(2, initCalls);
        Thread.currentThread().sleep(100);
        o1 = pc.get("1");
        o2 = pc.get("2");
        assertEquals("value_1", o1);
        assertEquals("value_2", o2);
        assertEquals(2, initCalls);
        Thread.currentThread().sleep(100);
        o1 = pc.get("1");
        o2 = pc.get("2");
        assertEquals("value_1", o1);
        assertEquals("value_2", o2);
        assertEquals(2, initCalls);
    }
    /**
     * highly dependent on the internal implementation details of the cache.
     *
     * @throws  Exception  DOCUMENT ME!
     */
    @Test
    public void testGetInternal() throws Exception {
        System.out.println("TEST " + getCurrentMethodName());

        final PurgingCache pc = new PurgingCache(new Calculator() {

                    @Override
                    public String calculate(final Object input) throws Exception {
                        initCalls++;

                        return "value_" + input;
                    }
                }, 500, 200);

        Object o1 = pc.get("1");
        assertEquals("value_1", o1);
        Thread.currentThread().sleep(300);

        final Field cacheField = pc.getClass().getDeclaredField("cache");
        cacheField.setAccessible(true);

        assertEquals(1, ((HashMap)cacheField.get(pc)).size());
        Thread.currentThread().sleep(300);
        assertEquals(0, ((HashMap)cacheField.get(pc)).size());

        o1 = pc.get("1");
        final Object o2 = pc.get("2");
        Object o3 = pc.get("3");
        final Object o4 = pc.get("4");
        Object o5 = pc.get("5");
        assertEquals("value_1", o1);
        assertEquals("value_2", o2);
        assertEquals("value_3", o3);
        assertEquals("value_4", o4);
        assertEquals("value_5", o5);

        assertEquals(5, ((HashMap)cacheField.get(pc)).size());
        Thread.currentThread().sleep(100);
        assertEquals(5, ((HashMap)cacheField.get(pc)).size());
        o3 = pc.get("3");
        o5 = pc.get("5");
        Thread.currentThread().sleep(150);
        assertEquals(5, ((HashMap)cacheField.get(pc)).size());
        o3 = pc.get("3");
        o5 = pc.get("5");
        Thread.currentThread().sleep(300);
        assertEquals(2, ((HashMap)cacheField.get(pc)).size());
        Thread.currentThread().sleep(500);
        assertEquals(0, ((HashMap)cacheField.get(pc)).size());
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    @Test(expected = CacheException.class)
    public void testGet_CalculateException() {
        System.out.println("TEST " + getCurrentMethodName());

        final PurgingCache pc = new PurgingCache(new Calculator() {

                    @Override
                    public String calculate(final Object input) throws Exception {
                        throw new IllegalStateException("test");
                    }
                }, 1000, 200);

        pc.get("1");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testGet_NullKeyException() {
        System.out.println("TEST " + getCurrentMethodName());

        final PurgingCache pc = new PurgingCache(new Calculator() {

                    @Override
                    public String calculate(final Object input) throws Exception {
                        throw new IllegalStateException("test");
                    }
                }, 1000, 200);

        pc.get(null);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    @Test
    public void testSetKeyPurgeInterval() throws Exception {
        System.out.println("TEST " + getCurrentMethodName());

        final PurgingCache pc = new PurgingCache(new Calculator() {

                    @Override
                    public String calculate(final Object input) throws Exception {
                        initCalls++;

                        return "value_" + input;
                    }
                }, 500, 200);
        final Field cacheField = pc.getClass().getDeclaredField("cache");
        cacheField.setAccessible(true);

        Object o1 = pc.get("1");
        assertEquals(1, ((HashMap)cacheField.get(pc)).size());
        Thread.currentThread().sleep(300);
        assertEquals(1, ((HashMap)cacheField.get(pc)).size());
        Thread.currentThread().sleep(300);
        assertEquals(0, ((HashMap)cacheField.get(pc)).size());

        pc.setKeyPurgeInterval(300);

        o1 = pc.get("1");
        assertEquals(1, ((HashMap)cacheField.get(pc)).size());
        Thread.currentThread().sleep(400);
        assertEquals(0, ((HashMap)cacheField.get(pc)).size());

        pc.setKeyPurgeInterval(800);

        o1 = pc.get("1");
        assertEquals(1, ((HashMap)cacheField.get(pc)).size());
        Thread.currentThread().sleep(400);
        assertEquals(1, ((HashMap)cacheField.get(pc)).size());
        Thread.currentThread().sleep(200);
        assertEquals(1, ((HashMap)cacheField.get(pc)).size());
        Thread.currentThread().sleep(300);
        assertEquals(0, ((HashMap)cacheField.get(pc)).size());
        
        pc.setKeyPurgeInterval(0);
        assertEquals(800, pc.getKeyPurgeInterval());
        pc.setKeyPurgeInterval(-100);
        assertEquals(800, pc.getKeyPurgeInterval());
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    @Test
    public void testSetValuePurgeInterval() throws Exception {
        System.out.println("TEST " + getCurrentMethodName());

        final PurgingCache pc = new PurgingCache(new Calculator() {

                    @Override
                    public String calculate(final Object input) throws Exception {
                        initCalls++;

                        return "value_" + input;
                    }
                }, 1000, 200);

        Object o1 = pc.get("1");
        assertEquals(1, initCalls);
        Thread.currentThread().sleep(100);
        o1 = pc.get("1");
        assertEquals(1, initCalls);

        pc.setValuePurgeInterval(500);

        Thread.currentThread().sleep(350);
        o1 = pc.get("1");
        // the previous setValue call has no influence on existing refs
        assertEquals(2, initCalls);
        Thread.currentThread().sleep(350);
        o1 = pc.get("1");
        // now the set has taken effect
        assertEquals(2, initCalls);
        Thread.currentThread().sleep(300);
        o1 = pc.get("1");
        assertEquals(2, initCalls);
        Thread.currentThread().sleep(300);

        pc.setValuePurgeInterval(100);

        o1 = pc.get("1");
        // the previous setValue call has no influence on existing refs
        assertEquals(2, initCalls);
        Thread.currentThread().sleep(600);
        o1 = pc.get("1");
        assertEquals(3, initCalls);
        Thread.currentThread().sleep(200);
        o1 = pc.get("1");
        assertEquals(4, initCalls);
        
        pc.setValuePurgeInterval(0);
        assertEquals(100, pc.getValuePurgeInterval());
        pc.setValuePurgeInterval(-100);
        assertEquals(100, pc.getValuePurgeInterval());
    }
}
