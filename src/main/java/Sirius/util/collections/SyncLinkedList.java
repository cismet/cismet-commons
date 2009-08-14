/*
 * SyncLinkedList.java
 *
 * Created on 25. Mai 2004, 12:58
 */

package Sirius.util.collections;

import java.util.*;
/**
 *
 * @author  schlob
 */


public class SyncLinkedList extends LinkedList
{
    public synchronized boolean add(Object o)
    {
        if(!contains(o))
            return super.add(o);
        else
            return true;
    }
    
    public synchronized boolean isEmpty()
    {
        return super.isEmpty();
    }
    
    public synchronized boolean addAll(Collection c)
    {
        return super.addAll(c);
    }
    
    public synchronized Object removeFirst()
    {
        return super.removeFirst();
    }
    
    public synchronized boolean remove(java.lang.Object o)
    {
        return super.remove(o);
    }
    
    
}



