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
     * Deletes all white-spaces from the given string.
     *
     * @param   string  the string that whose white-spaces shall be removed
     *
     * @return  a string without white-spaces or <code>null</code> if the given string was null
     */
    public static String deleteWhitespaces(final String string) {
        if (string == null) {
            return null;
        }

        return string.replaceAll("\\s", ""); // NOI18N
    }
}
