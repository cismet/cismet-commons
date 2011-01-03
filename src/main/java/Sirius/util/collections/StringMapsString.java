/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package Sirius.util.collections;

/**
 * renames.
 *
 * @version  $Revision$, $Date$
 */
public class StringMapsString extends java.util.Hashtable {

    //~ Constructors -----------------------------------------------------------

    /**
     * ---------------------------------------------------------
     */
    public StringMapsString() {
        super();
    }

    /**
     * Creates a new StringMapsString object.
     *
     * @param  initialCapacity  DOCUMENT ME!
     */
    public StringMapsString(final int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * ---------------------------------------------------------
     *
     * @param  initialCapacity  DOCUMENT ME!
     * @param  loadFactor       DOCUMENT ME!
     */
    public StringMapsString(final int initialCapacity, final float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * -----------------------------------------------------------
     *
     * @param   descriptor  DOCUMENT ME!
     * @param   aString     DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public void add(final String descriptor, final String aString) throws Exception {
        super.put(descriptor, aString);

        if (!containsKey(descriptor)) {
            throw new Exception("Could not insert key :" + descriptor); // NOI18N
        }
    }                                                                   // end add

    /**
     * ---------------------------------------------------------
     *
     * @param   descriptor  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception                       DOCUMENT ME!
     * @throws  java.lang.NullPointerException  DOCUMENT ME!
     */
    public String getStringValue(final String descriptor) throws Exception {
        if (containsKey(descriptor)) {
            final java.lang.Object candidate = super.get(descriptor);

            if (candidate instanceof String) {
                return ((String)candidate);
            }

            throw new java.lang.NullPointerException("Entry is not a String :" + descriptor); // NOI18N
        }                                                                                     // endif

        throw new java.lang.NullPointerException("No entry :" + descriptor); // NOI18N
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
