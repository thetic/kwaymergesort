import java.util.LinkedList;
import java.util.Queue;

/**
 * A class that contains a group of sorting algorithms.
 * The input to the sorting algorithms is assumed to be
 * an array of integers.
 *
 */
public class Sort {

    /**
     * number of trials per condition set
     */
    private static final int NUM_TESTS = 3;

    /**
     * Different k-values to test
     */
    private static final int[] RADIX = {2, 3, 5, 10, 20, 50};

    /**
     * Different sizes of data sets to test
     */
    private static final int[] SIZES
            = {200000, 400000, 800000, 1600000, 3200000};

    // Constructor for objects of class Sort
    public Sort() {
    }

    /**
     * Given an array of integers and an integer k, sort the array
     * (ascending order) using k-way mergesort.
     * @param data  an array of integers
     * @param k     the k in k-way mergesort
     */
    public static void kwayMergesort (int[] data, int k) {
        kwayMergesortRecursive (data, 0, data.length - 1, k);
    }

    /**
     * The recursive part of k-way merge sort.
     * Given an array of integers (data), a low index, high index,
     * and an integer k, sort the subarray data[low..high] (ascending order)
     * using k-way merge sort.
     * @param data  an array of integers
     * @param low   low index
     * @param high  high index
     * @param k     the k in k-way mergesort
     */
    public static void kwayMergesortRecursive (int[] data, int low,
                                               int high, int k) {
        if (low < high) {
            for (int i = 0; i < k; i++) {
                kwayMergesortRecursive (data,
                        low + (i * (high - low + 1) / k),
                        low + ((i + 1) * (high - low + 1) / k) - 1,
                        k);
            }
            merge(data, low, high, k);
        }
    }

    /**
     * Given an array of integers (data), a low index, a high index,
     * and an integer k, sort the subarray data[low..high].
     * This method assumes that each of the k subarrays
     * data[low + i * (high - low + 1) / k
     * ...
     * low + (i + 1) * (high - low + 1) / k - 1],
     * for i = 0...k - 1, are sorted.
     * @param data  an array of integers
     * @param low   low index
     * @param high  high index
     * @param k     the k in k-way mergesort
     */
    public static void merge (int[] data, int low, int high, int k) {

        if (high < low + k) {
            // the subarray has k or fewer elements
            // just make one big heap and do deleteMins() on it
            Comparable[] subarray = new MergesortHeapNode[high - low + 1];
            for (int i = 0, j = low; i < subarray.length; i++, j++) {
                subarray[i] = new MergesortHeapNode(data[j], 0);
            }
            BinaryHeap heap = BinaryHeap.buildHeap(subarray);

            for (int j = low; j <= high; j++) {
                try {
                    data[j] = ((MergesortHeapNode) heap.deleteMin()).getKey();
                }
                catch (EmptyHeapException e) {
                    System.out.println ("Tried to delete from an empty heap.");
                }
            }

        } else {
            // create queue for each subarray
            Queue[] queues = new Queue[k];
            for (int i = 0; i < k; i++) {
                queues[i] = new LinkedList<MergesortHeapNode>();
            }

            // create a min heap populated with first element of each subarray
            BinaryHeap heap = new BinaryHeap();
            for (int i = 0; i < k; i++) {
                for (int j = low + i * (high - low + 1) / k;
                        j < low + (i + 1) * (high - low + 1) / k;
                        j++) {
                    //noinspection unchecked
                    queues[i].add(new MergesortHeapNode(data[j], i));
                }
                heap.insert((Comparable) queues[i].poll());
            }

            // to find each element pop from heap and replace from same subarray
            for (int i = low; i <= high; i++) {
                try {
                    MergesortHeapNode next
                            = (MergesortHeapNode) heap.deleteMin();
                    data[i] = next.getKey();
                    next = (MergesortHeapNode) queues[next.getWhichSubarray()].poll();
                    if (next != null) {
                        heap.insert(next);
                    }
                } catch (EmptyHeapException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Given an integer size, produce an array of size random integers.
     * The integers of the array are between 0 and size (inclusive) with
     * random uniform distribution.
     * @param size  the number of elements in the returned array
     * @return      an array of integers
     */
    public static int[] getRandomArrayOfIntegers(int size) {
        int[] data = new int[size];
        for (int i = 0; i < size; i++) {
            data[i] = (int) ((size + 1) * Math.random());
        }
        return data;
    }
    
    public static void main(String[] args) {
        System.out.print("n\\k");
        for (int rad : RADIX) {
            System.out.print("," + rad);
        }
        System.out.println();
        long t;
        for (int n : SIZES) {
            System.out.print(n);
            for (int k : RADIX) {
                t = 0;
                for (int i = 0; i < NUM_TESTS; i++) {
                    t += test(n, k);
                }
                t /= NUM_TESTS;
                System.out.print("," + t);
            }
            System.out.println();
        }
        System.exit(0);
    }

    /**
     * Tests the time to execute a k-way merge sort on a random array
     * @param n the size of the array
     * @param k the radix of the sort
     * @return the time in milliseconds to execute to sort
     */
    private static long test(int n, int k) {
        int[] data = getRandomArrayOfIntegers(n);
        long start, finish, ret;
        start = System.currentTimeMillis();
        kwayMergesort(data, k);
        finish = System.currentTimeMillis();
        ret = finish - start;
        if (!isSorted(data, n)) {
            System.err.println("Error: data not sorted");
            System.exit(-1);
        }
        return ret;
    }

    /**
     * Determines whether an array is sorted
     * @param data array of integers
     * @param size number of elements in array
     * @return <code>true</code> if every element of the array is less than
     * or equal to the next element and <code>false</code> otherwise
     */
    private static boolean isSorted(int[] data, int size) {
        for (int i = 0; i < size - 1; i++) {
            if (data[i] > data[i + 1])
                return false;
        }
        return true;
    }
}