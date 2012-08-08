/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.tools;

import org.junit.Ignore;
import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.*;

/**
 * DOCUMENT ME!
 *
 * @author   Martin Scholl
 * @version  $Revision$, $Date$
 */
public class PasswordEncrypterTest {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String getCurrentMethodName() {
        return new Throwable().getStackTrace()[1].getMethodName();
    }

    /**
     * Test of encryptString method, of class PasswordEncrypter.
     */
    @Test
    public void testEncryptDecryptString() {
        System.out.println("TEST " + getCurrentMethodName());

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

    /**
     * DOCUMENT ME!
     */
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

    /**
     * DOCUMENT ME!
     */
    @Test
    public void testDecryptCompatibility() {
        System.out.println("TEST " + getCurrentMethodName());

        String code = "cryptd::A34ewoajf403worehfd";
        String expResult = code;
        String result = String.valueOf(PasswordEncrypter.decrypt(code.toCharArray(), true));
        assertEquals(expResult, result);

        code = "A34ewoajf403worehfd";
        expResult = code;
        result = String.valueOf(PasswordEncrypter.decrypt(code.toCharArray(), false));
        assertEquals(expResult, result);

        code = "crypt::88f67ebad197b40f6bf85171cee69a0e";
        expResult = "foo";
        result = String.valueOf(PasswordEncrypter.decrypt(code.toCharArray(), true));
        assertEquals(expResult, result);
    }

    /**
     * Test of encrypt method, of class PasswordEncrypter.
     */
    @Ignore
    @Test
    public void testEncryptDecryptUTF8() {
        System.out.println("TEST " + getCurrentMethodName());
        
        char[] input = null;
        char[] out = PasswordEncrypter.encrypt(input, true);
        char[] in = PasswordEncrypter.decrypt(out, true);
        assertNull(in);
        
        input = "".toCharArray();
        out = PasswordEncrypter.encrypt(input, false);
        assertFalse(input.length == out.length);
        in = PasswordEncrypter.decrypt(out, false);
        assertArrayEquals(input, in);
        
        input = "aaaa".toCharArray();
        out = PasswordEncrypter.encrypt(input, false);
        assertFalse(input.length == out.length);
        in = PasswordEncrypter.decrypt(out, false);
        assertArrayEquals(input, in);
        
        input = "adsfwewrefdg34erf".toCharArray();
        out = PasswordEncrypter.encrypt(input, false);
        assertFalse(input.length == out.length);
        in = PasswordEncrypter.decrypt(out, false);
        assertArrayEquals(input, in);
        
        input = ")§/Zhm$ER876HN §5refbdz0hoiä%GRbf".toCharArray();
        out = PasswordEncrypter.encrypt(input, false);
        assertFalse(input.length == out.length);
        in = PasswordEncrypter.decrypt(out, false);
        assertArrayEquals(input, in);
        
        input = "$§MORGPw45eht-dfHJBeTDfg§$E)QWpesdhg$WRSGDwrlsjg90ou24hre9to35eRGR$%rEfbø¨⁄€ª∞fn4lwef4r".toCharArray();
        out = PasswordEncrypter.encrypt(input, false);
        assertFalse(input.length == out.length);
        in = PasswordEncrypter.decrypt(out, false);
        assertArrayEquals(input, in);
    }

    /**
     * Test of safeRead method, of class PasswordEncrypter.
     *
     * @throws  Exception  DOCUMENT ME!
     */
    @Test
    public void testSafeRead() throws Exception {
        System.out.println("TEST " + getCurrentMethodName());
        InputStream propertyStream = getClass().getResourceAsStream("PasswordEncrypterTestSafeRead1.properties");

        char[] property = "abc".toCharArray();
        byte[] expResult = "def".getBytes("ASCII");
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

    /**
     * DOCUMENT ME!
     *
     * @param   bytes  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private char[] getChars(final byte[] bytes) {
        final char[] chars = new char[bytes.length];
        for (int i = 0; i < bytes.length; ++i) {
            chars[i] = (char)bytes[i];
        }

        return chars;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   chars  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private byte[] getBytes(final char[] chars) {
        final byte[] bytes = new byte[chars.length];
        for (int i = 0; i < chars.length; ++i) {
            bytes[i] = (byte)chars[i];
        }

        return bytes;
    }
}
