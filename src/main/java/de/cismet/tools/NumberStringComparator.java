/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * NumberStringComparator.java
 *
 * Created on 12. Januar 2005, 11:31
 */
package de.cismet.tools;

/**
 * Comperator for Objects, which can contain Numbers and Strings.<br>
 * It will be sorted in the following form:<br>
 * 1<br>
 * 2<br>
 * 3<br>
 * 4<br>
 * 5<br>
 * 6<br>
 * 7<br>
 * 8<br>
 * 9<br>
 * 10<br>
 * 11<br>
 * 12<br>
 * A<br>
 * B<br>
 * C<br>
 * AA<br>
 * AB<br>
 * BBB<br>
 * BBC<br>
 * ...<br>
 *
 * @author   hell
 * @version  $Revision$, $Date$
 */
public class NumberStringComparator implements java.util.Comparator {

    //~ Instance fields --------------------------------------------------------

    private boolean numbersFirst = true;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of NumberStringComparator.
     */
    public NumberStringComparator() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Compare Methode
     *
     * @param   o1  Object one
     * @param   o2  object two
     *
     * @return  value of the Comparation
     */
    @Override
    public int compare(final Object o1, final Object o2) {
        final String s1 = o1.toString();
        final String s2 = o2.toString();
        Double d1 = null;
        Double d2 = null;
        try {
            d1 = new Double(s1.trim());
        } catch (Exception e) {
        }

        try {
            d2 = new Double(s2.trim());
        } catch (Exception e) {
        }

        if ((d1 == null) && (d2 == null)) {
            if (s1.length() > s2.length()) {
                return 1;
            } else if (s1.length() < s2.length()) {
                return -1;
            } else {
                return s1.compareTo(s2);
            }
        } else if ((d1 == null) && (d2 != null)) {
            if (numbersFirst) {
                return 1;
            } else {
                return -1;
            }
        } else if ((d1 != null) && (d2 == null)) {
            if (numbersFirst) {
                return -1;
            } else {
                return 1;
            }
        } else /*if (d1!=null&&d2!=null)*/ {
            return d1.compareTo(d2);
        }
    }
}
