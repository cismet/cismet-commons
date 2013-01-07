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
 * Linked Properties
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class LinkedProperties extends Properties {

    //~ Instance fields --------------------------------------------------------

    private final LinkedHashMap map = new LinkedHashMap();

    //~ Methods ----------------------------------------------------------------
    
    /**
     * Associates a given <code>value</code> with a given <code>key</code<
     * 
     * @param key
     * @param value
     * 
     * @return the previous <code>value</code> associated with <code>key</code>, or
     *         <code>null</code> if there was no mapping for <code>key</code>.
     *         (A <code>null</code> return can also indicate that the map
     *         previously associated <code>null</code> with <code>key</code>.)
     */
    
    @Override
    public synchronized Object put(final Object key, final Object value) {
        return map.put(key, value);
    }

    /**
     * Returns the <code>value</code> for the given <code>key</code>
     * 
     * @param key
     * 
     * @return the associated <code>value</code>. Returns <code>null</code>, if the is no mapping for this key.
     */
    @Override
    public synchronized Object get(final Object key) {
        return map.get(key);
    }

    /**
     * Removes every <code>mapping</code> of this <code>map<code>
     */
    
    @Override
    public synchronized void clear() {
        map.clear();
    }

    /**
     * @throws UnsupportedOperationException
     */
    @Override
    public synchronized Object clone() {
        throw new UnsupportedOperationException();
    }

    /**
     * Tests whether there is any <code>mapping</code> with this <code>value</code> or not
     * 
     * @param value value whose presence in this map is to be tested
     * 
     * @return True, if there is a <code>key</code> with this <code>value</code>
     */
    @Override
    public boolean containsValue(final Object value) {
        return map.containsValue(value);
    }

     /**
     * Tests whether there is any <code>mapping</code> with this <code>value</code> or not
     * 
     * @param value value whose presence in this map is to be tested
     * 
     * @return True, if there is a <code>key</code> with this <code>value</code>
     */
    
    @Override
    public synchronized boolean contains(final Object value) {
        return containsValue(value);
    }

     /**
     * Tests whether the specified key is in the map or not
     * 
     * @param key key whose presence in this map is to be tested
     * 
     * @return True, if there is a <code>key</code>
     */
    
    @Override
    public synchronized boolean containsKey(final Object key) {
        return map.containsKey(key);
    }

    /**
     * Returns all <code>values</code>. Uses an <code>Iterator</code> over all <code>elements</code>.
     * 
     * @return every <code>value</code> in this map
     */
    @Override
    public synchronized Enumeration elements() {
        return new IteratorEnumeration(map.values().iterator());
    }

    /**
     * Returns a <code>Set</code> view of the mappings contained in this map.
     * 
     * @return <code>Set</code>
     */
    @Override
    public Set entrySet() {
        return map.entrySet();
    }

    /**
     * @throws  UnsupportedOperationException
     */
    
    @Override
    public synchronized boolean equals(final Object o) {
        throw new UnsupportedOperationException();
    }

    /**
     * Tests whether there is any mapping on the map or not
     * 
     * @return true, if there is no mapping
     */
    @Override
    public synchronized boolean isEmpty() {
        return map.isEmpty();
    }

    /**
     * Returns all <code>keys</code>. Uses an <code>Iterator</code> over all <code>elements</code>.
     * 
     * @return every <code>key</code> in this map
     */
    @Override
    public synchronized Enumeration keys() {
        return new IteratorEnumeration(map.keySet().iterator());
    }

    /**
     * Returns a <code>Set</code> view of the keys contained in this map.
     * 
     * @return <code>Set</code>
     */
    @Override
    public Set keySet() {
        return map.keySet();
    }

    /**
     * @throws UnsupportedOperationException
     */
    @Override
    public Enumeration propertyNames() {
        throw new UnsupportedOperationException();
    }

    /**
     * Copies all of the <code>mappings</code> from the specified <code>map</code> to this <code>map</code>.
     * These <code>mappings</code> will replace any <code>mappings</code> that this <code>map</code> had for
     * any of the <code>keys</code> currently in the specified <code>map</code>
     * 
     * @param t <code>map</code>
     */
    @Override
    public synchronized void putAll(final Map t) {
        map.putAll(t);
    }

    /**
     * Removes specified <code>key</code>
     * @param key <code>key</code>
     * @return the previous <code>value</code> associated with <code>key</code>, or
     *         <code>null</code> if there was no mapping for <code>key</code>.
     */
    @Override
    public synchronized Object remove(final Object key) {
        return map.remove(key);
    }

    /**
     * Returns the number of key-value mappings in this map.
     *
     * @return the number of key-value mappings in this map
     */
    @Override
    public synchronized int size() {
        return map.size();
    }

    /**
     * @throws UnsupportedOperationException
     */
    @Override
    public Collection values() {
        throw new UnsupportedOperationException();
    }

    /**
     * Searches for the property with the specified key in this property list.
     * If the key is not found in this property list, the default property list,
     * and its defaults, recursively, are then checked. The method returns
     * <code>null</code> if the property is not found.
     * 
     * @param key the property key
     * 
     * @return the value in this property list with the specified key value.
     */
    @Override
    public String getProperty(final String key) {
        final Object oval = get(key);
        final String sval = (oval instanceof String) ? (String)oval : null;
        return ((sval == null) && (defaults != null)) ? defaults.getProperty(key) : sval;
    }
}

/**
 * IteratorEnumeration
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
     * @param   i  Iterator
     *
     * @throws  IllegalArgumentException  Iterator is null
     */
    public IteratorEnumeration(final Iterator i) {
        if (i == null) {
            throw new IllegalArgumentException("Iterator is null"); // NOI18N
        }
        iterator = i;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Tests whether the <code>Iterator</code> as more Elemtents or not.
     * 
     * @return <code>True</code>, if the <code>Iterator</code> has more Elements.
     */
    @Override
    public boolean hasMoreElements() {
        return iterator.hasNext();
    }

    /**
     * Returns the next element in the iteration.
     * 
     * @return the next element in the iteration
     * @throws NoSuchElementException if the iteration has no more elements
     */
    @Override
    public Object nextElement() {
        return iterator.next();
    }
}
