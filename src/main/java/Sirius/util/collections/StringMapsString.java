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
     * Creates a new StringMapString object
     */
    public StringMapsString() {
        super();
    }

    /**
     * Creates a new StringMapsString object.
     *
     * @param  initialCapacity  Capacity when Object is created
     */
    public StringMapsString(final int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * Creates a new StringMapsString object.
     *
     * @param  initialCapacity  Capacity when Object is created
     * @param  loadFactor       buffer for capacity increase
     */
    public StringMapsString(final int initialCapacity, final float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Associates a <code>String</code> astring(value) to a <code>String</code> descriptor(key).
     *
     * @param   descriptor  key
     * @param   aString     value
     *
     * @throws  Exception  Contains no Key
     */
    public void add(final String descriptor, final String aString) throws Exception {
        super.put(descriptor, aString);

        if (!containsKey(descriptor)) {
            throw new Exception("Could not insert key :" + descriptor); // NOI18N
        }
    }                                                                   // end add

    /**
     * Getter for the Value as a <code>String</code>
     *
     * @param   descriptor  key
     *
     * @return  StringValue
     *
     * @throws  Exception                       
     * @throws  java.lang.NullPointerException  "Entry is no String" or "No Entry"
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
     * Tests if the specified object is a key in <code>StringMapsString</code>
     *
     * @param   key  possible key
     *
     * @return  <code>true</code>, if the object is a key in <code>StringMapsString</code>
     * 
     * @see #containsKey(java.lang.Object) 
     */
    public boolean containsStringKey(final String key) {
        return super.containsKey(key);
    }
}
