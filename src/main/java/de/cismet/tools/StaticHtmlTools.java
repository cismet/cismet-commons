/*
 * StaticHtmlTools.java
 * Copyright (C) 2005 by:
 *
 *----------------------------
 * cismet GmbH
 * Goebenstrasse 40
 * 66117 Saarbruecken
 * http://www.cismet.de
 *----------------------------
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *----------------------------
 * Author:
 * thorsten.hell@cismet.de
 *----------------------------
 *
 * Created on 20. April 2006, 15:22
 *
 */

package de.cismet.tools;

/**
 *
 * @author thorsten.hell@cismet.de
 */
public class StaticHtmlTools {
    
    public static String convertHTTPReferences(String newString) {
        int hrefStartPos;
        int hrefEndPos;
        String hrefEndChars;
        String hrefString;
        char zero=0;
        char nine=9;
        char ten=10;
        char thirteen=13;
        hrefEndChars = " " + zero + nine + ten + thirteen;
        hrefStartPos =  newString.indexOf("http://");
        
        
        while (hrefStartPos > 0) {
            int startPos = newString.indexOf( "<",hrefStartPos);
            int endPos = newString.indexOf(">",hrefStartPos);
//
            if ((endPos == -1) || ((endPos > startPos) && (startPos > 0))) {
                
//			'** if we're not inside a <tag>, then convert the link
                hrefEndPos = hrefStartPos + 7;
//
//			'** find the end of the http:// reference
                while (hrefEndPos <= newString.length()) {
//
                    if (hrefEndChars.indexOf(newString.charAt(hrefEndPos))>=0) {
                        break;
                    }
                    hrefEndPos++;
                }
//
//			'** make sure that the character at the end of the http:// reference
//			'** isn't really some punctuation that's probably not part of the URL
//			'** (these characters aren't strictly illegal, but we're making some
//			'** educated guesses based on common URL and sentence structure)
                
                while (hrefEndPos > hrefStartPos) {
                    if (".,?!&:-()[]<>{}'\"".indexOf(newString.charAt(hrefEndPos-1))==-1) {
                        break;
                    }
                    hrefEndPos--;
                }
                
                hrefString=newString.substring(hrefStartPos, hrefEndPos);
                newString=newString.substring(0,hrefStartPos ) + "<a href=\"" + hrefString + "\">" + hrefString +"</a>" + newString.substring(hrefStartPos + hrefString.length());
                
                hrefEndPos += ("<a href='"+hrefString+"'></a>").length();
            } else if ((endPos < startPos)&& (endPos>0)) {
//		Elseif (endPos < startPos) And (endPos > 0) Then
//			'** if we're inside a tag, assume it's an <a href> tag, and skip
//			'** to the closing </a> tag (so we don't accidentally double-link
//			'** something like <a href="http://blah">http://blah</a>)
                hrefEndPos = newString.indexOf("</a>",endPos)+5;
//			hrefEndPos = Instr(endPos, newString, "</a>", 5)
                if (hrefEndPos == 4) {
                    hrefEndPos = newString.length();
                }
            } else {
                hrefEndPos = endPos;
            }
            
            hrefStartPos = newString.indexOf("http://",hrefEndPos);
        }
        
        return newString;
    }
    
    
    public static String stripMetaTag(String text) {
        // String used for searching, comparison and indexing
        String textUpperCase = text.toUpperCase();
        
        int indexHead = textUpperCase.indexOf("<META ");
        int indexMeta = textUpperCase.indexOf("<META ");
        int indexBody = textUpperCase.indexOf("<BODY ");
        
        // Not found or meta not inside the head nothing to strip...
        if (indexMeta == -1 || indexMeta > indexHead && indexMeta < indexBody)
            return text;
        
        // Find end of meta tag text.
        int indexHeadEnd = textUpperCase.indexOf(">", indexMeta);
        
        // Strip meta tag text
        return text.substring(0, indexMeta-1) + text.substring(indexHeadEnd+1);
    }
    
    public static void main(String[] args) {
        String test="<head>test http://www.google.de?quiery <a href=\"http://www.wer.de/\">wer</a></head>";
        System.out.println(convertHTTPReferences(test));
    }
    
    
    public static String stringToHTMLString(String string) {
    if(string == null){
        return null;
    }
    StringBuffer sb = new StringBuffer(string.length());
    // true if last char was blank
    boolean lastWasBlankChar = false;
    int len = string.length();
    char c;

    for (int i = 0; i < len; i++)
        {
        c = string.charAt(i);
        if (c == ' ') {
            // blank gets extra work,
            // this solves the problem you get if you replace all
            // blanks with &nbsp;, if you do that you loss 
            // word breaking
            if (lastWasBlankChar) {
                lastWasBlankChar = false;
                sb.append("&nbsp;");
                }
            else {
                lastWasBlankChar = true;
                sb.append(' ');
                }
            }
        else {
            lastWasBlankChar = false;
            //
            // HTML Special Chars
            if (c == '"')
                sb.append("&quot;");
            else if (c == '&')
                sb.append("&amp;");
            else if (c == '<')
                sb.append("&lt;");
            else if (c == '>')
                sb.append("&gt;");
            else if (c == '\n')
                // Handle Newline
                //sb.append("&lt;/br&gt;");
                sb.append("\n");
            else {
                int ci = 0xffff & c;
                if (ci < 160 )
                    // nothing special only 7 Bit
                    sb.append(c);
                else {
                    // Not 7 Bit use the unicode system
                    sb.append("&#");
                    sb.append(new Integer(ci).toString());
                    sb.append(';');
                    }
                }
            }
        }
    return sb.toString();
}
}
