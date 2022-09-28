package dummy;

import java.io.IOException;
import java.util.Arrays;

public class QuickSortTest {

    public static void main(String[] args) throws IOException {

        int cnt = 100000;
        int[] arr1 = new int[cnt];
        int[] arr2 = new int[cnt];
        int[] arr3 = new int[cnt];
        int[] arr4 = new int[cnt];
        int[] arr5 = new int[cnt];
        int[] arr6 = new int[cnt];
        for (int i = 0; i < arr1.length; i++) {
            int rand = (int) (Math.random() * 100 + 1);
            arr1[i] = rand;
            arr2[i] = rand;
            arr3[i] = rand;
            arr4[i] = rand;
            arr5[i] = rand;
            arr6[i] = rand;
        }

        long beforeTime = System.currentTimeMillis();
        leftQucikSort(arr1);
        // System.out.println(Arrays.toString(arr1));
        long afterTime = System.currentTimeMillis();
        System.out.println(+(afterTime - beforeTime) + "ms: LeftQuickSort");

        beforeTime = System.currentTimeMillis();
        quickSort(arr2);
        // System.out.println(":"+count1);
        // System.out.println(Arrays.toString(arr2));
        afterTime = System.currentTimeMillis();
        System.out.println((afterTime - beforeTime) + "ms: CenterQuickSort");

        beforeTime = System.currentTimeMillis();
        dualPivotQuickSort(arr3);
        // System.out.println(Arrays.toString(arr3));
        afterTime = System.currentTimeMillis();
        System.out.println((afterTime - beforeTime) + "ms: DualCenterQucikSort");

        beforeTime = System.currentTimeMillis();
        quickSortThread(arr4);
        afterTime = System.currentTimeMillis();
        System.out.println((afterTime - beforeTime) + "ms: ThreadCenterQuickSort");

        beforeTime = System.currentTimeMillis();
        Arrays.sort(arr5);
        afterTime = System.currentTimeMillis();
        System.out.println((afterTime - beforeTime) + "ms: Arrays.sort");

        beforeTime = System.currentTimeMillis();
        dualQuickSortThread(arr6);
        afterTime = System.currentTimeMillis();
        // System.out.println(Arrays.toString(arr6));
        System.out.println("DualThr:" + (afterTime - beforeTime) + "ms");

    }

    public static void leftQucikSort(int[] a) {
        l_pivot_sort(a, 0, a.length - 1);
    }

    public static void l_pivot_sort(int[] a, int lo, int hi) {
        if (lo >= hi)
            return;

        int pivot = partition2(a, lo, hi);
        l_pivot_sort(a, lo, pivot - 1);
        l_pivot_sort(a, pivot + 1, hi);
    }

    public static int partition2(int[] a, int left, int right) {

        int lo = left;
        int hi = right;
        int pivot = a[left];
        while (lo < hi) {
            while (lo < hi && a[hi] > pivot)
                hi--;
            while (lo < hi && a[lo] <= pivot)
                lo++;
            swap(a, lo, hi);
        }

        swap(a, left, lo);
        return lo;
    }

    public static void quickSortThread(int[] arr) {
        quickSortThread(arr, 0, arr.length - 1);
    }

    public static void quickSortThread(int[] a, int lo, int hi) {
        if (lo >= hi) {
            return;
        }
        int pivot = partition(a, lo, hi);

        Thread thread1 = new Thread(new TreadTest(a, lo, pivot));
        Thread thread2 = new Thread(new TreadTest(a, pivot + 1, hi));
        thread1.start();
        thread2.start();
        try {
            thread1.join();
            thread2.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void quickSort(int[] arr) {
        quickSort(arr, 0, arr.length - 1);
    }

    public static void quickSort(int[] a, int lo, int hi) {
        if (lo >= hi) {
            return;
        }
        int pivot = partition(a, lo, hi);
        quickSort(a, lo, pivot);
        quickSort(a, pivot + 1, hi);
    }

    public static int partition(int[] a, int left, int right) {

        int lo = left - 1;
        int hi = right + 1;
        int pivot = a[(left + right) / 2];

        while (true) {
            do {
                lo++;
            } while (a[lo] < pivot);

            do {
                hi--;
            } while (a[hi] > pivot);

            if (lo >= hi) {
                return hi;
            }

            swap(a, lo, hi);
        }

    }

    public static void swap(int[] arr, int a, int b) {
        int temp = arr[a];
        arr[a] = arr[b];
        arr[b] = temp;
    }

    public static void dualQuickSortThread(int[] arr) {
        dualQuickSortThread(arr, 0, arr.length - 1);
    }

    public static void dualQuickSortThread(int[] a, int lo, int hi) {
        if (lo < hi) {

            int[] piv = dulPartition(a, lo, hi);

            Thread thread3 = new Thread(new TreadTest2(a, lo, piv[0] - 1));
            Thread thread4 = new Thread(new TreadTest2(a, piv[0] + 1, piv[1] - 1));
            Thread thread5 = new Thread(new TreadTest2(a, piv[1] + 1, hi));
            thread3.start();
            thread4.start();
            thread5.start();
            try {
                thread3.join();
                thread4.join();
                thread5.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void dualPivotQuickSort(int[] arr) {
        dualPivotQuickSort(arr, 0, arr.length - 1);
    }

    static void dualPivotQuickSort(int[] arr, int low, int high) {
        if (low < high) {
            int[] piv;
            piv = dulPartition(arr, low, high);

            dualPivotQuickSort(arr, low, piv[0] - 1);
            dualPivotQuickSort(arr, piv[0] + 1, piv[1] - 1);
            dualPivotQuickSort(arr, piv[1] + 1, high);
        }
    }

    static int[] dulPartition(int[] arr, int low, int high) {
        if (arr[low] > arr[high])
            swap(arr, low, high);

        // p is the left pivot, and q
        // is the right pivot.
        int j = low + 1;
        int g = high - 1, k = low + 1,
                p = arr[low], q = arr[high];

        while (k <= g) {
            if (arr[k] < p) {
                swap(arr, k, j);
                j++;
            } else if (arr[k] >= q) {
                while (arr[g] > q && k < g)
                    g--;

                swap(arr, k, g);
                g--;

                if (arr[k] < p) {
                    swap(arr, k, j);
                    j++;
                }
            }
            k++;
        }
        j--;
        g++;

        swap(arr, low, j);
        swap(arr, high, g);

        return new int[] { j, g };
    }
}

class TreadTest implements Runnable {
    int[] arr;
    int low;
    int high;

    TreadTest(int[] a, int lo, int hi) {
        arr = a;
        low = lo;
        high = hi;
    }

    public void run() {

        QuickSortTest.quickSort(arr, low, high);
    }
}

class TreadTest2 implements Runnable {
    int[] arr;
    int low;
    int high;

    TreadTest2(int[] a, int lo, int hi) {
        arr = a;
        low = lo;
        high = hi;
    }

    public void run() {
        QuickSortTest.dualQuickSortThread(arr, low, high);
    }
}