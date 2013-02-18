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
 * Modified {@link LinkedList} for synchronisation.
 *
 * @author   schlob
 * @version  $Revision$, $Date$
 */

public class SyncLinkedList extends LinkedList {

    //~ Methods ----------------------------------------------------------------

    /**
     * Adds the given Object to the List, if it's not already contained in the List. <code>synchronized</code>.
     *
     * @param   o  Object to be added
     *
     * @return  true, if the Object is already contained or if the Object was add succesfully.
     *
     * @see     LinkedList#add(java.lang.Object)
     */
    @Override
    public synchronized boolean add(final Object o) {
        if (!contains(o)) {
            return super.add(o);
        } else {
            return true;
        }
    }
    /**
     * Checks whether the List is Empty or not. <code>synchronized</code>.
     *
     * @return  true, true if the list is Empty
     *
     * @see     LinkedList#isEmpty()
     */
    @Override
    public synchronized boolean isEmpty() {
        return super.isEmpty();
    }
    /**
     * Adds the given Collection to the List. <code>synchronized</code>.
     *
     * @param   c  Collection to be added
     *
     * @return  if the Collection was added
     *
     * @see     LinkedList#addAll(java.util.Collection)
     */
    @Override
    public synchronized boolean addAll(final Collection c) {
        return super.addAll(c);
    }
    /**
     * Removes the first Object in the List. <code>synchronized</code>.
     *
     * @return  the deleted Object
     *
     * @see     LinkedList#removeFirst()
     */
    @Override
    public synchronized Object removeFirst() {
        return super.removeFirst();
    }
    /**
     * Removes the first occurrence of the specified element from this list, if it is present. If this list does not
     * contain the element, it is unchanged. <code>synchronized</code>.
     *
     * @param   o  Object to be removed
     *
     * @return  true if the Object is removed
     *
     * @see     LinkedList#remove(java.lang.Object)
     */
    @Override
    public synchronized boolean remove(final java.lang.Object o) {
        return super.remove(o);
    }
}
