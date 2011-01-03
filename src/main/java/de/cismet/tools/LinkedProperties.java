/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.tools;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class LinkedProperties extends Properties {

    //~ Instance fields --------------------------------------------------------

    private final LinkedHashMap map = new LinkedHashMap();

    //~ Methods ----------------------------------------------------------------

    @Override
    public synchronized Object put(final Object key, final Object value) {
        return map.put(key, value);
    }

    @Override
    public synchronized Object get(final Object key) {
        return map.get(key);
    }

    @Override
    public synchronized void clear() {
        map.clear();
    }

    @Override
    public synchronized Object clone() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsValue(final Object value) {
        return map.containsValue(value);
    }

    @Override
    public synchronized boolean contains(final Object value) {
        return containsValue(value);
    }

    @Override
    public synchronized boolean containsKey(final Object key) {
        return map.containsKey(key);
    }

    @Override
    public synchronized Enumeration elements() {
        return new IteratorEnumeration(map.values().iterator());
    }

    @Override
    public Set entrySet() {
        return map.entrySet();
    }

    @Override
    public synchronized boolean equals(final Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public synchronized Enumeration keys() {
        return new IteratorEnumeration(map.keySet().iterator());
    }

    @Override
    public Set keySet() {
        return map.keySet();
    }

    @Override
    public Enumeration propertyNames() {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void putAll(final Map t) {
        map.putAll(t);
    }

    @Override
    public synchronized Object remove(final Object key) {
        return map.remove(key);
    }

    @Override
    public synchronized int size() {
        return map.size();
    }

    @Override
    public Collection values() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getProperty(final String key) {
        final Object oval = get(key);
        final String sval = (oval instanceof String) ? (String)oval : null;
        return ((sval == null) && (defaults != null)) ? defaults.getProperty(key) : sval;
    }
}

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
class IteratorEnumeration implements Enumeration {

    //~ Instance fields --------------------------------------------------------

    private Iterator iterator;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new IteratorEnumeration object.
     *
     * @param   i  DOCUMENT ME!
     *
     * @throws  IllegalArgumentException  DOCUMENT ME!
     */
    public IteratorEnumeration(final Iterator i) {
        if (i == null) {
            throw new IllegalArgumentException("Iterator is null"); // NOI18N
        }
        iterator = i;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public boolean hasMoreElements() {
        return iterator.hasNext();
    }

    @Override
    public Object nextElement() {
        return iterator.next();
    }
}
