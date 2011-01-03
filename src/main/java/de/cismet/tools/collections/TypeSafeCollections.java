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
 * @author      srichter
 * @version     $Revision$, $Date$
 * @deprecated  mscholl: you should be aware of what you're doing when using generics and there isn't much to save
 *              either so spare this additional import and method call as there is more overhead that way
 */
public final class TypeSafeCollections {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TypeSafeCollections object.
     *
     * @throws  AssertionError  DOCUMENT ME!
     */
    private TypeSafeCollections() {
        throw new AssertionError();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   <K>  DOCUMENT ME!
     * @param   <V>  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static <K, V> HashMap<K, V> newHashMap() {
        return new HashMap<K, V>();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   <K>  DOCUMENT ME!
     * @param   <V>  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static <K, V> LinkedHashMap<K, V> newLinkedHashMap() {
        return new LinkedHashMap<K, V>();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   <T>  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static <T> LinkedHashSet<T> newLinkedHashSet() {
        return new LinkedHashSet<T>();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   <K>  DOCUMENT ME!
     * @param   <V>  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static <K, V> ConcurrentHashMap<K, V> newConcurrentHashMap() {
        return new ConcurrentHashMap<K, V>();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   <K>  DOCUMENT ME!
     * @param   <V>  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static <K, V> TreeMap<K, V> newTreeMap() {
        return new TreeMap<K, V>();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   <T>  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static <T> ArrayList<T> newArrayList() {
        return new ArrayList<T>();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   <T>  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static <T> LinkedList<T> newLinkedList() {
        return new LinkedList<T>();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   <T>  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static <T> HashSet<T> newHashSet() {
        return new HashSet<T>();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   <T>  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static <T> TreeSet<T> newTreeSet() {
        return new TreeSet<T>();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   <T>   DOCUMENT ME!
     * @param   size  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] newArray(final int size) {
        return (T[])new Object[size];
    }

    /**
     * DOCUMENT ME!
     *
     * @param   <T>  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static <T> Vector<T> newVector() {
        return new Vector<T>();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   <T>  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static <T> Stack<T> newStack() {
        return new Stack<T>();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   <T>  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static <T> ConcurrentSkipListSet<T> newConcurrentSkipListSet() {
        return new ConcurrentSkipListSet<T>();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   <T>  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static <T> CopyOnWriteArrayList<T> newCopyOnWriteArrayList() {
        return new CopyOnWriteArrayList<T>();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   <T>  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static <T> CopyOnWriteArraySet<T> newCopyOnWriteArraySet() {
        return new CopyOnWriteArraySet<T>();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   <K>  DOCUMENT ME!
     * @param   <V>  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static <K, V> ConcurrentSkipListMap<K, V> newConcurrentSkipListMap() {
        return new ConcurrentSkipListMap<K, V>();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   <T>  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static <T> ConcurrentLinkedQueue<T> newConcurrentLinkedQueue() {
        return new ConcurrentLinkedQueue<T>();
    }
    // --------

    /**
     * DOCUMENT ME!
     *
     * @param   <K>       DOCUMENT ME!
     * @param   <V>       DOCUMENT ME!
     * @param   capacity  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static <K, V> HashMap<K, V> newHashMap(final int capacity) {
        return new HashMap<K, V>(capacity);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   <K>         DOCUMENT ME!
     * @param   <V>         DOCUMENT ME!
     * @param   capacity    DOCUMENT ME!
     * @param   loadfactor  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static <K, V> HashMap<K, V> newHashMap(final int capacity, final float loadfactor) {
        return new HashMap<K, V>(capacity, loadfactor);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   <K>  DOCUMENT ME!
     * @param   <V>  DOCUMENT ME!
     * @param   m    DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static <K, V> HashMap<K, V> newHashMap(final Map<? extends K, ? extends V> m) {
        return new HashMap<K, V>(m);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   <K>  DOCUMENT ME!
     * @param   <V>  DOCUMENT ME!
     * @param   m    DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static <K, V> ConcurrentHashMap<K, V> newConcurrentHashMap(final Map<? extends K, ? extends V> m) {
        return new ConcurrentHashMap<K, V>(m);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   <K>       DOCUMENT ME!
     * @param   <V>       DOCUMENT ME!
     * @param   capacity  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static <K, V> ConcurrentHashMap<K, V> newConcurrentHashMap(final int capacity) {
        return new ConcurrentHashMap<K, V>(capacity);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   <K>         DOCUMENT ME!
     * @param   <V>         DOCUMENT ME!
     * @param   capacity    DOCUMENT ME!
     * @param   loadfactor  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static <K, V> ConcurrentHashMap<K, V> newConcurrentHashMap(final int capacity, final float loadfactor) {
        return new ConcurrentHashMap<K, V>(capacity, loadfactor);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   <K>               DOCUMENT ME!
     * @param   <V>               DOCUMENT ME!
     * @param   capacity          DOCUMENT ME!
     * @param   loadfactor        DOCUMENT ME!
     * @param   concurrencyLevel  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static <K, V> ConcurrentHashMap<K, V> newConcurrentHashMap(final int capacity,
            final float loadfactor,
            final int concurrencyLevel) {
        return new ConcurrentHashMap<K, V>(capacity, loadfactor, concurrencyLevel);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   <K>  DOCUMENT ME!
     * @param   <V>  DOCUMENT ME!
     * @param   m    DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static <K, V> TreeMap<K, V> newTreeMap(final Map<? extends K, ? extends V> m) {
        return new TreeMap<K, V>(m);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   <K>  DOCUMENT ME!
     * @param   <V>  DOCUMENT ME!
     * @param   sm   DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static <K, V> TreeMap<K, V> newTreeMap(final SortedMap<K, ? extends V> sm) {
        return new TreeMap<K, V>(sm);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   <K>         DOCUMENT ME!
     * @param   <V>         DOCUMENT ME!
     * @param   comparator  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static <K, V> TreeMap<K, V> newTreeMap(final Comparator<? super K> comparator) {
        return new TreeMap<K, V>(comparator);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   <T>       DOCUMENT ME!
     * @param   capacity  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static <T> ArrayList<T> newArrayList(final int capacity) {
        return new ArrayList<T>(capacity);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   <T>   DOCUMENT ME!
     * @param   coll  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static <T> ArrayList<T> newArrayList(final Collection<? extends T> coll) {
        return new ArrayList<T>(coll);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   <T>   DOCUMENT ME!
     * @param   coll  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static <T> LinkedList<T> newLinkedList(final Collection<? extends T> coll) {
        return new LinkedList<T>(coll);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   <T>       DOCUMENT ME!
     * @param   capacity  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static <T> HashSet<T> newHashSet(final int capacity) {
        return new HashSet<T>(capacity);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   <T>         DOCUMENT ME!
     * @param   capacity    DOCUMENT ME!
     * @param   loadfactor  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static <T> HashSet<T> newHashSet(final int capacity, final float loadfactor) {
        return new HashSet<T>(capacity, loadfactor);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   <T>   DOCUMENT ME!
     * @param   coll  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static <T> HashSet<T> newHashSet(final Collection<? extends T> coll) {
        return new HashSet<T>(coll);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   <T>   DOCUMENT ME!
     * @param   coll  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static <T> TreeSet<T> newTreeSet(final Collection<? extends T> coll) {
        return new TreeSet<T>(coll);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   <T>  DOCUMENT ME!
     * @param   ss   DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static <T> TreeSet<T> newTreeSet(final SortedSet<? extends T> ss) {
        return new TreeSet<T>(ss);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   <T>   DOCUMENT ME!
     * @param   comp  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static <T> TreeSet<T> newTreeSet(final Comparator<? super T> comp) {
        return new TreeSet<T>(comp);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   <T>       DOCUMENT ME!
     * @param   capacity  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static <T> Vector<T> newVector(final int capacity) {
        return new Vector<T>(capacity);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   <T>        DOCUMENT ME!
     * @param   capacity   DOCUMENT ME!
     * @param   increment  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static <T> Vector<T> newVector(final int capacity, final int increment) {
        return new Vector<T>(capacity, increment);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   <T>   DOCUMENT ME!
     * @param   coll  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static <T> Vector<T> newVector(final Collection<? extends T> coll) {
        return new Vector<T>(coll);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   <T>   DOCUMENT ME!
     * @param   coll  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static <T> ConcurrentSkipListSet<T> newConcurrentSkipListSet(final Collection<? extends T> coll) {
        return new ConcurrentSkipListSet<T>(coll);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   <T>  DOCUMENT ME!
     * @param   ss   DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static <T> ConcurrentSkipListSet<T> newConcurrentSkipListSet(final SortedSet<? extends T> ss) {
        return new ConcurrentSkipListSet<T>(ss);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   <T>   DOCUMENT ME!
     * @param   comp  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static <T> ConcurrentSkipListSet<T> newConcurrentSkipListSet(final Comparator<? super T> comp) {
        return new ConcurrentSkipListSet<T>(comp);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   <T>   DOCUMENT ME!
     * @param   coll  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static <T> CopyOnWriteArrayList<T> newCopyOnWriteArrayList(final Collection<? extends T> coll) {
        return new CopyOnWriteArrayList<T>(coll);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   <T>     DOCUMENT ME!
     * @param   copyIn  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static <T> CopyOnWriteArrayList<T> newCopyOnWriteArrayList(final T[] copyIn) {
        return new CopyOnWriteArrayList<T>(copyIn);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   <T>   DOCUMENT ME!
     * @param   coll  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static <T> CopyOnWriteArraySet<T> newCopyOnWriteArraySet(final Collection<? extends T> coll) {
        return new CopyOnWriteArraySet<T>(coll);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   <K>  DOCUMENT ME!
     * @param   <V>  DOCUMENT ME!
     * @param   m    DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static <K, V> ConcurrentSkipListMap<K, V> newConcurrentSkipListMap(final Map<? extends K, ? extends V> m) {
        return new ConcurrentSkipListMap<K, V>(m);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   <K>  DOCUMENT ME!
     * @param   <V>  DOCUMENT ME!
     * @param   sm   DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static <K, V> ConcurrentSkipListMap<K, V> newConcurrentSkipListMap(final SortedMap<K, ? extends V> sm) {
        return new ConcurrentSkipListMap<K, V>(sm);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   <K>   DOCUMENT ME!
     * @param   <V>   DOCUMENT ME!
     * @param   comp  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static <K, V> ConcurrentSkipListMap<K, V> newConcurrentSkipListMap(final Comparator<? super K> comp) {
        return new ConcurrentSkipListMap<K, V>(comp);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   <T>   DOCUMENT ME!
     * @param   coll  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static <T> ConcurrentLinkedQueue<T> newConcurrentLinkedQueue(final Collection<? extends T> coll) {
        return new ConcurrentLinkedQueue<T>(coll);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   <K>       DOCUMENT ME!
     * @param   <V>       DOCUMENT ME!
     * @param   capacity  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static <K, V> LinkedHashMap<K, V> newLinkedHashMap(final int capacity) {
        return new LinkedHashMap<K, V>(capacity);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   <K>  DOCUMENT ME!
     * @param   <V>  DOCUMENT ME!
     * @param   m    DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static <K, V> LinkedHashMap<K, V> newLinkedHashMap(final Map<? extends K, ? extends V> m) {
        return new LinkedHashMap<K, V>(m);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   <K>         DOCUMENT ME!
     * @param   <V>         DOCUMENT ME!
     * @param   capacity    DOCUMENT ME!
     * @param   loadfactor  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static <K, V> LinkedHashMap<K, V> newLinkedHashMap(final int capacity, final float loadfactor) {
        return new LinkedHashMap<K, V>(capacity, loadfactor);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   <K>          DOCUMENT ME!
     * @param   <V>          DOCUMENT ME!
     * @param   capacity     DOCUMENT ME!
     * @param   loadfactor   DOCUMENT ME!
     * @param   accessOrder  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static <K, V> LinkedHashMap<K, V> newLinkedHashMap(final int capacity,
            final float loadfactor,
            final boolean accessOrder) {
        return new LinkedHashMap<K, V>(capacity, loadfactor, accessOrder);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   <T>   DOCUMENT ME!
     * @param   coll  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static <T> LinkedHashSet<T> newLinkedHashSet(final Collection<? extends T> coll) {
        return new LinkedHashSet<T>(coll);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   <T>       DOCUMENT ME!
     * @param   capacity  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static <T> LinkedHashSet<T> newLinkedHashSet(final int capacity) {
        return new LinkedHashSet<T>(capacity);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   <T>         DOCUMENT ME!
     * @param   capacity    DOCUMENT ME!
     * @param   loadFactor  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static <T> LinkedHashSet<T> newLinkedHashSet(final int capacity, final float loadFactor) {
        return new LinkedHashSet<T>(capacity, loadFactor);
    }
}
