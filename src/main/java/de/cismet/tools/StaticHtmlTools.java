/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.tools;

import java.io.UnsupportedEncodingException;

import java.net.URLEncoder;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten.hell@cismet.de
 * @version  $Revision$, $Date$
 */
public class StaticHtmlTools {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   newString  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String convertHTTPReferences(String newString) {
        int hrefStartPos;
        int hrefEndPos;
        final String hrefEndChars;
        String hrefString;
        final char zero = 0;
        final char nine = 9;
        final char ten = 10;
        final char thirteen = 13;
        hrefEndChars = " " + zero + nine + ten + thirteen; // NOI18N
        hrefStartPos = newString.indexOf("http://");       // NOI18N

        while (hrefStartPos > 0) {
            final int startPos = newString.indexOf("<", hrefStartPos); // NOI18N
            final int endPos = newString.indexOf(">", hrefStartPos);   // NOI18N
//
            if ((endPos == -1) || ((endPos > startPos) && (startPos > 0))) {
//                      '** if we're not inside a <tag>, then convert the link
                hrefEndPos = hrefStartPos + 7;
//
//                      '** find the end of the http:// reference
                while (hrefEndPos <= newString.length()) {
//
                    if (hrefEndChars.indexOf(newString.charAt(hrefEndPos)) >= 0) {
                        break;
                    }
                    hrefEndPos++;
                }
//
//                      '** make sure that the character at the end of the http:// reference
//                      '** isn't really some punctuation that's probably not part of the URL
//                      '** (these characters aren't strictly illegal, but we're making some
//                      '** educated guesses based on common URL and sentence structure)

                while (hrefEndPos > hrefStartPos) {
                    if (".,?!&:-()[]<>{}'\"".indexOf(newString.charAt(hrefEndPos - 1)) == -1) { // NOI18N
                        break;
                    }
                    hrefEndPos--;
                }

                hrefString = newString.substring(hrefStartPos, hrefEndPos);
                newString = newString.substring(0, hrefStartPos) + "<a href=\"" + hrefString + "\">" + hrefString
                            + "</a>" + newString.substring(hrefStartPos + hrefString.length()); // NOI18N

                hrefEndPos += ("<a href='" + hrefString + "'></a>").length(); // NOI18N
            } else if ((endPos < startPos) && (endPos > 0)) {
//              Elseif (endPos < startPos) And (endPos > 0) Then
//                      '** if we're inside a tag, assume it's an <a href> tag, and skip
//                      '** to the closing </a> tag (so we don't accidentally double-link
//                      '** something like <a href="http://blah">http://blah</a>)
                hrefEndPos = newString.indexOf("</a>", endPos) + 5; // NOI18N
//                      hrefEndPos = Instr(endPos, newString, "</a>", 5)
                if (hrefEndPos == 4) {
                    hrefEndPos = newString.length();
                }
            } else {
                hrefEndPos = endPos;
            }

            hrefStartPos = newString.indexOf("http://", hrefEndPos); // NOI18N
        }

        return newString;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   text  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String stripMetaTag(final String text) {
        // String used for searching, comparison and indexing
        final String textUpperCase = text.toUpperCase();

        final int indexHead = textUpperCase.indexOf("<META "); // NOI18N
        final int indexMeta = textUpperCase.indexOf("<META "); // NOI18N
        final int indexBody = textUpperCase.indexOf("<BODY "); // NOI18N

        // Not found or meta not inside the head nothing to strip...
        if ((indexMeta == -1) || ((indexMeta > indexHead) && (indexMeta < indexBody))) {
            return text;
        }

        // Find end of meta tag text.
        final int indexHeadEnd = textUpperCase.indexOf(">", indexMeta); // NOI18N

        // Strip meta tag text
        return text.substring(0, indexMeta - 1) + text.substring(indexHeadEnd + 1);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  args  DOCUMENT ME!
     */
    public static void main(final String[] args) {
        final String test = "<head>test http://www.google.de?quiery <a href=\"http://www.wer.de/\">wer</a></head>"; // NOI18N
        System.out.println(convertHTTPReferences(test));
        System.out.println(encodeURLParameter("Nr. E-lala 99 ? ab & null"));
    }

    /**
     * DOCUMENT ME!
     *
     * @param   string  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String stringToHTMLString(final String string) {
        if (string == null) {
            return null;
        }
        final StringBuffer sb = new StringBuffer(string.length());
        // true if last char was blank
        boolean lastWasBlankChar = false;
        final int len = string.length();
        char c;

        for (int i = 0; i < len; i++) {
            c = string.charAt(i);
            if (c == ' ') {
                // blank gets extra work,
                // this solves the problem you get if you replace all
                // blanks with &nbsp;, if you do that you loss
                // word breaking
                if (lastWasBlankChar) {
                    lastWasBlankChar = false;
                    sb.append("&nbsp;"); // NOI18N
                } else {
                    lastWasBlankChar = true;
                    sb.append(' ');
                }
            } else {
                lastWasBlankChar = false;
                //
                // HTML Special Chars
                if (c == '"') {          // NOI18N
                    sb.append("&quot;"); // NOI18N
                } else if (c == '&') {   // NOI18N
                    sb.append("&amp;");  // NOI18N
                } else if (c == '<') {   // NOI18N
                    sb.append("&lt;");   // NOI18N
                } else if (c == '>') {   // NOI18N
                    sb.append("&gt;");   // NOI18N
                } else if (c == '\n') {  // NOI18N
                    // Handle Newline
                    // sb.append("&lt;/br&gt;");
                    sb.append("\n"); // NOI18N
                } else {
                    final int ci = 0xffff & c;
                    if (ci < 160) {
                        // nothing special only 7 Bit
                        sb.append(c);
                    } else {
                        // Not 7 Bit use the unicode system
                        sb.append("&#"); // NOI18N
                        sb.append(new Integer(ci).toString());
                        sb.append(';');  // NOI18N
                    }
                }
            }
        }
        return sb.toString();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   parameter  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String encodeURLParameter(final String parameter) {
        try {
            if (parameter == null) {
                return null;
            }
            final String encodedURL = URLEncoder.encode(parameter, "UTF-8");

            if (encodedURL != null) {
                // replace all + with %20 because the method URLEncoder.encode() replaces all spaces with '+', but
                // the web dav client interprets %20 as a space.
                return encodedURL.toString().replaceAll("\\+", "%20");
            } else {
                return "";
            }
        } catch (final UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return parameter;
    }
}
