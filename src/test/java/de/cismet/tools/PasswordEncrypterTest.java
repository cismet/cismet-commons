/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.tools;

import java.io.InputStream;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Martin Scholl
 */
public class PasswordEncrypterTest {
    
    public PasswordEncrypterTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of main method, of class PasswordEncrypter.
     */
    @Ignore
    @Test
    public void testMain() {
        System.out.println("main");
        String[] args = null;
        PasswordEncrypter.main(args);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of decryptString method, of class PasswordEncrypter.
     */
    @Ignore
    @Test
    public void testDecryptString() {
        System.out.println("decryptString");
        String code = "";
        String expResult = "";
        String result = PasswordEncrypter.decryptString(code);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of encryptString method, of class PasswordEncrypter.
     */
    @Ignore
    @Test
    public void testEncryptString() {
        System.out.println("encryptString");
        String pwd = "";
        String expResult = "";
        String result = PasswordEncrypter.encryptString(pwd);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of encrypt method, of class PasswordEncrypter.
     */
    @Ignore
    @Test
    public void testEncrypt() {
        System.out.println("encrypt");
        char[] string = null;
        char[] expResult = null;
        char[] result = PasswordEncrypter.encrypt(string);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of decrypt method, of class PasswordEncrypter.
     */
    @Ignore
    @Test
    public void testDecrypt() {
        System.out.println("decrypt");
        char[] string = null;
        char[] expResult = null;
        char[] result = PasswordEncrypter.decrypt(string);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of safeRead method, of class PasswordEncrypter.
     */
    @Test
    public void testSafeRead() throws Exception {
        System.out.println("TEST: safeRead");
        InputStream propertyStream = getClass().getResourceAsStream("PasswordEncrypterTestSafeRead1.properties");
        
        char[] property = "abc".toCharArray();
        byte[] expResult = getBytes("def".toCharArray());
        byte[] result = PasswordEncrypter.safeRead(propertyStream, property);
        assertArrayEquals(expResult, result);
        propertyStream.close();
        
        // this is going to be null, as only ASCII chars are treated correctly
        propertyStream = getClass().getResourceAsStream("PasswordEncrypterTestSafeRead1.properties");
        property = "ürpk".toCharArray();
        result = PasswordEncrypter.safeRead(propertyStream, property);
        assertNull(result);
        propertyStream.close();
        
        propertyStream = getClass().getResourceAsStream("PasswordEncrypterTestSafeRead1.properties");
        property = "abcd".toCharArray();
        result = PasswordEncrypter.safeRead(propertyStream, property);
        assertNull(result);
        propertyStream.close();
               
        propertyStream = getClass().getResourceAsStream("PasswordEncrypterTestSafeRead1.properties");
        property = "834irtgj".toCharArray();
        expResult = getBytes("aow4lrjtg".toCharArray());
        result = PasswordEncrypter.safeRead(propertyStream, property);
        assertArrayEquals(expResult, result);
        propertyStream.close();
        
        propertyStream = getClass().getResourceAsStream("PasswordEncrypterTestSafeRead1.properties");
        property = "j2ol3er".toCharArray();
        expResult = getBytes("fj2k4olwr".toCharArray());
        result = PasswordEncrypter.safeRead(propertyStream, property);
        assertArrayEquals(expResult, result);
        propertyStream.close();
        
        propertyStream = getClass().getResourceAsStream("PasswordEncrypterTestSafeRead1.properties");
        property = "# foo".toCharArray();
        result = PasswordEncrypter.safeRead(propertyStream, property);
        assertNull(result);
        propertyStream.close();
            
        propertyStream = getClass().getResourceAsStream("PasswordEncrypterTestSafeRead1.properties");
        property = "aejkjhf#efal".toCharArray();
        expResult = getBytes("aio34f#3sd".toCharArray());
        result = PasswordEncrypter.safeRead(propertyStream, property);
        assertArrayEquals(expResult, result);
        propertyStream.close();
            
        propertyStream = getClass().getResourceAsStream("PasswordEncrypterTestSafeRead1.properties");
        property = " fdad  ".toCharArray();
        expResult = getBytes(" 93j ".toCharArray());
        result = PasswordEncrypter.safeRead(propertyStream, property);
        assertArrayEquals(expResult, result);
        propertyStream.close();
    }
    
    private char[] getChars(final byte[] bytes){
        final char[] chars = new char[bytes.length];
        for(int i = 0; i < bytes.length; ++i){
            chars[i] = (char)bytes[i];
        }
        
        return chars;
    }
    
    private byte[] getBytes(final char[] chars){
        final byte[] bytes = new byte[chars.length];
        for(int i = 0; i < chars.length; ++i){
            bytes[i] = (byte)chars[i];
        }
        
        return bytes;
    }
}
