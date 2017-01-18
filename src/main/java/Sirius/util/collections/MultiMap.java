/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package Sirius.util.collections;

import java.util.*;
/**
 * Modified {@link HashMap}, which makes it possible to map Keys to multiple Values.
 *
 * <p>key=idnetifier;value=LinkedList.</p>
 *
 * @version  $Revision$, $Date$
 */
public class MultiMap extends HashMap {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new Multimap Object with size 10.
     */
    public MultiMap() {
        this(10);
    }
    /**
     * Creates new Multimap Object with specified size.
     *
     * @param  size  size
     */
    public MultiMap(final int size) {
        super(size);
    }

    //~ Methods ----------------------------------------------------------------

    /////////////////////////////////////////////////

    /**
     * Assosiates <code>value</code> with <code>key</code> in this <code>map</code>. Instead of replace the previous
     * value for a key,it is possible to have multiple values for one key possible.
     *
     * @param   key    key
     * @param   value  value
     *
     * @return  <code>Null</code>
     */

    @Override
    public Object put(final Object key, final Object value) {
        SyncLinkedList list = null;

        if (containsKey(key))
        // anh\u00E4ngen an bestehende liste
        {
            list = (SyncLinkedList)get(key);

            list.add(value);
        } else
        // keine existierende liste
        {
            list = new SyncLinkedList();
            list.add(value);
            super.put(key, list);
        }

        // no item is replaced
        return null;
    } // end add

    //////////////////////////////////////////////////////////////
    /**
     * Appends the values of the specified <code>map</code> to this <code>map</code>. Instead of replace the previous
     * value for a key, it is possible multiple values for one key possible.
     *
     * @param  t  map
     */

    @Override
    public void putAll(final Map t) {
        final Iterator i = t.entrySet().iterator();

        while (i.hasNext()) {
            final Map.Entry e = (Map.Entry)i.next();

            final Object val = e.getValue();
            final Object key = e.getKey();

            // wenn mehrere Eintr\u00E4ge zu einem key
            if (val instanceof SyncLinkedList) {
                final Iterator iter = ((SyncLinkedList)val).iterator();

                while (iter.hasNext()) {
                    put(key, iter.next());
                }
            } else {
                put(key, val);
            }
        }
    }

    /**
     * Removes specified <code>value</code> associeated with specified <code>key</code>.
     *
     * @param   key    key whose mapping is to be removed
     * @param   value  mapping to be removed
     *
     * @return  true, if remove suceeded
     */

    //J-
    public boolean remove(final Object key, final Object value) {
        SyncLinkedList list = null;

        if (containsKey(key)) {
            list = (SyncLinkedList)get(key);

            return list.remove(value);

            // Iterator iter = list.iterator();
            //
            // while(iter.hasNext())
            // if(value.equals(iter.next()) )
            // return list.remove(value);

        } else {
            return false;
        }
    }
    //J+
} // end class

//////////////////////////////////////////////////////////////////////////
