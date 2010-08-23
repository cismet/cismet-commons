package de.cismet.tools;
public class Sorter {
    
    
    
    
    
//-----------------------------------------------------------------------------------------------
    /**ueberpruft ob das array sortiert ist*/
    private static boolean isSorted(Comparable[] array) {
        if(array.length < 2)
            return true;
        
        for(int i = 1;i < array.length;i++) {
            if(array[i-1].compareTo(array[i])> 0) // >
            {
                System.out.println("fehler bei i"+i);  // NOI18N
                return false;
                
            }
        }
        
        
        return true;
        
        
        
    }
    
    
    
    
//-------------------------------------------------------------------------------
    
    
    public static void insertionSort(Comparable[] array) {
        insertionSort(array,0,array.length-1);
        
    }
    
    /**InsertionSort*/
    
    public static void insertionSort(Comparable[] array, int left, int right) {
        
        int in,out;
        
        for(out = left+1;out <= right;out++) {
            Comparable tmp = array[out];
            in = out;
            
            while(in>left && array[in-1].compareTo(tmp) >= 0) // >=
            {
                array[in]=array[in-1];
                --in;
            }
            
            array[in] = tmp;
        }
        
        
        
    }
    
    
    
//----------------------------------------------------------------------------------------------------------
    
    /**Quicksort welches bei der Unterschreitung von partitionsize auf InsertionSort umschaltet*/
    
    private static void quickSort(Comparable[] array,int left,int right,int partitionSize) {
        if(left>=right)
            return;
        
        int size = right-left+1;
        
        if(size<partitionSize)
            insertionSort(array,left,right);
        
        else {
            
            
            Object median = array[right];// rightMost Element
            int partition = partitionIt(array,left,right,median);
            
            quickSort(array,left,partition-1,partitionSize);
            quickSort(array,partition+1,right,partitionSize);
            
        }
        
    }
    
//----------------------------------------------------------------------------------------------
    
    public  static void quickSort(Comparable[] array) {
        
        
        quickSort(array,0,array.length-1,16);// no insertion when partition >=16
        
        
    }
    
    
    
//---------------------------------------------------------------------------
    
    /*nimmt die unterteilung in die Mengen S< u. S> vor wird von Quicksort verwendet**/
    private static int partitionIt(Comparable[] array,int left, int right,Object pivot) {
        
        int leftPtr = left-1;
        int rightPtr = right ;
        
        while(true) {
            
            
            while(array[++leftPtr].compareTo( pivot) < 0 )// <
                ; // nop
            
            while((array[--rightPtr].compareTo(pivot) >0) && rightPtr >0 ) // >
                ; // nop
            
            if(leftPtr >= rightPtr)
                break;
            
            else
                swap(array,leftPtr,rightPtr);
            
            
            
        }// end forever
        
        
        swap(array,leftPtr,right);
        
        return leftPtr;
        
        
        
    }
    
//----------------------------------------------------------------------------------------------
    
    
    /**tauscht 2 Elemente eines Arrays*/
    
    private static void swap(Object[] array,int i, int j) {
        
        Object tmp = array[i];
        
        array[i] = array[j];
        
        array[j] = tmp;
        
    }
    
    
    
}// end class sorter
