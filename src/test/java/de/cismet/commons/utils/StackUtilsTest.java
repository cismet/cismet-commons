package de.cismet.commons.utils;



import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 *
 * @author martin.scholl@cismet.de
 */
public class StackUtilsTest
{

    public StackUtilsTest()
    {
    }

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
    public void testGetMethodName_0args()
    {
        final String expected = getCurrentMethodName() + "()";
        final String result = StackUtils.getMethodName();
        
        assertEquals(expected, result);
    }

    @Test
    public void testGetMethodName_ObjectArr()
    {
        final String p1 = "p1";
        final int p2 = 2;
        final List p3 = new ArrayList();
        final String expected = getCurrentMethodName() + "(String,Integer,ArrayList)";
        final String result = StackUtils.getMethodName(p1, p2, p3);
        
        assertEquals(expected, result);
    }

    @Test
    public void testGetMethodName_boolean_ObjectArr()
    {
        final String p1 = "p1";
        final int p2 = 2;
        final List p3 = new ArrayList();
        p3.add("abc");
        p3.add(27);
        String expected = getCurrentMethodName() + "(String,Integer,ArrayList)";
        String result = StackUtils.getMethodName(false, new Object[] {p1, p2, p3});
        
        assertEquals(expected, result);
        
        expected = getCurrentMethodName() + "(java.lang.String,java.lang.Integer,java.util.ArrayList)";
        result = StackUtils.getMethodName(true, new Object[] {p1, p2, p3});
        
        assertEquals(expected, result);
        
        
    }

    @Test
    public void testGetMethodName_3args()
    {
        
        final String p1 = "p1";
        final int p2 = 2;
        final List p3 = new ArrayList();
        p3.add("abc");
        p3.add(27);
        String expected = getCurrentMethodName() + "(String,Integer,ArrayList)";
        String result = StackUtils.getMethodName(false, false, new Object[] {p1, p2, p3});
        
        assertEquals(expected, result);
        
        expected = getCurrentMethodName() + "(java.lang.String,java.lang.Integer,java.util.ArrayList)";
        result = StackUtils.getMethodName(true, false, new Object[] {p1, p2, p3});
        
        assertEquals(expected, result);
        
        expected = getCurrentMethodName() + "(String{p1},Integer{2},ArrayList{[abc, 27]})";
        result = StackUtils.getMethodName(false, true, new Object[] {p1, p2, p3});
        
        assertEquals(expected, result);
        
        expected = getCurrentMethodName() + "(java.lang.String{p1},java.lang.Integer{2},java.util.ArrayList{[abc, 27]})";
        result = StackUtils.getMethodName(true, true, new Object[] {p1, p2, p3});
        
        assertEquals(expected, result);
    }

    @Test
    public void testGetDebuggingThrowable()
    {
        final Throwable t = StackUtils.getDebuggingThrowable();
        final String result = t.getStackTrace()[0].getMethodName();
        final String expected = getCurrentMethodName();
        
        assertEquals(expected, result);
    }
    
    @Test
    public void testEqualsStackTrace()
    {
        final StackTraceElement[] ste1 = new StackTraceElement[] {
            new StackTraceElement("c1", "m1", "f1", 1),
            new StackTraceElement("c2", "m2", "f2", 2),
            new StackTraceElement("c3", "m3", "f3", 3),
            new StackTraceElement("c4", "m4", "f4", 4)
        };
        final StackTraceElement[] ste2 = new StackTraceElement[] {
            new StackTraceElement("c2", "m2", "f2", 2),
            new StackTraceElement("c1", "m1", "f1", 1),
            new StackTraceElement("c3", "m3", "f3", 3),
            new StackTraceElement("c4", "m4", "f4", 4)
        };
        final StackTraceElement[] ste3 = new StackTraceElement[] {
            new StackTraceElement("c1", "m1", "f1", 2),
            new StackTraceElement("c2", "m2", "f2", 3),
            new StackTraceElement("c3", "m3", "f3", 4),
            new StackTraceElement("c4", "m4", "f4", 5)
        };
        final StackTraceElement[] ste4 = new StackTraceElement[] {
            new StackTraceElement("c1", "m1", "f1", 1),
            new StackTraceElement("c2", "m2", "f2", 2),
            new StackTraceElement("c3", "m3", "f3", 3),
            new StackTraceElement("c4", "m4", "f4", 4)
        };
        assertFalse(StackUtils.equals(ste1, ste2, false));
        assertTrue(StackUtils.equals(ste1, ste3, false));
        assertFalse(StackUtils.equals(ste1, ste3, true));
        assertTrue(StackUtils.equals(ste1, ste4, true));
    }
    
    @Test
    public void testEqualsStackTraceElement()
    {
        final StackTraceElement ste = new StackTraceElement("c", "m", "f", 1);
        final StackTraceElement ste2 = new StackTraceElement("c", "m", "f", 2);
        final StackTraceElement ste3 = new StackTraceElement("c", "m", "f", 1);
        
        assertTrue(StackUtils.equals(ste, ste2, false));
        assertFalse(StackUtils.equals(ste, ste2, true));
        assertTrue(StackUtils.equals(ste, ste3, true));
    }
}