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
public class StringMapsInt extends java.util.Hashtable {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new StringMapsInt object.
     */
    public StringMapsInt() {
        super();
    }

    /**
     * Creates a new StringMapsInt object.
     *
     * @param  initialCapacity  DOCUMENT ME!
     * @param  loadFactor       DOCUMENT ME!
     */
    public StringMapsInt(final int initialCapacity, final float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  descriptor  DOCUMENT ME!
     * @param  sqlID       DOCUMENT ME!
     */
    public void add(final String descriptor, final int sqlID) {
        super.put(descriptor, new Integer(sqlID));
    } // end add

    /**
     * /////////////////////////////////////////
     *
     * @param   descriptor  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception                       DOCUMENT ME!
     * @throws  java.lang.NullPointerException  DOCUMENT ME!
     */
    public int getIntValue(final String descriptor) throws Exception {
        if (super.containsKey(descriptor)) {
            final java.lang.Object candidate = super.get(descriptor);

            if (candidate instanceof Integer) {
                return ((Integer)candidate).intValue();
            }

            throw new java.lang.NullPointerException("Entry is not a Integer :" + descriptor); // NOI18N
        }                                                                                      // endif

        throw new java.lang.NullPointerException("No entry :" + descriptor); // NOI18N // to be changed in further
                                                                             // versions when exception concept is
                                                                             // accomplished
    }
    /**
     * ///// containsIntKey/////////////////////////////////
     *
     * @param   key  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean containsStringKey(final String key) {
        return super.containsKey(key);
    }
}
