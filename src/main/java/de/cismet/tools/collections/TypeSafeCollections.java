/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.tools.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Convenience Class for generic Collection creation.
 *
 * @author srichter
 */
public final class TypeSafeCollections {

    private TypeSafeCollections() {
        throw new AssertionError();
    }

    public static final <K, V> HashMap<K, V> newHashMap() {
        return new HashMap<K, V>();
    }

    public static final <K, V> LinkedHashMap<K, V> newLinkedHashMap() {
        return new LinkedHashMap<K, V>();
    }

    public static final <T> LinkedHashSet<T> newLinkedHashSet() {
        return new LinkedHashSet<T>();
    }

    public static final <K, V> ConcurrentHashMap<K, V> newConcurrentHashMap() {
        return new ConcurrentHashMap<K, V>();
    }

    public static <K, V> TreeMap<K, V> newTreeMap() {
        return new TreeMap<K, V>();
    }

    public static final <T> ArrayList<T> newArrayList() {
        return new ArrayList<T>();
    }

    public static final <T> LinkedList<T> newLinkedList() {
        return new LinkedList<T>();
    }

    public static final <T> HashSet<T> newHashSet() {
        return new HashSet<T>();
    }

    public static final <T> TreeSet<T> newTreeSet() {
        return new TreeSet<T>();
    }

    @SuppressWarnings("unchecked")
    public static final <T> T[] newArray(int size) {
        return (T[]) new Object[size];
    }

    public static final <T> Vector<T> newVector() {
        return new Vector<T>();
    }

    public static final <T> Stack<T> newStack() {
        return new Stack<T>();
    }

    public static final <T> ConcurrentSkipListSet<T> newConcurrentSkipListSet() {
        return new ConcurrentSkipListSet<T>();
    }

    public static final <T> CopyOnWriteArrayList<T> newCopyOnWriteArrayList() {
        return new CopyOnWriteArrayList<T>();
    }

    public static final <T> CopyOnWriteArraySet<T> newCopyOnWriteArraySet() {
        return new CopyOnWriteArraySet<T>();
    }

    public static final <K, V> ConcurrentSkipListMap<K, V> newConcurrentSkipListMap() {
        return new ConcurrentSkipListMap<K, V>();
    }

    public static final <T> ConcurrentLinkedQueue<T> newConcurrentLinkedQueue() {
        return new ConcurrentLinkedQueue<T>();
    }
    //--------

    public static final <K, V> HashMap<K, V> newHashMap(int capacity) {
        return new HashMap<K, V>(capacity);
    }

    public static final <K, V> HashMap<K, V> newHashMap(int capacity, float loadfactor) {
        return new HashMap<K, V>(capacity, loadfactor);
    }

    public static final <K, V> HashMap<K, V> newHashMap(Map<? extends K, ? extends V> m) {
        return new HashMap<K, V>(m);
    }

    public static final <K, V> ConcurrentHashMap<K, V> newConcurrentHashMap(Map<? extends K, ? extends V> m) {
        return new ConcurrentHashMap<K, V>(m);
    }

    public static final <K, V> ConcurrentHashMap<K, V> newConcurrentHashMap(int capacity) {
        return new ConcurrentHashMap<K, V>(capacity);
    }

    public static final <K, V> ConcurrentHashMap<K, V> newConcurrentHashMap(int capacity, float loadfactor) {
        return new ConcurrentHashMap<K, V>(capacity, loadfactor);
    }

    public static final <K, V> ConcurrentHashMap<K, V> newConcurrentHashMap(int capacity, float loadfactor, int concurrencyLevel) {
        return new ConcurrentHashMap<K, V>(capacity, loadfactor, concurrencyLevel);
    }

    public static <K, V> TreeMap<K, V> newTreeMap(Map<? extends K, ? extends V> m) {
        return new TreeMap<K, V>(m);
    }

    public static <K, V> TreeMap<K, V> newTreeMap(SortedMap<K, ? extends V> sm) {
        return new TreeMap<K, V>(sm);
    }

    public static <K, V> TreeMap<K, V> newTreeMap(Comparator<? super K> comparator) {
        return new TreeMap<K, V>(comparator);
    }

    public static final <T> ArrayList<T> newArrayList(int capacity) {
        return new ArrayList<T>(capacity);
    }

    public static final <T> ArrayList<T> newArrayList(Collection<? extends T> coll) {
        return new ArrayList<T>(coll);
    }

    public static final <T> LinkedList<T> newLinkedList(Collection<? extends T> coll) {
        return new LinkedList<T>(coll);
    }

    public static final <T> HashSet<T> newHashSet(int capacity) {
        return new HashSet<T>(capacity);
    }

    public static final <T> HashSet<T> newHashSet(int capacity, float loadfactor) {
        return new HashSet<T>(capacity, loadfactor);
    }

    public static final <T> HashSet<T> newHashSet(Collection<? extends T> coll) {
        return new HashSet<T>(coll);
    }

    public static final <T> TreeSet<T> newTreeSet(Collection<? extends T> coll) {
        return new TreeSet<T>(coll);
    }

    public static final <T> TreeSet<T> newTreeSet(SortedSet<? extends T> ss) {
        return new TreeSet<T>(ss);
    }

    public static final <T> TreeSet<T> newTreeSet(Comparator<? super T> comp) {
        return new TreeSet<T>(comp);
    }

    public static final <T> Vector<T> newVector(int capacity) {
        return new Vector<T>(capacity);
    }

    public static final <T> Vector<T> newVector(int capacity, int increment) {
        return new Vector<T>(capacity, increment);
    }

    public static final <T> Vector<T> newVector(Collection<? extends T> coll) {
        return new Vector<T>(coll);
    }

    public static final <T> ConcurrentSkipListSet<T> newConcurrentSkipListSet(Collection<? extends T> coll) {
        return new ConcurrentSkipListSet<T>(coll);
    }

    public static final <T> ConcurrentSkipListSet<T> newConcurrentSkipListSet(SortedSet<? extends T> ss) {
        return new ConcurrentSkipListSet<T>(ss);
    }

    public static final <T> ConcurrentSkipListSet<T> newConcurrentSkipListSet(Comparator<? super T> comp) {
        return new ConcurrentSkipListSet<T>(comp);
    }

    public static final <T> CopyOnWriteArrayList<T> newCopyOnWriteArrayList(Collection<? extends T> coll) {
        return new CopyOnWriteArrayList<T>(coll);
    }

    public static final <T> CopyOnWriteArrayList<T> newCopyOnWriteArrayList(T[] copyIn) {
        return new CopyOnWriteArrayList<T>(copyIn);
    }

    public static final <T> CopyOnWriteArraySet<T> newCopyOnWriteArraySet(Collection<? extends T> coll) {
        return new CopyOnWriteArraySet<T>(coll);
    }

    public static final <K, V> ConcurrentSkipListMap<K, V> newConcurrentSkipListMap(Map<? extends K, ? extends V> m) {
        return new ConcurrentSkipListMap<K, V>(m);
    }

    public static final <K, V> ConcurrentSkipListMap<K, V> newConcurrentSkipListMap(SortedMap<K, ? extends V> sm) {
        return new ConcurrentSkipListMap<K, V>(sm);
    }

    public static final <K, V> ConcurrentSkipListMap<K, V> newConcurrentSkipListMap(Comparator<? super K> comp) {
        return new ConcurrentSkipListMap<K, V>(comp);
    }

    public static final <T> ConcurrentLinkedQueue<T> newConcurrentLinkedQueue(Collection<? extends T> coll) {
        return new ConcurrentLinkedQueue<T>(coll);
    }

    public static final <K, V> LinkedHashMap<K, V> newLinkedHashMap(int capacity) {
        return new LinkedHashMap<K, V>(capacity);
    }

    public static final <K, V> LinkedHashMap<K, V> newLinkedHashMap(Map<? extends K, ? extends V> m) {
        return new LinkedHashMap<K, V>(m);
    }

    public static final <K, V> LinkedHashMap<K, V> newLinkedHashMap(int capacity, float loadfactor) {
        return new LinkedHashMap<K, V>(capacity, loadfactor);
    }

    public static final <K, V> LinkedHashMap<K, V> newLinkedHashMap(int capacity, float loadfactor, boolean accessOrder) {
        return new LinkedHashMap<K, V>(capacity, loadfactor, accessOrder);
    }

    public static final <T> LinkedHashSet<T> newLinkedHashSet(Collection<? extends T> coll) {
        return new LinkedHashSet<T>(coll);
    }

    public static final <T> LinkedHashSet<T> newLinkedHashSet(int capacity) {
        return new LinkedHashSet<T>(capacity);
    }

    public static final <T> LinkedHashSet<T> newLinkedHashSet(int capacity, float loadFactor) {
        return new LinkedHashSet<T>(capacity, loadFactor);
    }
}
