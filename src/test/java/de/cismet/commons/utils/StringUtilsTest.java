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
}