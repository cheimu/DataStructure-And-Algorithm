package datastructures.sorting;

import misc.BaseTest;
import misc.Searcher;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

import org.junit.Test;

import datastructures.concrete.ArrayHeap;
import datastructures.concrete.DoubleLinkedList;
import datastructures.interfaces.IList;
import datastructures.interfaces.IPriorityQueue;

/**
* See spec for details on what kinds of tests this class should include.
*/
public class TestSortingStress extends BaseTest {
    protected <T extends Comparable<T>> IPriorityQueue<T> makeInstance() {
        return new ArrayHeap<>();
    }
    
    @Test(timeout=10*SECOND)
    public void stressTestInsert() {
        int size = 10000000;
        IPriorityQueue<Integer> heap = new ArrayHeap<Integer>();
        for (int i = 0; i < size; i++) {
            heap.insert(i);
        }
        assertEquals(heap.size(), size);
    }
    
    @Test(timeout=10*SECOND)
    public void stressTestRemoveMin() {
        int size = 10000000;
        IPriorityQueue<Integer> heap = new ArrayHeap<Integer>();
        for (int i = 0; i < size; i++) {
            heap.insert(i);
        }
        for (int i = 0; i < size; i++) {
            assertEquals(heap.removeMin(), i);
        }
    }
    
    @Test(timeout=10*SECOND)
    public void stressTestRandomInsertion() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        Random rand = new Random();
        int[] list = new int[1000000];
        for (int i = 0; i < 1000000; i++) {
            int element = rand.nextInt(1000);
            list[i] = element;
            heap.insert(element);
        }
        Arrays.sort(list);
        for (int i = 0; i < 1000000; i++) {
            int element = heap.removeMin();
            assertEquals(element, list[i]);
        }
    }
    
    @Test(timeout=10*SECOND)
    public void stressTestSort() {
        int size = 1000000;
        int[] list = new int[1000000];
        IList<Integer> list2 = new DoubleLinkedList<Integer>();
        Random rand = new Random();
        for (int i = 0; i < size; i++) {
            int temp = rand.nextInt(1000);
            list[i] = temp;
            list2.add(temp);
        }
        Arrays.sort(list);
        IList<Integer> top = Searcher.topKSort(size, list2);
        Iterator<Integer> itr = top.iterator();
        for (int i = 0; i < size; i++) {
            assertEquals(list[i], itr.next());
        }
    }
    
    @Test(timeout=10*SECOND)
    public void stressTestReverseOrderSort() {
        int size = 1000000;
        int[] list = new int[1000000];
        IList<Integer> list2 = new DoubleLinkedList<Integer>();
        for (int i = 0; i < size; i++) {
            int temp = size - i;
            list[i] = temp;
            list2.add(temp);
        }
        Arrays.sort(list);
        IList<Integer> top = Searcher.topKSort(size, list2);
        Iterator<Integer> itr = top.iterator();
        for (int i = 0; i < size; i++) {
            assertEquals(list[i], itr.next());
        }
    }
    
    @Test(timeout=10*SECOND)
    public void stressTestOrderedSort() {
        int size = 10000000;
        int[] list = new int[10000000];
        IList<Integer> list2 = new DoubleLinkedList<Integer>();
        for (int i = 0; i < size; i++) {
            int temp = i;
            list[i] = temp;
            list2.add(temp);
        }
        Arrays.sort(list);
        IList<Integer> top = Searcher.topKSort(size, list2);
        Iterator<Integer> itr = top.iterator();
        for (int i = 0; i < size; i++) {
            assertEquals(list[i], itr.next());
        }
    }
    
    @Test(timeout=10*SECOND)
    public void stressTestGiganticK() {
        int size = 5000000;
        int[] list = new int[5000000];
        IList<Integer> list2 = new DoubleLinkedList<Integer>();
        for (int i = 0; i < size; i++) {
            int temp = i;
            list[i] = temp;
            list2.add(temp);
        }
        Arrays.sort(list);
        IList<Integer> top = Searcher.topKSort(2500000, list2);
        Iterator<Integer> itr = top.iterator();
        for (int i = 2500000; i < size; i++) {
            assertEquals(list[i], itr.next());
        }
    }
}