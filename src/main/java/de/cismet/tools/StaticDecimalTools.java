/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.tools;

/**
 * StaticDecimalTools. Rounds <code>Double</code>.
 *
 * @author   thorsten.hell@cismet.de
 * @version  $Revision$, $Date$
 */
public class StaticDecimalTools {

    //~ Methods ----------------------------------------------------------------

    /**
     * Rounds Doubles to the Form "0.00"
     *
     * @param   d  <code>Double</code> which should be rounded
     *
     * @return  the rounded <code>Double</code>
     *
     * @see     #round(java.lang.String, double)
     */
    public static String round(final double d) {
        return round("0.00", d); // NOI18N
    }
    /**
     * Rounds the Doubles to a specified Form.
     *
     * @param   pattern  Form of the rounded <code>Double</code> as <code>String</code>
     * @param   d        <code>Double</code> which should be rounded
     *
     * @return  the rounded <code>Double</code>
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
