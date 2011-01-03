/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package Sirius.util.collections;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public class IntMapsString extends java.util.Hashtable {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new IntMapsString object.
     */
    IntMapsString() {
        super();
    }

    /**
     * Creates a new IntMapsString object.
     *
     * @param  initialCapacity  DOCUMENT ME!
     * @param  loadFactor       DOCUMENT ME!
     */
    IntMapsString(final int initialCapacity, final float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  id       DOCUMENT ME!
     * @param  aString  DOCUMENT ME!
     */
    public void add(final int id, final String aString) {
        super.put(new Integer(id), aString);
    } // end add

    /**
     * DOCUMENT ME!
     *
     * @param   id  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception                       DOCUMENT ME!
     * @throws  java.lang.NullPointerException  DOCUMENT ME!
     */
    public String getStringValue(final int id) throws Exception {
        final Integer key = new Integer(id);

        if (super.containsKey(key)) {
            final java.lang.Object candidate = super.get(key);

            if (candidate instanceof java.lang.String) {
                return ((String)candidate);
            }

            throw new java.lang.NullPointerException("Entry is not a String :" + id); // NOI18N
        }                                                                             // endif

        throw new java.lang.NullPointerException("No entry :" + id); // NOI18N // to be changed in further versions
                                                                     // when exception concept is accomplished
    }
    /**
     * ///// containsIntKey/////////////////////////////////
     *
     * @param   key  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean containsIntKey(final int key) {
        return super.containsKey(new Integer(key));
    }
}
