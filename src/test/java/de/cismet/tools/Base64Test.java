/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.tools;

import org.junit.Test;


import static org.junit.Assert.*;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public class Base64Test {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Base64Test object.
     */
    public Base64Test() {
    }

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
     * DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    @Test
    public void testToBase64UTF8() throws Exception {
        System.out.println("TEST " + getCurrentMethodName());
        
        String input;
        byte[] expResult;
        byte[] result;
        
        try
        {
            Base64.toBase64(null, false);
            fail("expected IllegalArgumentException");
        }catch(final IllegalArgumentException e)
        {
            // cool;
        }

        input = "";
        expResult = "".getBytes("UTF-8");
        result = Base64.toBase64(input.getBytes("UTF-8"), true);
        assertArrayEquals(expResult, result);
        
        input = "abc";
        expResult = "YWJj".getBytes("UTF-8");
        result = Base64.toBase64(input.getBytes("UTF-8"), true);
        assertArrayEquals(expResult, result);

        input = "2309wohetöoh349 5o";
        expResult = "MjMwOXdvaGV0w7ZvaDM0OSA1bw==".getBytes("UTF-8");
        result = Base64.toBase64(input.getBytes("UTF-8"), true);
        assertArrayEquals(expResult, result);

        input = "!§$W)rgußp43rhgoö3    4lahrsbjgöoq35eulrghfgnq3n 4wctäw-seejrgdn35le,r hö3 8-itenq5g4.et fgb, m";
        expResult = "IcKnJFcpcmd1w59wNDNyaGdvw7YzICAgIDRsYWhyc2JqZ8O2b3EzNWV1bHJnaGZnbnEzbiA0d2N0\r\nw6R3LXNlZWpyZ2RuMzVsZSxyIGjDtjMgOC1pdGVucTVnNC5ldCBmZ2IsIG0="
                    .getBytes("UTF-8");
        result = Base64.toBase64(input.getBytes("UTF-8"), true);
        assertArrayEquals(expResult, result);

        input = "1ü0ß234z58hlkl dtu3ä95er-gjä3 rötuo-§Q%OJEZHj4pw5erHJTQ §tuä3 5-j FG AUecpthzu43p uwg#QBE >SJ>D Gjqhä6"
                    + "fjkda öh4fi- awrzsägvkny-dj göäawz g2    h-4wxußä249uß9p24i3retkglfdvc. fnwBÖUIEL<RSN ÖOFPW4RSÖ FF4 "
                    + "RG1E22F4ERPHTOSÖ . JRGLET.GFNXK-LODG UBJ ";
        expResult = "McO8MMOfMjM0ejU4aGxrbCBkdHUzw6Q5NWVyLWdqw6QzIHLDtnR1by3Cp1ElT0pFWkhqNHB3NWVy\r\nSEpUUSDCp3R1w6QzIDUtaiBGRyBBVWVjcHRoenU0M3AgdXdnI1FCRSA+U0o+RCBHanFow6Q2Zmpr\r\nZGEgw7ZoNGZpLSBhd3J6c8OkZ3ZrbnktZGogZ8O2w6Rhd3ogZzIgICAgaC00d3h1w5/DpDI0OXXD\r\nnzlwMjRpM3JldGtnbGZkdmMuIGZud0LDllVJRUw8UlNOIMOWT0ZQVzRSU8OWIEZGNCBSRzFFMjJG\r\nNEVSUEhUT1PDliAuIEpSR0xFVC5HRk5YSy1MT0RHIFVCSiA="
                    .getBytes("UTF-8");
        result = Base64.toBase64(input.getBytes("UTF-8"), false);
        assertArrayEquals(expResult, result);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    @Test
    public void testFromBase64UTF8() throws Exception {
        System.out.println("TEST " + getCurrentMethodName());

        String input = "";
        byte[] expResult;
        byte[] result;
        
        try
        {
            Base64.fromBase64(null, false);
            fail("expected IllegalArgumentException");
        }catch(final IllegalArgumentException e)
        {
            // cool
        }
        
        try
        {
            Base64.fromBase64(input.getBytes("UTF-8"), false);
            fail("expected IllegalArgumentException");
        }catch(final IllegalArgumentException e)
        {
            // cool
        }
        
        input = "aaa";
        try
        {
            Base64.fromBase64(input.getBytes("UTF-8"), false);
            fail("expected IllegalArgumentException");
        }catch(final IllegalArgumentException e)
        {
            // cool
        }
        
        input = "----";
        try
        {
            Base64.fromBase64(input.getBytes("UTF-8"), false);
            fail("expected IllegalArgumentException");
        }catch(final IllegalArgumentException e)
        {
            // cool
        }
        
        input = "aaaabbbbccccddd";
        try
        {
            Base64.fromBase64(input.getBytes("UTF-8"), false);
            fail("expected IllegalArgumentException");
        }catch(final IllegalArgumentException e)
        {
            // cool
        }

        input = "YWJj";
        expResult = "abc".getBytes("UTF-8");
        result = Base64.fromBase64(input.getBytes("UTF-8"), true);
        assertArrayEquals(expResult, result);

        input = "MjMwOXdvaGV0w7ZvaDM0OSA1bw==";
        expResult = "2309wohetöoh349 5o".getBytes("UTF-8");
        result = Base64.fromBase64(input.getBytes("UTF-8"), true);
        assertArrayEquals(expResult, result);

        input = "IcKnJFcpcmd1w59wNDNyaGdvw7YzICAgIDRsYWhyc2JqZ8O2b3EzNWV1bHJnaGZnbnEzbiA0d2N0\r\nw6R3LXNlZWpyZ2RuMzVsZSxyIGjDtjMgOC1pdGVucTVnNC5ldCBmZ2IsIG0=";
        expResult = ("!§$W)rgußp43rhgoö3    4lahrsbjgöoq35eulrghfgnq3n 4wctäw-seejrgdn35le,r hö3 8-itenq5g4.et fgb, m").getBytes("UTF-8");
        result = Base64.fromBase64(input.getBytes("UTF-8"), true);
        assertArrayEquals(expResult, result);

        input = "McO8MMOfMjM0ejU4aGxrbCBkdHUzw6Q5NWVyLWdqw6QzIHLDtnR1by3Cp1ElT0pFWkhqNHB3NWVy\r\nSEpUUSDCp3R1w6QzIDUtaiBGRyBBVWVjcHRoenU0M3AgdXdnI1FCRSA+U0o+RCBHanFow6Q2Zmpr\r\nZGEgw7ZoNGZpLSBhd3J6c8OkZ3ZrbnktZGogZ8O2w6Rhd3ogZzIgICAgaC00d3h1w5/DpDI0OXXD\r\nnzlwMjRpM3JldGtnbGZkdmMuIGZud0LDllVJRUw8UlNOIMOWT0ZQVzRSU8OWIEZGNCBSRzFFMjJG\r\nNEVSUEhUT1PDliAuIEpSR0xFVC5HRk5YSy1MT0RHIFVCSiA=";
        expResult = ("1ü0ß234z58hlkl dtu3ä95er-gjä3 rötuo-§Q%OJEZHj4pw5erHJTQ §tuä3 5-j FG AUecpthzu43p uwg#QBE >SJ>D Gjqhä6"
                    + "fjkda öh4fi- awrzsägvkny-dj göäawz g2    h-4wxußä249uß9p24i3retkglfdvc. fnwBÖUIEL<RSN ÖOFPW4RSÖ FF4 "
                    + "RG1E22F4ERPHTOSÖ . JRGLET.GFNXK-LODG UBJ ").getBytes("UTF-8");
        result = Base64.fromBase64(input.getBytes("UTF-8"), false);
        assertArrayEquals(expResult, result);
    }
}
