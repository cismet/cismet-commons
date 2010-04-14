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
 *
 * @author thorsten
 */
public class LinkedProperties extends Properties {

    private final LinkedHashMap map = new LinkedHashMap();

    @Override
    public synchronized Object put(Object key, Object value) {
        return map.put(key, value);
    }

    @Override
    public synchronized Object get(Object key) {
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
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public synchronized boolean contains(Object value) {
        return containsValue(value);
    }

    @Override
    public synchronized boolean containsKey(Object key) {
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
    public synchronized boolean equals(Object o) {
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
    public synchronized void putAll(Map t) {
        map.putAll(t);
    }

    @Override
    public synchronized Object remove(Object key) {
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
    public String getProperty(String key) {
        final Object oval = get(key);
        final String sval = (oval instanceof String) ? (String) oval : null;
        return ((sval == null) && (defaults != null)) ? defaults.getProperty(key) : sval;
    }
}

class IteratorEnumeration
        implements Enumeration {

    private Iterator iterator;

    public IteratorEnumeration(Iterator i) {
        if (i == null) {
            throw new IllegalArgumentException("Iterator is null");  // NOI18N
        }
        iterator = i;
    }

    public boolean hasMoreElements() {
        return iterator.hasNext();
    }

    public Object nextElement() {
        return iterator.next();
    }
}

