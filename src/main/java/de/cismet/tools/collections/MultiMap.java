/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.tools.collections;

import java.util.*;
/**
 * //key=idnetifier;value=LinkedList.
 *
 * @version     $Revision$, $Date$
 * @deprecated  error-prone
 */
public class MultiMap extends LinkedHashMap {

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
     * HELL.
     *
     * @param   key    DOCUMENT ME!
     * @param   value  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean contains(final Object key, final Object value) {
        return containsKey(key) && ((SyncLinkedList)get(key)).contains(value);
    }
    /**
     * HELL.
     *
     * @param   key  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Iterator iterator(final String key) {
        if (containsKey(key)) {
            return ((SyncLinkedList)get(key)).iterator();
        } else {
            return null;
        }
    }

////////////////////////////////////////////////////////

    /**
     * DOCUMENT ME!
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

            final boolean listElementRemoved = list.remove(value);

            if (list.isEmpty()) {
                this.remove(key);
            }

            // Iterator iter = list.iterator();
            //
            // while(iter.hasNext())
            // if(value.equals(iter.next()) )
            // return list.remove(value);

            return listElementRemoved;
        } else {
            return false;
        }
    }
} // end class

//////////////////////////////////////////////////////////////////////////
