/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.tools;

/**
 * Equals helper operations.
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class Equals {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Equals object.
     */
    private Equals() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Tests if both objects are equals whereas they are considered equal if they are both <code>null</code>, too.
     *
     * @param   o1  object one
     * @param   o2  object two
     *
     * @return  <code>true</code> if o1 == null and o2 == null or if o1.equals(o2), <code>false</code> otherwise
     */
    public static boolean nullEqual(final Object o1, final Object o2) {
        if (xor(o1, o2)) {
            return false;
        } else if (allNull(o1, o2)) {
            return true;
        } else {
            return o1.equals(o2);
        }
    }

    /**
     * Tests if one of the objects is <code>null</code> and the other is not <code>null</code>.
     *
     * @param   o1  object one
     * @param   o2  object two
     *
     * @return  <code>true</code> if object one is <code>null</code> and object two is not <code>null</code> and vice
     *          versa, <code>false</code> if both objects are <code>null</code> or both objects are not <code>
     *          null</code>.
     */
    public static boolean xor(final Object o1, final Object o2) {
        return ((o1 == null) && (o2 != null)) || ((o1 != null) && (o2 == null));
    }

    /**
     * Tests if all given objects are <code>null.</code>
     *
     * @param   obs  the objects
     *
     * @return  <code>true</code> if all given objects are null, <code>false</code> otherwise
     */
    public static boolean allNull(final Object... obs) {
        for (final Object o : obs) {
            if (o != null) {
                return false;
            }
        }

        return true;
    }
}
