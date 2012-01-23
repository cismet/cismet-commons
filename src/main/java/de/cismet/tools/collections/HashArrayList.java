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
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class HashArrayList<E> extends ArrayList<E> {

    //~ Instance fields --------------------------------------------------------

    HashSet<E> containsMarkerSet = new HashSet<E>();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new HashArrayList object.
     */
    public HashArrayList() {
    }

    /**
     * Creates a new HashArrayList object.
     *
     * @param  c  DOCUMENT ME!
     */
    public HashArrayList(final Collection<? extends E> c) {
        super(c);
        for (final E item : c) {
            containsMarkerSet.add(item);
        }
    }

    /**
     * Creates a new HashArrayList object.
     *
     * @param  initialCapacity  DOCUMENT ME!
     */
    public HashArrayList(final int initialCapacity) {
        super(initialCapacity);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public boolean add(final E e) {
        containsMarkerSet.add(e);
        return super.add(e);
    }

    @Override
    public void add(final int index, final E element) {
        containsMarkerSet.add(element);
        super.add(index, element);
    }

    @Override
    public boolean addAll(final Collection<? extends E> c) {
        containsMarkerSet.addAll(c);
        return super.addAll(c);
    }

    @Override
    public boolean addAll(final int index, final Collection<? extends E> c) {
        containsMarkerSet.addAll(c);
        return super.addAll(index, c);
    }

    @Override
    public void clear() {
        containsMarkerSet.clear();
        super.clear();
    }

    @Override
    public Object clone() {
        return new HashArrayList<E>(this);
    }

    @Override
    public boolean contains(final Object o) {
//        return containsMarkerSet.contains(o);
        return super.contains(o);
    }

    @Override
    public E remove(final int index) {
        containsMarkerSet.remove(get(index));
        return super.remove(index);
    }

    @Override
    public boolean remove(final Object o) {
        containsMarkerSet.remove(o);
        return super.remove(o);
    }

    @Override
    protected void removeRange(final int fromIndex, final int toIndex) {
        for (int i = fromIndex; i <= toIndex; ++i) {
            containsMarkerSet.remove(get(i));
        }
        super.removeRange(fromIndex, toIndex);
    }

    @Override
    public E set(final int index, final E element) {
        containsMarkerSet.remove(get(index));
        containsMarkerSet.add(element);
        return super.set(index, element);
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        containsMarkerSet.removeAll(c);
        return super.removeAll(c);
    }
}
