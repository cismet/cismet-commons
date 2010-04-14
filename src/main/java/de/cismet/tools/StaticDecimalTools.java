/*
 * StaticDecimalTools.java
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
 * Created on 21. M\u00E4rz 2006, 15:05
 *
 */

package de.cismet.tools;

/**
 *
 * @author thorsten.hell@cismet.de
 */
public class StaticDecimalTools {
    
    public static String round(double d)   {
        return round("0.00",d);  // NOI18N
    }
    public static String round(String pattern,double d)   {
        double dd=((double)(Math.round(d*100)))/100;
        java.text.DecimalFormat myFormatter = new java.text.DecimalFormat(pattern);
        java.text.DecimalFormatSymbols symbols = new java.text.DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');  // NOI18N
        symbols.setGroupingSeparator('.');  // NOI18N
        myFormatter.setDecimalFormatSymbols(symbols);
        return myFormatter.format(d);
    }
   
    
    
}
