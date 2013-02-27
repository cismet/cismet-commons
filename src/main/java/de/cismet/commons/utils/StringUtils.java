/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.commons.utils;

/**
 * Various helpers for {@link String} literals.
 *
 * @author   schlob
 * @author   mscholl
 * @version  1.1
 */
public final class StringUtils {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of StringUtils.
     */
    private StringUtils() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Deletes Whitspaces in the <code>String</code>.
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
