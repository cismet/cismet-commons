package Sirius.util.collections;


import java.util.*;

////key=idnetifier;value=LinkedList
public class MultiMap extends HashMap
{
    
    
    ////////////////////////////////////////////////
    public MultiMap()
    {
        this(10);
    }
    
    ////////////////////////////////////////////////
    public MultiMap(int size)
    {
        super(size);
        
    }
    
    
    
    /////////////////////////////////////////////////
    
    public Object put(Object key,Object value)
    {
        SyncLinkedList list = null;
        
        
        if(containsKey(key))
            //anh\u00E4ngen an bestehende liste
        {
            list = (SyncLinkedList)get(key);
            
            list.add(value);
            
            
            
        }
        else
            //keine existierende liste
        {
            list=new SyncLinkedList();
            list.add(value);
            super.put(key,list);
        }
        
        
        //no item is replaced
        return null;
        
    }// end add
    
    //////////////////////////////////////////////////////////////
    
    public void putAll(Map t)
    {
        Iterator i = t.entrySet().iterator();
        
        while (i.hasNext())
        {
            Map.Entry e = (Map.Entry) i.next();
            
            Object val = e.getValue();
            Object key = e.getKey();
            
            // wenn mehrere Eintr\u00E4ge zu einem key
            if(val instanceof SyncLinkedList)
            {
                Iterator iter = ((SyncLinkedList)val).iterator();
                
                while(iter.hasNext())
                    put(key, iter.next());
                
            }
            else
                put(key,val );
        }
    }
    
    
////////////////////////////////////////////////////////
    
    
    public boolean remove(Object key,Object value)
    {
        SyncLinkedList list = null;
        
        
        if(containsKey(key))
        {
            list = (SyncLinkedList)get(key);
            
            return list.remove(value);
            
            //                Iterator iter = list.iterator();
            //
            //                while(iter.hasNext())
            //                    if(value.equals(iter.next()) )
            //                        return list.remove(value);
            
            
            
        }
        else
            return false;
        
        
        
        
    }
    
}// end class




//////////////////////////////////////////////////////////////////////////


