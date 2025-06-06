/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.tools.collections;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class CircularObjectPool<T> {

    //~ Instance fields --------------------------------------------------------

    private final List<T> pool;
    private final AtomicInteger counter = new AtomicInteger(0);
    private final int size;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new CircularObjectPool object.
     *
     * @param   initialObjects  DOCUMENT ME!
     *
     * @throws  IllegalArgumentException  DOCUMENT ME!
     */
    public CircularObjectPool(final List<T> initialObjects) {
        if (initialObjects.size() <= 0) {
            throw new IllegalArgumentException("Pool size must be > 0");
        }
        this.size = initialObjects.size();
        this.pool = initialObjects;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public T next() {
        final int index = Math.floorMod(counter.getAndIncrement(), size);
        return pool.get(index);
    }
}
