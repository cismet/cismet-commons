/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.tools;

import java.io.InputStream;
import org.apache.commons.codec.binary.Base64;
import org.junit.*;
import static org.junit.Assert.*;
import sun.misc.BASE64Encoder;

/**
 *
 * @author Martin Scholl
 */
public class PasswordEncrypterTest {

    @Test
    public void testToBase64() {
        System.out.println("TEST toBase64");
        
        String string = "abcd";
        
        byte[] expResult1 = Base64.encodeBase64(string.getBytes());
        String expResult2 = new BASE64Encoder().encode(string.getBytes());
        byte[] result = PasswordEncrypter.toBase64(string.getBytes(), true);
        
        System.out.println(new String(result));
        System.out.println(new String(expResult1));
        System.out.println(expResult2);
        assertArrayEquals(expResult1, result);
    }
    
    /**
     * Test of encryptString method, of class PasswordEncrypter.
     */
    @Test
    public void testEncryptDecryptString() {
        System.out.println("TEST encryptDecryptString");
        
        String pwd = "abc";
        String expResult = "abc";
        String result = PasswordEncrypter.decryptString(PasswordEncrypter.encryptString(pwd));
        assertEquals(expResult, result);
        
        pwd = "9320weSÜF43";
        expResult = "9320weSÜF43";
        result = PasswordEncrypter.decryptString(PasswordEncrypter.encryptString(pwd));
        assertEquals(expResult, result);
        
        pwd = "/u24$3wer&ßdg\\2394r";
        expResult = "/u24$3wer&ßdg\\2394r";
        result = PasswordEncrypter.decryptString(PasswordEncrypter.encryptString(pwd));
        assertEquals(expResult, result);
        
        pwd = "/u24$3wer&ßdg\\2394r";
        expResult = "/u24$3wer&ßdg\\2394r";
        result = PasswordEncrypter.decryptString(PasswordEncrypter.encryptString(pwd));
        assertEquals(expResult, result);
        
    }

    @Test
    public void testEncryptStringAssertNotEqual() {
        System.out.println("TEST encryptString");
        
        String pwd = "dsaed";
        String result1 = PasswordEncrypter.encryptString(pwd);
        String result2 = PasswordEncrypter.encryptString(pwd);
        assertNotSame(result1, result2);
        
        pwd = "-----";
        result1 = PasswordEncrypter.encryptString(pwd);
        result2 = PasswordEncrypter.encryptString(pwd);
        assertNotSame(result1, result2);
    }
    
    // this condition is not met
    @Ignore
    @Test
    public void testEncryptStringAssertFixedLength() {
        System.out.println("TEST encryptString");
        
        String pwd1 = "dsaed";
        String pwd2 = "9032owehrd";
        String result1 = PasswordEncrypter.encryptString(pwd1);
        String result2 = PasswordEncrypter.encryptString(pwd2);
        assertEquals(result1.length(), result2.length());
        
        pwd1 = "dd";
        pwd2 = "ddd";
        result1 = PasswordEncrypter.encryptString(pwd1);
        result2 = PasswordEncrypter.encryptString(pwd2);
        assertEquals(result1.length(), result2.length());
    }

    @Test
    public void testDecryptString() {
        System.out.println("TEST decryptString");
        
        String code = "cryptd::A34ewoajf403worehfd";
        String expResult = code;
        String result = PasswordEncrypter.decryptString(code);
        assertEquals(expResult, result);
        
        code = "A34ewoajf403worehfd";
        expResult = code;
        result = PasswordEncrypter.decryptString(code);
        assertEquals(expResult, result);
    }

    @Test
    public void testDecryptCompatibility() {
        System.out.println("TEST decryptString");
        
        String code = "cryptd::A34ewoajf403worehfd";
        String expResult = code;
        String result = String.valueOf(PasswordEncrypter.decrypt(code.toCharArray()));
        assertEquals(expResult, result);
        
        code = "A34ewoajf403worehfd";
        expResult = code;
        result = String.valueOf(PasswordEncrypter.decrypt(code.toCharArray()));
        assertEquals(expResult, result);
        
        code = "crypt::88f67ebad197b40f6bf85171cee69a0e";
        expResult = "foo";
        result = String.valueOf(PasswordEncrypter.decrypt(code.toCharArray()));
        assertEquals(expResult, result);
    }
        
    /**
     * Test of encrypt method, of class PasswordEncrypter.
     */
    @Ignore
    @Test
    public void testEncryptDecrypt() {
        System.out.println("encrypt");
        char[] string = null;
        char[] expResult = null;
        char[] result = PasswordEncrypter.encrypt(string);
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
            
        propertyStream = getClass().getResourceAsStream("PasswordEncrypterTestSafeRead1.properties");
        property = "xxx".toCharArray();
        expResult = new byte[0];
        result = PasswordEncrypter.safeRead(propertyStream, property);
        assertArrayEquals(expResult, result);
        propertyStream.close();
        
        propertyStream = getClass().getResourceAsStream("PasswordEncrypterTestSafeRead1.properties");
        property = "yyy".toCharArray();
        result = PasswordEncrypter.safeRead(propertyStream, property);
        assertNull(result);
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
