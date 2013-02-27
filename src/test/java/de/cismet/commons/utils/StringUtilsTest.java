package de.cismet.commons.utils;



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
public class StringUtilsTest
{

    public StringUtilsTest()
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
    public void testDeleteWhitespaces()
    {
        System.out.println("TEST " + getCurrentMethodName());
        
        String input = null;
        String result = StringUtils.deleteWhitespaces(input);
        assertNull(result);
        
        input = "";
        String expected = "";
        result = StringUtils.deleteWhitespaces(input);
        assertEquals(expected, result);
        
        input = " ";
        expected = "";
        result = StringUtils.deleteWhitespaces(input);
        assertEquals(expected, result);
        
        input = "abc";
        expected = "abc";
        result = StringUtils.deleteWhitespaces(input);
        assertEquals(expected, result);
        
        input = " abc ";
        expected = "abc";
        result = StringUtils.deleteWhitespaces(input);
        assertEquals(expected, result);
        
        input = " ab cd ";
        expected = "abcd";
        result = StringUtils.deleteWhitespaces(input);
        assertEquals(expected, result);
        
        input = "\tab\n cd\n\nef ";
        expected = "abcdef";
        result = StringUtils.deleteWhitespaces(input);
        assertEquals(expected, result);
    }
    
    @Test
    public void testIsKeyword() {
        for(final String s : StringUtils.KEYWORDS){
            assertTrue("should be a keyword: " + s, StringUtils.isKeyword(s));
        }
        
        final String[] noKeywords = {null, "", "abc", "pakkage"};
        for(final String s : noKeywords){
            assertFalse("should not be a keyword: " + s, StringUtils.isKeyword(s));
        }
    }
    
    @Test
    public void testToPackage() {
        String part = null;
        String result = StringUtils.toPackage(part);
        assertNull(result);
        
        part = "";
        String expected = "";
        result = StringUtils.toPackage(part);
        assertEquals(expected, result);
        
        part = "a";
        expected = "a";
        result = StringUtils.toPackage(part);
        assertEquals(expected, result);
        
        part = "-";
        expected = "";
        result = StringUtils.toPackage(part);
        assertEquals(expected, result);
        
        part = "9";
        expected = "";
        result = StringUtils.toPackage(part);
        assertEquals(expected, result);
        
        part = "abcde";
        expected = "abcde";
        result = StringUtils.toPackage(part);
        assertEquals(expected, result);
        
        part = "9abcde";
        expected = "abcde";
        result = StringUtils.toPackage(part);
        assertEquals(expected, result);
        
        part = "-abcde-";
        expected = "abcde";
        result = StringUtils.toPackage(part);
        assertEquals(expected, result);
        
        part = "--9abcde";
        expected = "abcde";
        result = StringUtils.toPackage(part);
        assertEquals(expected, result);
        
        part = "----.a.---.";
        expected = "a";
        result = StringUtils.toPackage(part);
        assertEquals(expected, result);
        
        part = "a.a";
        expected = "a.a";
        result = StringUtils.toPackage(part);
        assertEquals(expected, result);
        
        part = ".a.a";
        expected = "a.a";
        result = StringUtils.toPackage(part);
        assertEquals(expected, result);
        
        part = "a.a.";
        expected = "a.a";
        result = StringUtils.toPackage(part);
        assertEquals(expected, result);
        
        part = ".a.a.a.";
        expected = "a.a.a";
        result = StringUtils.toPackage(part);
        assertEquals(expected, result);
        
        part = "abc-def";
        expected = "abcdef";
        result = StringUtils.toPackage(part);
        assertEquals(expected, result);
        
        part = "abc-de_f";
        expected = "abcde_f";
        result = StringUtils.toPackage(part);
        assertEquals(expected, result);
        
        part = ".ab--c._def.2-ghi.J--KLm0";
        expected = "abc._def.ghi.JKLm0";
        result = StringUtils.toPackage(part);
        assertEquals(expected, result);
        
        part = "a..a";
        expected = "a.a";
        result = StringUtils.toPackage(part);
        assertEquals(expected, result);
        
        part = "a.9--.a";
        expected = "a.a";
        result = StringUtils.toPackage(part);
        assertEquals(expected, result);
        
        part = "a.9--.a.9--";
        expected = "a.a";
        result = StringUtils.toPackage(part);
        assertEquals(expected, result);
        
        part = "my.package";
        expected = "my.package_";
        result = StringUtils.toPackage(part);
        assertEquals(expected, result);
        
        part = "null";
        expected = "null_";
        result = StringUtils.toPackage(part);
        assertEquals(expected, result);
    }
}