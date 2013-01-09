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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Creates {@link LinkedHashMap} for Properties.
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class LinkedProperties extends Properties {

    //~ Instance fields --------------------------------------------------------

    private final LinkedHashMap map = new LinkedHashMap();

    //~ Methods ----------------------------------------------------------------

    /**
     * Associates a given <code>value</code> with a given <code>key</code>. Removes the previous associated <code>
     * value</code>, if there is any.
     *
     * @param   key    The key for the given value
     * @param   value  The value for the given key
     *
     * @return  The previous <code>value</code> associated with <code>key</code>, or <code>null</code> if there was no
     *          mapping for <code>key</code>. (A <code>null</code> return can also indicate that the map previously
     *          associated <code>null</code> with <code>key</code>.)
     *
     * @see     HashMap#put(java.lang.Object, java.lang.Object)
     */

    @Override
    public synchronized Object put(final Object key, final Object value) {
        return map.put(key, value);
    }

    /**
     * Returns the <code>value</code> for the given <code>key.</code>
     *
     * @param   key  The key, whose value is searched
     *
     * @return  the associated <code>value</code>. Returns <code>null</code>, if the is no mapping for this key.
     *
     * @see     HashMap#get(java.lang.Object)
     */
    @Override
    public synchronized Object get(final Object key) {
        return map.get(key);
    }

    /**
     * Clears the <code>map</code>. (Removes every <code>mapping</code> for this <code>map</code>)
     *
     * @see  HashMap#clear()
     */

    @Override
    public synchronized void clear() {
        map.clear();
    }

    /**
     * Not Supported.
     *
     * @return  error
     *
     * @throws  UnsupportedOperationException  not supported
     */
    @Override
    public synchronized Object clone() {
        throw new UnsupportedOperationException();
    }

    /**
     * Tests whether there is any <code>key</code> associated with this <code>value</code> or not.
     *
     * @param   value  searched value
     *
     * @return  True, if there is a <code>key</code> with this <code>value</code>
     *
     * @see     HashMap#containsValue(java.lang.Object)
     */
    @Override
    public boolean containsValue(final Object value) {
        return map.containsValue(value);
    }

    /**
     * Tests whether there is any <code>key</code> associated with this <code>value</code> or not.
     *
     * @param   value  searched value
     *
     * @return  True, if there is a <code>key</code> with this <code>value</code>
     *
     * @see     HashMap#containsValue(java.lang.Object)
     */

    @Override
    public synchronized boolean contains(final Object value) {
        return containsValue(value);
    }

    /**
     * Tests whether the specified key is in the map or not.
     *
     * @param   key  searched key
     *
     * @return  True, if there is this <code>key</code>
     *
     * @see     HashMap#containsKey(java.lang.Object)
     */

    @Override
    public synchronized boolean containsKey(final Object key) {
        return map.containsKey(key);
    }

    /**
     * Returns all <code>values</code> as Enumeration. Uses an <code>Iterator</code> over all <code>elements</code>.
     *
     * @return  every <code>value</code> in this map as Enumeration
     *
     * @see     IteratorEnumeration
     */
    @Override
    public synchronized Enumeration elements() {
        return new IteratorEnumeration(map.values().iterator());
    }

    /**
     * Returns a <code>Set</code> view of the mappings contained in this map.
     *
     * @return  <code>Set</code>
     *
     * @see     HashMap#entrySet()
     */
    @Override
    public Set entrySet() {
        return map.entrySet();
    }

    /**
     * Not Supported.
     *
     * @param   o  Object
     *
     * @return  error
     *
     * @throws  UnsupportedOperationException  not supported
     */

    @Override
    public synchronized boolean equals(final Object o) {
        throw new UnsupportedOperationException();
    }

    /**
     * Tests whether there is any mapping on the map or not.
     *
     * @return  true, if there is no mapping
     *
     * @see     HashMap#isEmpty()
     */
    @Override
    public synchronized boolean isEmpty() {
        return map.isEmpty();
    }

    /**
     * Returns all <code>keys</code> as Enumeration. Uses an <code>Iterator</code> over all <code>elements</code>.
     *
     * @return  every <code>key</code> in this map as Enumeration.
     *
     * @see     IteratorEnumeration
     */
    @Override
    public synchronized Enumeration keys() {
        return new IteratorEnumeration(map.keySet().iterator());
    }

    /**
     * Returns a <code>Set</code> view of the keys contained in this map.
     *
     * @return  <code>Set</code>
     *
     * @see     HashMap#keySet()
     */
    @Override
    public Set keySet() {
        return map.keySet();
    }

    /**
     * not supported.
     *
     * @return  error
     *
     * @throws  UnsupportedOperationException  not supported
     */
    @Override
    public Enumeration propertyNames() {
        throw new UnsupportedOperationException();
    }

    /**
     * Copies all of the <code>mappings</code> from the specified <code>map</code> to this <code>map</code>. These
     * <code>mappings</code> will replace any <code>mappings</code>, that this <code>map</code> had for any of the
     * <code>keys</code> currently in the specified <code>map</code>
     *
     * @param  t  <code>map</code>
     *
     * @see    HashMap#putAll(java.util.Map)
     */
    @Override
    public synchronized void putAll(final Map t) {
        map.putAll(t);
    }

    /**
     * Removes specified <code>key.</code>
     *
     * @param   key  <code>key</code>
     *
     * @return  the previous <code>value</code> associated with <code>key</code>, or <code>null</code> if there was no
     *          mapping for <code>key</code>.
     *
     * @see     HashMap#remove(java.lang.Object)
     */
    @Override
    public synchronized Object remove(final Object key) {
        return map.remove(key);
    }

    /**
     * Returns the amount of mappings in this map.
     *
     * @return  the amount of mappings in this map
     *
     * @see     HashMap#size()
     */
    @Override
    public synchronized int size() {
        return map.size();
    }

    /**
     * not supported.
     *
     * @return  error
     *
     * @throws  UnsupportedOperationException  not supported!
     */
    @Override
    public Collection values() {
        throw new UnsupportedOperationException();
    }

    /**
     * Searches for the property with the specified key in this property list. If the key is not found in this property
     * list, the default property list, and its defaults, recursively, are then checked. The method returns <code>
     * null</code> if the property is not found.
     *
     * @param   key  the property key
     *
     * @return  the value in this property list with the specified key value.
     */
    @Override
    public String getProperty(final String key) {
        final Object oval = get(key);
        final String sval = (oval instanceof String) ? (String)oval : null;
        return ((sval == null) && (defaults != null)) ? defaults.getProperty(key) : sval;
    }
}

/**
 * IteratorEnumeration.
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
     * @return  <code>True</code>, if the <code>Iterator</code> has more Elements.
     *
     * @see     Iterator#hasNext()
     */
    @Override
    public boolean hasMoreElements() {
        return iterator.hasNext();
    }

    /**
     * Returns the next element in the iteration.
     *
     * @return  the next element in the iteration
     *
     * @see     Iterator#next()
     */
    @Override
    public Object nextElement() {
        return iterator.next();
    }
}
