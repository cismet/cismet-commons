/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.tools.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * Modified {@link ArrayList} Class, which contains an additional Hashset.
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class HashArrayList<E> extends ArrayList<E> {

    //~ Instance fields --------------------------------------------------------

    /**
     * @see  HashSet
     */
    HashSet<E> containsMarkerSet = new HashSet<E>();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new HashArrayList object.
     */
    public HashArrayList() {
    }

    /**
     * Creates a new HashArrayList object from a specified <code>Collection</code>.
     *
     * @param  c  <code>Collection</code>
     */
    public HashArrayList(final Collection<? extends E> c) {
        super(c);
        for (final E item : c) {
            containsMarkerSet.add(item);
        }
    }

    /**
     * Constructs an empty HashArrayList with the specified initial capacity.
     *
     * @param  initialCapacity  the initial capacity of the list
     */
    public HashArrayList(final int initialCapacity) {
        super(initialCapacity);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Appends the specified <code>Element</code> to the end of <code>Arraylist</code> and adds the specified <code>
     * Element</code> to the <code>HashSet</code> if it is not already present.
     *
     * @param   e  Element, which should get append
     *
     * @return  <code>True</code>
     *
     * @see     ArrayList#add(java.lang.Object)
     * @see     HashSet#add(java.lang.Object)
     */
    @Override
    public boolean add(final E e) {
        containsMarkerSet.add(e);
        return super.add(e);
    }

    /**
     * Appends the specified <code>Element</code> to the specified <code>index</code>.Shifts the element currently at
     * that position (if any) and any subsequent elements to the right (adds one to their indices). Adds the specified
     * <code>element</code> to the <code>HashSet</code> if it is not already present.
     *
     * @param  index    index, where the Element should be placed to
     * @param  element  Element to be appended
     *
     * @see    ArrayList#add(int, java.lang.Object)
     * @see    HashSet#add(java.lang.Object)
     */
    @Override
    public void add(final int index, final E element) {
        containsMarkerSet.add(element);
        super.add(index, element);
    }

    /**
     * This implementation <code>iterates</code> over the specified <code>Collection</code>, and adds each object
     * returned by the <code>iterator</code> to this <code>collection</code>, in turn. Appends the new Objects to the
     * end of the <code>ArrayList</code>
     *
     * @param   c  <code>Collection</code>
     *
     * @return  <code>true</code> if this list changed as a result of the call
     *
     * @see     ArrayList#addAll(java.util.Collection)
     * @see     HashSet#addAll(java.util.Collection)
     */
    @Override
    public boolean addAll(final Collection<? extends E> c) {
        containsMarkerSet.addAll(c);
        return super.addAll(c);
    }

    /**
     * This implementation <code>iterates</code> over the specified <code>Collection</code>, and adds each object
     * returned by the <code>iterator</code> to this <code>collection</code>, in turn. Inserts all of the elements in
     * the specified collection into the <code>ArrayList</code>, starting at the specified position. Shifts the element
     * currently at that position.
     *
     * @param   index  startindex
     * @param   c      Collection
     *
     * @return  <code>true</code> if this list changed as a result of the call
     *
     * @see     ArrayList#addAll(int, java.util.Collection)
     * @see     HashSet#addAll(java.util.Collection)
     */
    @Override
    public boolean addAll(final int index, final Collection<? extends E> c) {
        containsMarkerSet.addAll(c);
        return super.addAll(index, c);
    }

    /**
     * Removes all of the elements from this <code>HashArrayList</code>. The <code>ArrayList</code> and the <code>
     * HashSet</code> will be empty after this call returns.
     *
     * @see  ArrayList#clear()
     * @see  HashSet#clear()
     */
    @Override
    public void clear() {
        containsMarkerSet.clear();
        super.clear();
    }

    /**
     * Clones the <code>HashArrayList</code>.
     *
     * @return  the cloned <code>HashArrayList</code>
     */
    @Override
    public Object clone() {
        return new HashArrayList<E>(this);
    }

    /**
     * Tests whether the <code>ArrayList</code> contains the specified Object or not.
     *
     * @param   o  Object to be tested
     *
     * @return  <code>True</code>, if the <code>ArrayList</code> contains the specified Object
     *
     * @see     ArrayList#contains(java.lang.Object)
     */
    @Override
    public boolean contains(final Object o) {
//        return containsMarkerSet.contains(o);
        return super.contains(o);
    }

    /**
     * Removes the Object on the specified <code>index</code>. The <code>ArrayList</code> shifts any subsequent elements
     * to the left.Removes the element associated with the index in the <code>Hashmap</code> //Note: If the deleted
     * Object was multiple time inside the <code>Arraylist</code>, it could be possible that the Object is inside <code>
     * ArrayList</code>, but not in the <code>HashSet</code>
     *
     * @param   index  index, whose Object to be deleted
     *
     * @return  the Object, that was removed from the <code>HashArrayList</code>
     *
     * @see     ArrayList#remove(int)
     * @see     HashSet#remove(java.lang.Object)
     */
    @Override
    public E remove(final int index) {
        containsMarkerSet.remove(get(index));
        return super.remove(index);
    }

    /**
     * Removes the Object on the specified <code>index</code>. The <code>ArrayList</code> deletes the first occurrence
     * of the specified Object and shifts any subsequent elements to the left.Removes the element associated with the
     * index in the <code>Hashmap</code> //Note: If the deleted Object was multiple time inside the <code>
     * Arraylist</code>, it could be possible that the Object is inside <code>ArrayList</code>, but not in the <code>
     * HashSet</code>
     *
     * @param   o  Object to be deleted
     *
     * @return  the Object, that was removed from the <code>HashArrayList</code>
     *
     * @see     ArrayList#remove(java.lang.Object)
     * @see     HashSet#remove(java.lang.Object)
     */
    @Override
    public boolean remove(final Object o) {
        containsMarkerSet.remove(o);
        return super.remove(o);
    }

    /**
     * Removes the Object on the <code>index</code> within the specified range. The <code>ArrayList</code> shifts any
     * subsequent elements to the left.Removes the element associated with the index in the <code>Hashmap</code> //Note:
     * If the deleted Object was multiple time inside the <code>Arraylist</code>, it could be possible that the Object
     * is inside <code>ArrayList</code>, but not in the <code>HashSet</code>
     *
     * @param  fromIndex  Start IndexRange
     * @param  toIndex    End IndexRange
     *
     * @see    ArrayList#removeRange(int, int)
     * @see    HashSet#remove(java.lang.Object)
     */
    @Override
    protected void removeRange(final int fromIndex, final int toIndex) {
        for (int i = fromIndex; i <= toIndex; ++i) {
            containsMarkerSet.remove(get(i));
        }
        super.removeRange(fromIndex, toIndex);
    }

    /**
     * Replaces the element at the specified position in this <code>ArrayList</code> with the specified element. Removes
     * the element associated with the index in the <code>Hashmap</code> and adds the element.
     *
     * @param   index    index, whose element to be replaced
     * @param   element  element
     *
     * @return  the element previously at the specified position
     *
     * @see     ArrayList#set(int, java.lang.Object)
     */
    @Override
    public E set(final int index, final E element) {
        containsMarkerSet.remove(get(index));
        containsMarkerSet.add(element);
        return super.set(index, element);
    }

    /**
     * Removes all elements contained in the specified Collection.
     *
     * @param   c  Collection
     *
     * @return  <code>true</code> if this <code>HashArrayList</code> changed as a result of the call
     *
     * @see     ArrayList#removeAll(java.util.Collection)
     * @see     HashSet#removeAll(java.util.Collection)
     */
    @Override
    public boolean removeAll(final Collection<?> c) {
        containsMarkerSet.removeAll(c);
        return super.removeAll(c);
    }
}
