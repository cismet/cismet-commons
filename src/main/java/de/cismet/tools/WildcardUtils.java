/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.tools;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class WildcardUtils {

    //~ Static fields/initializers ---------------------------------------------

    private static final String[] DEFAULT_KEYS = new String[] { "?", "*" };
    private static final String[] DEFAULT_VALUES = new String[] { ".", ".*" };

    //~ Methods ----------------------------------------------------------------
 
    /**
     * DOCUMENT ME!
     *
     * @param   rule  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String wildcardToRegex(final String rule) {
        return wildcardToRegex(rule, DEFAULT_KEYS, DEFAULT_VALUES);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   rule    DOCUMENT ME!
     * @param   keys    DOCUMENT ME!
     * @param   values  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String wildcardToRegex(final String rule, final String[] keys, final String[] values) {
        if ((keys == null) || (values == null) || (keys.length != values.length)) {
            return null;
        }
        final Map<String, String> wildcards = new HashMap<>();
        for (int index = 0; index < keys.length; index++) {
            final String key = keys[index];
            final String value = values[index];
            wildcards.put(key, value);
        }
        return wildcardToRegex(rule, wildcards);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   rule       DOCUMENT ME!
     * @param   wildcards  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String wildcardToRegex(final String rule, final Map<String, String> wildcards) {
        if (rule != null) {
            final StringBuffer regex = new StringBuffer();
            int lastCursor = -1;
            do {
                int cursor = rule.length();
                String regexWildcard = null;
                for (final Map.Entry<String, String> wildcard : wildcards.entrySet()) {
                    final int wildcardIndex = rule.indexOf(wildcard.getKey(), lastCursor + 1);
                    if ((wildcardIndex > -1) && (wildcardIndex < cursor)) {
                        cursor = wildcardIndex;
                        regexWildcard = wildcard.getValue();
                    }
                }
                regex.append(Pattern.quote(rule.substring(lastCursor + 1, cursor)));
                if (regexWildcard != null) {
                    regex.append(regexWildcard);
                }
                lastCursor = cursor;
            } while (lastCursor < rule.length());
            return regex.toString();
        } else {
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   rule  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Pattern compilePattern(final String rule) {
        return Pattern.compile(WildcardUtils.wildcardToRegex(rule));
    }

    /**
     * DOCUMENT ME!
     *
     * @param   test  DOCUMENT ME!
     * @param   rule  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static boolean testForMatch(final String test, final String rule) {
        return compilePattern(rule).matcher(test).matches();
    }
}
