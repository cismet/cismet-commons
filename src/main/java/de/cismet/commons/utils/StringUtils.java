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

    /**
     * This operation turns the given string in a valid java package by eliminating all illegal characters at which the
     * dot character <code>'.'</code> is also considered illegal at the beginning and at the end of the string.
     * Additionally it checks if the resulting package identifiers are java keywords and appends a '_' if so. If the
     * given string is <code>null</code> then <code>null</code> is returned. If the given string is empty an empty
     * string is returned.<br/>
     * <br/>
     * Examples:<br/>
     * <br/>
     * &nbsp;'my.good.pakkage'&nbsp;->&nbsp;'my.good.pakkage'<br/>
     * &nbsp;'my.good.package'&nbsp;->&nbsp;'my.good.package_'<br/>
     * &nbsp;'my.--.9pakkage'&nbsp;->&nbsp;'my.pakkage'<br/>
     * &nbsp;'.3true..pakkages.'&nbsp;->&nbsp;'true_.pakkages'<br/>
     * &nbsp;'---'&nbsp;->&nbsp;''<br/>
     *
     * @param   pakkage  the string to turn into a valid java package name
     *
     * @return  a valid java package name
     *
     * @see     #isKeyword(java.lang.String)
     */
    public static String toPackage(final String pakkage) {
        if (pakkage == null) {
            return null;
        }

        //J-  trim leading and trailing illegal characters
        int begin = 0;
        int end = pakkage.length() - 1;
        for(; begin < end  && !Character.isJavaIdentifierStart(pakkage.charAt(begin)) ; ++begin) {}
        for(; end >= begin && !Character.isJavaIdentifierPart(pakkage.charAt(end))    ; --end)   {} //J+

        final String trimmed = pakkage.substring(begin, end + 1);

        final StringBuilder result = new StringBuilder(trimmed.length());

        int idBegin = 0;
        boolean first = true;
        for (int i = 0; i < trimmed.length(); ++i) {
            final char current = trimmed.charAt(i);
            if (Character.isJavaIdentifierPart(current)) {
                if (first && !Character.isJavaIdentifierStart(current)) {
                    // ignore
                } else {
                    if (first) {
                        idBegin = result.length();
                        first = false;
                    }

                    result.append(current);
                }
            } else if (('.' == current) && ('.' != result.charAt(result.length() - 1))) {
                // the dot is ignored if there has been a dot just before
                final String idendifier = result.substring(idBegin);
                if (isKeyword(idendifier)) {
                    result.append('_');
                }

                result.append(current);
                first = true;
            }
        }

        // trim the dot if it appears at the very end of the result
        final int lastIndex = result.length() - 1;
        if ((lastIndex > 0) && ('.' == result.charAt(lastIndex))) {
            result.deleteCharAt(lastIndex);
        }

        final String idendifier = result.substring(idBegin);
        if (isKeyword(idendifier)) {
            result.append('_');
        }

        return result.toString();
    }
}
