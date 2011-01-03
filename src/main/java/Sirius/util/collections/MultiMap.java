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
 * //key=idnetifier;value=LinkedList.
 *
 * @version  $Revision$, $Date$
 */
public class MultiMap extends HashMap {

    //~ Constructors -----------------------------------------------------------

    /**
     * //////////////////////////////////////////////
     */
    public MultiMap() {
        this(10);
    }
    /**
     * //////////////////////////////////////////////
     *
     * @param  size  DOCUMENT ME!
     */
    public MultiMap(final int size) {
        super(size);
    }

    //~ Methods ----------------------------------------------------------------

    /////////////////////////////////////////////////

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
     * //////////////////////////////////////////////////////
     *
     * @param   key    DOCUMENT ME!
     * @param   value  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
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
} // end class

//////////////////////////////////////////////////////////////////////////
