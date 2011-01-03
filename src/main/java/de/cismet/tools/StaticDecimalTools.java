/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.tools;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten.hell@cismet.de
 * @version  $Revision$, $Date$
 */
public class StaticDecimalTools {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   d  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String round(final double d) {
        return round("0.00", d); // NOI18N
    }
    /**
     * DOCUMENT ME!
     *
     * @param   pattern  DOCUMENT ME!
     * @param   d        DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String round(final String pattern, final double d) {
        final double dd = ((double)(Math.round(d * 100))) / 100;
        final java.text.DecimalFormat myFormatter = new java.text.DecimalFormat(pattern);
        final java.text.DecimalFormatSymbols symbols = new java.text.DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');  // NOI18N
        symbols.setGroupingSeparator('.'); // NOI18N
        myFormatter.setDecimalFormatSymbols(symbols);
        return myFormatter.format(d);
    }
}
