/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.tools;
/**
 * Sorter Tool
 *
 * @version  $Revision$, $Date$
 */
public class Sorter {

    //~ Methods ----------------------------------------------------------------

// -----------------------------------------------------------------------------------------------
    /**
     * Tests whether the specified <code>Array</code> is sorted or not.
     *
     * @param   array  <code>Array</code>, which is going to be tested.
     *
     * @return  <code>True</code>, if the <code>Array</code> is sorted.
     */
    private static boolean isSorted(final Comparable[] array) {
        if (array.length < 2) {
            return true;
        }

        for (int i = 1; i < array.length; i++) {
            if (array[i - 1].compareTo(array[i]) > 0)   // >
            {
                System.out.println("fehler bei i" + i); // NOI18N
                return false;
            }
        }

        return true;
    }

    /**
     * Sorts the specified <code>Array</code> with <code>insertionSort</code>. The Complete <code>Array</code> is searched.
     *
     * @param  array  <code>Array</code>, which is going to get sorted
     * 
     * @see #insertionSort(java.lang.Comparable[], int, int) 
     */
    public static void insertionSort(final Comparable[] array) {
        insertionSort(array, 0, array.length - 1);
    }

    /**
     * Sorts the specified <code>Array</code> with <code>insertionSort</code>. Sorts only a specified area.
     * 
     * @param array <code>Array</code>, which is going to get sorted
     * @param left left end of the area, which is going to get sorted
     * @param right right end of the area, which is going to get sorted
     * 
     */
    public static void insertionSort(final Comparable[] array, final int left, final int right) {
        int in;
        int out;

        for (out = left + 1; out <= right; out++) {
            final Comparable tmp = array[out];
            in = out;

            while ((in > left) && (array[in - 1].compareTo(tmp) >= 0)) // >=
            {
                array[in] = array[in - 1];
                --in;
            }

            array[in] = tmp;
        }
    }

//----------------------------------------------------------------------------------------------------------

    /**
     * Sorts the specified <code>Array</code> with <code>Quicksort</code>.Sorts only a specified area.
     * If the to be sorted area is smaller than or equal partitionSize, <code>InsertionSort</code> will be used.
     *
     * @param  array          <code>Array</code>, which is going to get sorted.
     * @param  left           left end of the area, which is going to get sorted
     * @param  right          right end of the area, which is going to get sorted
     * @param  partitionSize  minimal size for using <code>Quicksort</code>
     * 
     * @see #insertionSort(java.lang.Comparable[], int, int) 
     * @see #partitionIt(java.lang.Comparable[], int, int, java.lang.Object) 
     * @see #swap(java.lang.Object[], int, int) 
     */

    private static void quickSort(final Comparable[] array, final int left, final int right, final int partitionSize) {
        if (left >= right) {
            return;
        }

        final int size = right - left + 1;

        if (size < partitionSize) {
            insertionSort(array, left, right);
        } else {
            final Object median = array[right]; // rightMost Element
            final int partition = partitionIt(array, left, right, median);

            quickSort(array, left, partition - 1, partitionSize);
            quickSort(array, partition + 1, right, partitionSize);
        }
    }

    /**
     * Sorts the specified <code>Array</code> with <code>Quicksort</code>.Sorts the complete <code>Array</code>.
     * If the to be sorted area is smaller than or equal 16, <code>InsertionSort</code> will be used.
     *
     * @param  array  <code>Array</code>, which is going to get sorted.
     * 
     * @see #quickSort(java.lang.Comparable[], int, int, int) 
     */
    public static void quickSort(final Comparable[] array) {
        quickSort(array, 0, array.length - 1, 16); // no insertion when partition >=16
    }
    /**
     * Divides into two Sets
     * <ol>
     * <li>all Elements which are smaller than the Rightmost</li>
     * <li>all Elements which are bigger than the Rightmost</li>
     * </ol>
     *
     * @param   array  <code>Array, which is going to get Sorted
     * @param   left   left end of the area, which is going to get sorted
     * @param   right  right end of the area, which is going to get sorted
     * @param   pivot  <code>Rightmost</code>
     *
     * @return  Location of the Rightmost after the Sorting
     * 
     * @see #quickSort(java.lang.Comparable[], int, int, int) 
     * @see #swap(java.lang.Object[], int, int) 
     */
    private static int partitionIt(final Comparable[] array, final int left, final int right, final Object pivot) {
        int leftPtr = left - 1;
        int rightPtr = right;

        while (true) {
            while (array[++leftPtr].compareTo(pivot) < 0) { // <
                ;                                           // nop
            }

            while ((array[--rightPtr].compareTo(pivot) > 0) && (rightPtr > 0)) { // >
                ;                                                                // nop
            }

            if (leftPtr >= rightPtr) {
                break;
            } else {
                swap(array, leftPtr, rightPtr);
            }
        } // end forever

        swap(array, leftPtr, right);

        return leftPtr;
    }

//----------------------------------------------------------------------------------------------

    /**
     * swaps two Elements of the specified <code>Array</code>
     *
     * @param  array  <code>Array</code>, which is going to get sorted
     * @param  i      Location of Element one
     * @param  j      Location of Element two
     * 
     * @see #quickSort(java.lang.Comparable[], int, int, int) 
     * @see #partitionIt(java.lang.Comparable[], int, int, java.lang.Object) 
     */

    private static void swap(final Object[] array, final int i, final int j) {
        final Object tmp = array[i];

        array[i] = array[j];

        array[j] = tmp;
    }
} // end class sorter
