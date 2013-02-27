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

    //J-
    /** All the java keywords + 'true', 'false' 'null' literals **/
    public static final String[] KEYWORDS = {
        "abstract", "continue", "for",        "new" ,      "switch",        // NOI18N
        "assert",   "default",  "goto",       "package",   "synchronized",  // NOI18N
        "boolean",  "do",       "if",         "private",   "this",          // NOI18N
        "break",    "double",   "implements", "protected", "throw",         // NOI18N
        "byte",     "else",     "import",     "public",    "throws",        // NOI18N
        "case",     "enum",     "instanceof", "return",    "transient",     // NOI18N
        "catch",    "extends",  "int",        "short",     "try",           // NOI18N
        "char",     "final",    "interface",  "static",    "void",          // NOI18N
        "class",    "finally",  "long",       "strictfp",  "volatile",      // NOI18N
        "const",    "float",    "native",     "super",     "while",         // NOI18N

        // although this are no keywords but literals they are forbidden in identifiers, too
        "true", "false", "null"                                             // NOI18N
    }; //J+

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

    /**
     * Checks a given string if it is a java keyword. Note that 'true', 'false' and 'null' are also considered keywords
     * although they are really plain literals but with a special meaning. <code>null</code> is not considered a java
     * keyword.
     *
     * @param   identifier  the string to check if it is a java keyword
     *
     * @return  true if the given string is a java keyword, false otherwise
     *
     * @see     #KEYWORDS
     */
    public static boolean isKeyword(final String identifier) {
        if ((identifier == null) || identifier.isEmpty()) {
            return false;
        }

        for (int i = 0; i < KEYWORDS.length; ++i) {
            if (KEYWORDS[i].equals(identifier)) {
                return true;
            }
        }

        return false;
    }
}
