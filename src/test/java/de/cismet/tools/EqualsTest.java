package de.cismet.tools;



import java.lang.reflect.Method;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author martin.scholl@cismet.de
 */
public class EqualsTest
{

    public EqualsTest()
    {
    }

    private String getCurrentMethodName()
    {
        return new Throwable().getStackTrace()[1].getMethodName();
    }

    @Test
    public void testBeanDeepEqual()
    {
        System.out.println("TEST " + getCurrentMethodName());
        
        final BeanTestClass b1 = new BeanTestClass();
        final BeanTestClass b2 = new BeanTestClass();
        final BeanTestClass b3 = new BeanTestClass("abc", 3, 3.3, true, false, true, "1", "my");
        final BeanTestClass b4 = new BeanTestClass("abc", 3, 3.3, true, false, true, "1", "my");
        final BeanTestClass b5 = new BeanTestClass("abc", 3, 3.3, true, false, true, "1", "your");
        final BeanTestClass b6 = new BeanTestClass("abc", 3, 3.3, true, false, false, "1", "your");
        final BeanTestClass b7 = new BeanTestClass("ab", 3, 3.3, true, false, false, "1", "your");
        
        assertTrue(Equals.beanDeepEqual(null, null));
        assertTrue(Equals.beanDeepEqual(b1, b2));
        assertTrue(Equals.beanDeepEqual(b3, b4));
        assertTrue(Equals.beanDeepEqual(b3, b5));
        assertTrue(Equals.beanDeepEqual(b3, b4));

        assertFalse(Equals.beanDeepEqual(null, b2));
        assertFalse(Equals.beanDeepEqual(b1, null));
        assertFalse(Equals.beanDeepEqual(b1, new Object()));
        assertFalse(Equals.beanDeepEqual(b1, new BeanTestSubClass()));
        assertFalse(Equals.beanDeepEqual(b1, new BeanTestSubClass2()));
        assertFalse(Equals.beanDeepEqual(b1, b3));
        assertFalse(Equals.beanDeepEqual(b2, b7));
        assertFalse(Equals.beanDeepEqual(b3, b6));
        assertFalse(Equals.beanDeepEqual(b5, b7));
        assertFalse(Equals.beanDeepEqual(b6, b7));
    }

    @Test
    public void testBeanDeepEqualIgnore()
    {
        System.out.println("TEST " + getCurrentMethodName());
        
        final BeanTestClass b1 = new BeanTestClass();
        final BeanTestClass b2 = new BeanTestClass();
        final BeanTestClass b3 = new BeanTestClass("abc", 3, 3.3, true, false, true, "1", "my");
        final BeanTestClass b4 = new BeanTestClass("abc", 3, 3.3, true, false, true, "1", "my");
        final BeanTestClass b5 = new BeanTestClass("abc", 3, 3.3, true, false, true, "1", "your");
        final BeanTestClass b6 = new BeanTestClass("abc", 3, 3.3, true, false, false, "1", "your");
        final BeanTestClass b7 = new BeanTestClass("ab", 3, 3.3, true, false, false, "1", "your");
        
        assertTrue(Equals.beanDeepEqual(null, null));
        assertTrue(Equals.beanDeepEqual(b1, b2));
        assertTrue(Equals.beanDeepEqual(b3, b4));
        assertTrue(Equals.beanDeepEqual(b3, b5));
        assertTrue(Equals.beanDeepEqual(b3, b4));
        
        assertTrue(Equals.beanDeepEqual(b2, b7, "getS", "getI", "getDoubble", "isB", "getMyGoodObj"));
        assertTrue(Equals.beanDeepEqual(b3, b6, "getS", "isTheBool"));
        assertTrue(Equals.beanDeepEqual(b5, b7, "getS", "isTheBool"));
        assertTrue(Equals.beanDeepEqual(b6, b7, "getS"));

        assertFalse(Equals.beanDeepEqual(null, b2));
        assertFalse(Equals.beanDeepEqual(b1, null));
        assertFalse(Equals.beanDeepEqual(b1, new Object()));
        assertFalse(Equals.beanDeepEqual(b1, new BeanTestSubClass()));
        assertFalse(Equals.beanDeepEqual(b1, new BeanTestSubClass2()));
        assertFalse(Equals.beanDeepEqual(b1, b3));
        assertFalse(Equals.beanDeepEqual(b2, b7));
        assertFalse(Equals.beanDeepEqual(b3, b6));
        assertFalse(Equals.beanDeepEqual(b5, b7));
        assertFalse(Equals.beanDeepEqual(b6, b7));
    }
    
    @Test
    public void testIsBeanGetter() throws Exception
    {
        System.out.println("TEST " + getCurrentMethodName());
        
        final BeanTestClass i = new BeanTestClass();
        final Class c = i.getClass();
        
        Method m = c.getMethod("isB", (Class[])null);
        assertTrue(Equals.isBeanGetter(m));
        
        m = c.getMethod("getBool", (Class[])null);
        assertTrue(Equals.isBeanGetter(m));
        
        m = c.getMethod("getDoubble", (Class[])null);
        assertTrue(Equals.isBeanGetter(m));
        
        m = c.getMethod("getI", (Class[])null);
        assertTrue(Equals.isBeanGetter(m));
        
        m = c.getMethod("getMyGoodObj", (Class[])null);
        assertTrue(Equals.isBeanGetter(m));
        
        m = c.getMethod("getBool", (Class[])null);
        assertTrue(Equals.isBeanGetter(m));
        
        m = c.getMethod("getS", (Class[])null);
        assertTrue(Equals.isBeanGetter(m));
        
        m = c.getMethod("isTheBool", (Class[])null);
        assertTrue(Equals.isBeanGetter(m));
        
        m = c.getMethod("getObject", (Class[])null);
        assertFalse(Equals.isBeanGetter(m));
        
        m = c.getMethod("getTheString", new Class[]{int.class});
        assertFalse(Equals.isBeanGetter(m));
        
        m = c.getMethod("m", (Class[])null);
        assertFalse(Equals.isBeanGetter(m));
    }
    
    private final class BeanTestSubClass extends BeanTestClass{
        private String s1;

        public String getS1()
        {
            return s1;
        }
    }
    
    private final class BeanTestSubClass2 extends BeanTestClass {

        @Override
        public boolean getBool()
        {
            return super.getBool();
        }
    }
    
    private class BeanTestClass {
        private String s;
        private int i;
        private Double doubble;
        private boolean b;
        private boolean bool;
        private boolean theBool;
        private Object myGoodObj;
        private String myString;

        public BeanTestClass()
        {
        }

        public BeanTestClass(String s, int i, Double doubble, boolean b, boolean bool, boolean theBool, Object myGoodObj, String myString)
        {
            this.s = s;
            this.i = i;
            this.doubble = doubble;
            this.b = b;
            this.bool = bool;
            this.theBool = theBool;
            this.myGoodObj = myGoodObj;
            this.myString = myString;
        }

        public boolean isB()
        {
            return b;
        }

        public boolean getBool()
        {
            return bool;
        }

        public Double getDoubble()
        {
            return doubble;
        }

        public int getI()
        {
            return i;
        }

        public Object getMyGoodObj()
        {
            return myGoodObj;
        }

        public String getS()
        {
            return s;
        }

        public boolean isTheBool()
        {
            return theBool;
        }
        
        public Object getObject(){
            return null;
        }
        
        public String getTheString(int i){
            return myString;
        }
        
        public Object m(){
            return null;
        }
    }

    @Test
    public void testNullEqual()
    {
        System.out.println("TEST " + getCurrentMethodName());
        
        assertTrue(Equals.nullEqual(null, null));
        final Object o = "";
        assertTrue(Equals.nullEqual(o, o));
        assertTrue(Equals.nullEqual(o, ""));
        assertFalse(Equals.nullEqual(o, new Object()));
        assertFalse(Equals.nullEqual(null, new Object()));
        assertFalse(Equals.nullEqual(o, null));
    }

    @Test
    public void testXor()
    {
        System.out.println("TEST " + getCurrentMethodName());
        
        assertTrue(Equals.xor(1, null));
        assertTrue(Equals.xor(null, ""));
        assertFalse(Equals.xor(null, null));
        assertFalse(Equals.xor(1, ""));
    }

    @Test
    public void testAllNull()
    {
        System.out.println("TEST " + getCurrentMethodName());
        
        assertTrue(Equals.allNull((Object[])null));
        assertTrue(Equals.allNull(null, null, null, null));
        assertFalse(Equals.allNull(""));
        assertFalse(Equals.allNull(null, "", null));
        assertFalse(Equals.allNull(null, null, null, 1));
        assertFalse(Equals.allNull(1, 2, 3, 4));
    }

    @Test
    public void testNonNull()
    {
        System.out.println("TEST " + getCurrentMethodName());
        
        assertTrue(Equals.nonNull(new Object()));
        assertTrue(Equals.nonNull(new Object(), "", 1));
        assertFalse(Equals.nonNull((Object[])null));
        assertFalse(Equals.nonNull(null, null));
        assertFalse(Equals.nonNull(1, new Object(), null, ""));
    }
}