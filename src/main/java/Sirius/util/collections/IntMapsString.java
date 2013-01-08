/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package Sirius.util.collections;

import java.util.Hashtable;

/**
 * Modified {@link Hashtable}, which maps <code>Integer</code> to <code>String</code>.
 *
 * <ul>
 *   <li><code>Key</code> - <code>Integer</code></li>
 *   <li><code>Value</code> - <code>String</code></li>
 * </ul>
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
     * @param  initialCapacity  Capacity when Object is created
     * @param  loadFactor       buffer for capacity increase
     *
     * @see    Hashtable
     */
    IntMapsString(final int initialCapacity, final float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Associates a <code>Integer</code> id(key) to a <code>String</code> astring(value).
     *
     * @param  id       key
     * @param  aString  value
     *
     * @see    #put(java.lang.Object, java.lang.Object)
     */
    public void add(final int id, final String aString) {
        super.put(new Integer(id), aString);
    } // end add

    /**
     * Getter for the Value as a <code>String.</code>
     *
     * @param   id  key
     *
     * @return  <code>Stringvalue</code>
     *
     * @throws  Exception                       throws Exeption if anything went wrong
     * @throws  java.lang.NullPointerException  "Entry is not a String" if key not a String or "No entry" if <code>
     *                                          id<\code> has no entry</code>
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
     * Tests whether the specified object is a key in <code>IntMapsString</code> or not.
     *
     * @param   key  possible key
     *
     * @return  <code>true</code>, if the object is a key in <code>IntMapsString</code>
     *
     * @see     #containsKey(java.lang.Object)
     */
    public boolean containsIntKey(final int key) {
        return super.containsKey(new Integer(key));
    }
}
