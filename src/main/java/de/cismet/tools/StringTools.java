/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * StringTools.java
 *
 * Created on 4. Mai 2004, 14:05
 */
package de.cismet.tools;

/**
 * String Tools
 *
 * @author   schlob
 * @version  $Revision$, $Date$
 */
public final class StringTools {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of StringTools.
     */
    private StringTools() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Deletes Whitspaces in the <code>String</code>
     *
     * @param   string  <code>String</code>, which is going to get cleaned
     *
     * @return  cleaned <code>String</code>
     */
    public static String deleteWhitespaces(final String string) {
        if (string == null) {
            return string;
        }

        String clean = ""; // NOI18N

        final char[] chars = string.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            if (!Character.isWhitespace(chars[i])) {
                clean += chars[i];
            }
        }

        return clean;
    }
}
