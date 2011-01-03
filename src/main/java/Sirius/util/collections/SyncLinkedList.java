/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * SyncLinkedList.java
 *
 * Created on 25. Mai 2004, 12:58
 */
package Sirius.util.collections;

import java.util.*;
/**
 * DOCUMENT ME!
 *
 * @author   schlob
 * @version  $Revision$, $Date$
 */

public class SyncLinkedList extends LinkedList {

    //~ Methods ----------------------------------------------------------------

    @Override
    public synchronized boolean add(final Object o) {
        if (!contains(o)) {
            return super.add(o);
        } else {
            return true;
        }
    }

    @Override
    public synchronized boolean isEmpty() {
        return super.isEmpty();
    }

    @Override
    public synchronized boolean addAll(final Collection c) {
        return super.addAll(c);
    }

    @Override
    public synchronized Object removeFirst() {
        return super.removeFirst();
    }

    @Override
    public synchronized boolean remove(final java.lang.Object o) {
        return super.remove(o);
    }
}
