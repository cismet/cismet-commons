/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.tools;

import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.*;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public class JnlpToolsTest {

    //~ Static fields/initializers ---------------------------------------------

    private static final String USER_LANGUAGE = "user.language";
    private static final String USER_COUNTRY = "user.country";
    private static final String USER_VARIANT = "user.variant";

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new JnlpToolsTest object.
     */
    public JnlpToolsTest() {
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
     */
    @Test
    public void testAdjustDefaultLocale() {
        System.out.println("TEST " + getCurrentMethodName());

        final Locale def = Locale.getDefault();
        JnlpTools.adjustDefaultLocale();
        assertEquals(def, Locale.getDefault());
        Locale.setDefault(def);
        
        final String defL = def.getLanguage();
        final String defC = def.getCountry();
        final String defV = def.getVariant();

        final String userL = System.getProperty(USER_LANGUAGE);
        final String userC = System.getProperty(USER_COUNTRY);
        final String userV = System.getProperty(USER_VARIANT);

        System.setProperty(USER_LANGUAGE, "");
        JnlpTools.adjustDefaultLocale();
        assertEquals(def, Locale.getDefault());
        if (userL == null) {
            System.clearProperty(USER_LANGUAGE);
        } else {
            System.setProperty(USER_LANGUAGE, userL);
        }
        Locale.setDefault(def);

        System.setProperty(USER_COUNTRY, "");
        JnlpTools.adjustDefaultLocale();
        assertEquals(def, Locale.getDefault());
        if (userC == null) {
            System.clearProperty(USER_COUNTRY);
        } else {
            System.setProperty(USER_COUNTRY, userC);
        }
        Locale.setDefault(def);

        System.setProperty(USER_VARIANT, "");
        JnlpTools.adjustDefaultLocale();
        assertEquals(def, Locale.getDefault());
        if (userV == null) {
            System.clearProperty(USER_VARIANT);
        } else {
            System.setProperty(USER_VARIANT, userV);
        }
        Locale.setDefault(def);

        System.setProperty(USER_VARIANT, "abc");
        JnlpTools.adjustDefaultLocale();
        assertEquals(new Locale(defL, defC, "abc"), Locale.getDefault());
        if (userV == null) {
            System.clearProperty(USER_VARIANT);
        } else {
            System.setProperty(USER_VARIANT, userV);
        }
        Locale.setDefault(def);

        System.setProperty(USER_COUNTRY, "ab");
        JnlpTools.adjustDefaultLocale();
        assertEquals(new Locale(defL, "ab", defV), Locale.getDefault());
        if (userC == null) {
            System.clearProperty(USER_COUNTRY);
        } else {
            System.setProperty(USER_COUNTRY, userC);
        }
        Locale.setDefault(def);

        System.setProperty(USER_LANGUAGE, "ab");
        JnlpTools.adjustDefaultLocale();
        assertEquals(new Locale("ab", defC, defV), Locale.getDefault());
        if (userL == null) {
            System.clearProperty(USER_LANGUAGE);
        } else {
            System.setProperty(USER_LANGUAGE, userL);
        }
        Locale.setDefault(def);

        System.setProperty(USER_VARIANT, "abc");
        System.setProperty(USER_COUNTRY, "ab");
        System.setProperty(USER_LANGUAGE, "ab");
        JnlpTools.adjustDefaultLocale();
        assertEquals(new Locale("ab", "ab", "abc"), Locale.getDefault());
        if (userL == null) {
            System.clearProperty(USER_LANGUAGE);
        } else {
            System.setProperty(USER_LANGUAGE, userL);
        }
        if (userC == null) {
            System.clearProperty(USER_COUNTRY);
        } else {
            System.setProperty(USER_COUNTRY, userC);
        }
        if (userV == null) {
            System.clearProperty(USER_VARIANT);
        } else {
            System.setProperty(USER_VARIANT, userV);
        }
        Locale.setDefault(def);
    }
}
