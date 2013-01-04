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
     * @param  initialCapacity  Capacity when Object is created
     * @param  loadFactor       buffer for capacity increase
     */
    public StringMapsInt(final int initialCapacity, final float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Associates <code>Integer</code> sq1ID(<code>value</code>) to a <code>String</code> descriptor(<code>key</code>).
     *
     * @param  descriptor  key
     * @param  sqlID       value
     * 
     * @see #put(java.lang.Object, java.lang.Object) 
     */
    public void add(final String descriptor, final int sqlID) {
        super.put(descriptor, new Integer(sqlID));
    } // end add

    /**
     * Getter for the Value as a <code>Integer</code>
     *
     * @param   descriptor  descriptor
     *
     * @return  IntValue
     *
     * @throws  Exception                       DOCUMENT ME!
     * @throws  java.lang.NullPointerException  "Entry is not a Integer" or "No entry"
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
     * Tests if the specified object is a key in <code>StringMapsInt</code>
     *
     * @param   key possible key
     *
     * @return  <code>true</code>, if the object is a key in <code>StringMapsInt</code>
     * 
     * @see #containsKey(java.lang.Object) 
     */
    public boolean containsStringKey(final String key) {
        return super.containsKey(key);
    }
}
