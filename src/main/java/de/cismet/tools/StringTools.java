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
 * DOCUMENT ME!
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
     * DOCUMENT ME!
     *
     * @param   string  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
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
