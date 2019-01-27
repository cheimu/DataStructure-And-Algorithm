package datastructures.sorting;

import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Random;

import misc.BaseTest;
import datastructures.concrete.ArrayHeap;
import datastructures.interfaces.IPriorityQueue;
import misc.exceptions.EmptyContainerException;
import org.junit.Test;

/**
* See spec for details on what kinds of tests this class should include.
*/
public class TestArrayHeapFunctionality extends BaseTest {
    protected <T extends Comparable<T>> IPriorityQueue<T> makeInstance() {
        return new ArrayHeap<>();
    }
    
    @Test(timeout=SECOND)
    public void testSize() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        heap.insert(4);
        heap.insert(-1);
        assertEquals(2, heap.size());
    }
    
    @Test(timeout=SECOND)
    public void testPeekAndRemoveMin() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        for (int i = 0; i < 1000; i++) {
            heap.insert(i);
        }
        for (int i = 0; i < 1000; i++) {
            assertEquals(i, heap.peekMin());
            assertEquals(1000 - i, heap.size());
            assertEquals(heap.removeMin(), i);
        }
    }
    
    @Test(timeout=SECOND)
    public void testRemoveMinCorrectness() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        for (int i = 0; i < 10; i++) {
            heap.insert(i);
        }
        for (int i = 0; i < 10; i++) {
            int peek = heap.peekMin();
            int removed = heap.removeMin();
            assertEquals(peek, removed);
        }
    }
    
    @Test(timeout=SECOND)
    public void testRemoveMinException() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        try {
            heap.removeMin();
            fail("Expected EmptyContainerException");
        } catch (EmptyContainerException ex) {
            // do nothing: this is ok
        }
    }
    
    @Test(timeout=SECOND)
    public void testRemoveMinNegative() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        for (int i = -30; i < 10; i++) {
            heap.insert(i);
        }
        for (int i = -30; i < 10; i++) {
            assertEquals(i, heap.peekMin());
            assertEquals(10 - i, heap.size());
            heap.removeMin();
        }
    }
    
    @Test(timeout=SECOND)
    public void testPeekMinException() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        try {
            heap.peekMin();
            fail("Expected EmptyContainerException");
        } catch (EmptyContainerException ex) {
            // Do nothing: this is ok
        }
    }
    
    @Test(timeout=SECOND)
    public void testInsertInteger() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        for (int i = 0; i > -100; i--) {
            heap.insert(i);
            assertEquals(i, heap.peekMin());
            assertEquals(-i + 1, heap.size());
        }
    }
    
    @Test(timeout=SECOND)
    public void testInsertChar() {
        IPriorityQueue<Character> heap = this.makeInstance();
        for (int i = 0; i > -20; i--) {
            char c = (char) ('z' + i);
            heap.insert(c);
            assertEquals((char) ('z' + i), heap.peekMin());
            assertEquals(-i + 1, heap.size());
        }
    }
    
    @Test(timeout=SECOND)
    public void testInsertException() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        try {
            heap.insert(null);
            fail("Expected IllegalArgumentEception");
        } catch (IllegalArgumentException ex) {
            // do nothing: this is ok
        }
    }
    
    @Test(timeout=SECOND)
    public void testRandomInsertion() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        Random rand = new Random();
        int[] list = new int[100];
        for (int i = 0; i < 100; i++) {
            int element = rand.nextInt(1000);
            list[i] = element;
            heap.insert(element);
        }
        Arrays.sort(list);
        for (int i = 0; i < 100; i++) {
            int element = heap.removeMin();
            assertEquals(element, list[i]);
        }
    }
    
    @Test(timeout=SECOND)
    public void testGeneral() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 3; j++) {
                heap.insert(1);
            }
            for (int j = 0; j < 4; j++) {
                heap.insert(0);
            }
            for (int j = 0; j < 5; j++) {
                heap.insert(-1);
            }
        }
        for (int i = 0; i < 50; i++) {
            assertEquals(heap.removeMin(), -1);
        }
        for (int i = 0; i < 40; i++) {
            assertEquals(heap.removeMin(), 0);
        }
        for (int i = 0; i < 30; i++) {
            assertEquals(heap.removeMin(), 1);
        }
    }
}