package Sirius.util.collections;

import java.util.*;
import java.util.Vector; 



////////// Class //////////////////////////////////////

public class LongVector extends java.util.Vector<java.lang.Integer> implements java.io.Serializable//////////////
{
    private final transient org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(this.getClass());
    
    private boolean sorted;// false by default maybe sorted but not tested if tested it's set true
    
    
    
    
    //constructors
    public LongVector()
    {super();sorted = false;}
    
    public LongVector(int initialCapacity)
    {super(initialCapacity);sorted = false;}
    
    public LongVector(int initialCapacity, int capacityIncrement)
    {super(initialCapacity,capacityIncrement);sorted = false;}
    
    public LongVector(Collection c)
    {super(c);sorted = false;}
    
    public LongVector(Integer[] inElements)
    {copyInto(inElements);sorted = false;}
    
    public LongVector(int[] inElements)
    {
        for(int i=0;i<inElements.length;i++)
        {
            if(i>0 && inElements[i-1] > inElements[i])
            {
                sorted = false;
            }
            
            add(inElements[i]);
        }
        
        
    }
    
    
    
//    ////////////////////methods/////////////////////////////////////////
//
//    public  int at(int index) throws Exception
//    {
//        if (size() > index)
//        {
//            java.lang.Object intValue = super.get(index);
//            if (intValue instanceof Integer)
//                return ((Integer) intValue).intValue();
//            throw new  java.lang.NullPointerException("wrong type cannot convert to Integer");
//        }
//        throw new java.lang.IndexOutOfBoundsException("wrong Index of vector in method at()");
//
//    }
//
    ///////// converts to intArray/////////////////////
    public int[] convertToArray()
    {
        
        
        int integers[] = new int[size()];
        
        
        for(int i =0;i<integers.length;i++)
            integers[i] = get(i);
        
        
        return integers;
    }// end of convertToArray
    
    
    ////////intersection//////////////////////////////////
    
    
    public LongVector intersection(LongVector inVec)throws Exception
    {
        
        int newSize = inVec.size();
        
        if(newSize > size()) // find the smaller value
            newSize = size();
        
        LongVector tmpVec = new LongVector(newSize,5);
        try
        {
            for (int i=0; i < size();i++)
                if ( inVec.contains( get(i) ) )
                    tmpVec.add(get(i));
            
        }
        catch (Exception e)
        {logger.error(e);}
        
        //tmpVec.distinctify();
        
        return tmpVec;
    }// end intersection
    
    
    
    
    /////////////////////////////////////
    
    void printMe() // for tests
    {
        int i =0;
        try
        {
            for(i=0;i<size();i++)
                System.out.println(i +"  "+ get(i)+ "\n");  // NOI18N
        }
        catch (Exception e)
        {
            logger.error("Error at "+ i,e);  // NOI18N
            
            
        }//end catch
        
    } // end printme
    
    ///////////////////////////////
    
    public String forSqlStatement()
    {
        String sql = new String("(");  // NOI18N
        int i =0;
        
        try
        {
            distinctify();
            
            
            
            for(i=0;i<size();i++)
            {
                if(i==0)
                    sql+=get(i);
                else
                    sql += ","+get(i);  // NOI18N
            }
            sql+= ")";  // NOI18N
            
            
        }
        
        catch (Exception e)
        {
            
            logger.error("Error at "+ i,e);  // NOI18N
            sql = new String("()");  // NOI18N
        }
        return sql;
    }
    
    
    /////////////converts a bag to a set/////////////////
    
    protected void distinctify() throws Exception
    {
        
        for (int j=0;j<size();j++)
        {
            for(int i = j; i< size();i++)
            {
                if(get(i)==get(j)&& i!=j)
                {
                    removeElementAt(i);
                    i--;
                }
                
            }// endfor i
            
        } // end for j
        
    }// end distinctify
    
    ////////////////////////////////////
    // neu schreiben??
    public boolean isSorted()
    {
        return sorted;
    }
    
    //----------------------------------------------------------------
    // binaersuche einsetzen
    public boolean contains(long value)
    {
        try
        {
            
            if (sorted)
            {
                for(int i = 0; i< size();i++)
                {
                    if		(value < get(i))
                        return false;
                    
                    else if (value == get(i))
                        return true;
                }// end for
                
                return false;
            }
            else	//not sorted
            {
                for(int i = 0; i< size();i++)
                {
                    if(value == get(i))
                        return true;
                    
                }// end for
                
                return false;// not found
                
            }
        }
        catch(Exception e)
        {logger.error(e);}
        
        return false; // NEVER REACHED when no Exception is thrown
        
        
    }// end contains
    
    
    
    
}// end of class LongVector
