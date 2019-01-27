package datastructures.concrete;

import java.util.NoSuchElementException;

import datastructures.interfaces.IPriorityQueue;
import misc.exceptions.EmptyContainerException;

/**
 * See IPriorityQueue for details on what each method must do.
 */
public class ArrayHeap<T extends Comparable<T>> implements IPriorityQueue<T> {
    // See spec: you must implement a implement a 4-heap.
    private static final int NUM_CHILDREN = 4;

    // You MUST use this field to store the contents of your heap.
    // You may NOT rename this field: we will be inspecting it within
    // our private tests.
    private T[] heap;

    // Feel free to add more fields and constants.
    private int length;
    private int layers;

    public ArrayHeap() {
        this.heap = makeArrayOfT(1);
        this.length = 0;
        this.layers = 1;
    }

    /**
     * This method will return a new, empty array of the given size
     * that can contain elements of type T.
     *
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private T[] makeArrayOfT(int size) {
        // This helper method is basically the same one we gave you
        // in ArrayDictionary and ChainedHashDictionary.
        //
        // As before, you do not need to understand how this method
        // works, and should not modify it in any way.
        return (T[]) (new Comparable[size]);
    }
    
    @Override
    public T removeMin() {
        if (this.length == 0) {
            throw new EmptyContainerException();
        }

        T item = this.heap[0];
        this.heap[0] = this.heap[this.length - 1];
        this.heap[this.length - 1] = null;
        this.length--;
        exchangeDown(0, 1);
        return item;
    }
    
    private void exchangeDown(int rootIndex, int layer) {
        if (layer <= this.layers - 1) {
            
            int[] childIndexs = new int[NUM_CHILDREN];
            T min = this.heap[rootIndex];
            int minIndex = rootIndex;
            for (int i = 1; i <= NUM_CHILDREN; i++) {
                childIndexs[i - 1] = rootIndex * NUM_CHILDREN + i;
                if (this.heap[childIndexs[i - 1]] != null) {
                    if (this.heap[childIndexs[i - 1]].compareTo(min) < 0) {
                        min = this.heap[childIndexs[i - 1]];
                        minIndex = childIndexs[i - 1];
                    }
                }
            }
            this.heap[minIndex] = this.heap[rootIndex];
            this.heap[rootIndex] = min;
            exchangeDown(minIndex, layer + 1);
        }
    }

    @Override
    public T peekMin() {
        if (this.length == 0) {
            throw new EmptyContainerException();
        }
        return this.heap[0];
    }

    @Override
    public void insert(T item) {
        if (item == null) {
            throw new IllegalArgumentException("The item is null");
        }
        if (this.length == this.heap.length) {
            resize();
        }
        this.heap[this.length] = item;
        exchange(this.length);
        this.length++;
    }
    
    private void resize() {
        T[] newHeap = makeArrayOfT((int) Math.pow(NUM_CHILDREN, this.layers) + this.heap.length);
        this.layers++;
        for (int i = 0; i < this.length; i++) {
            newHeap[i] = this.heap[i];
        }
        this.heap = newHeap;
    }
    
    private void exchange(int childIndex) {
        int rootIndex = (childIndex - 1) / NUM_CHILDREN;
        T root = this.heap[rootIndex];
        T child = this.heap[childIndex];
        int move = root.compareTo(child);
        if (move > 0) {
            this.heap[rootIndex] = child;
            this.heap[childIndex] = root;
            if (rootIndex != 0) {
                exchange(rootIndex);
            }
        } 
    }

    @Override
    public int size() {
        return this.length;
    }

    @Override
    public void remove(T item) {
        int i = 0;
        while (!item.equals(this.heap[i]) && i < this.length) {
            i++;
            
        }
        if (i == this.length) {
            throw new NoSuchElementException();
        }
        int layer;
        if (i == 0) {
            layer = 1;
        } else {
            layer = (int) (Math.ceil(Math.log(i) / Math.log(4)) + 1);
        }
        this.heap[i] = this.heap[this.length - 1];
        this.heap[this.length - 1] = null;
        this.length--;
        exchangeDown(i, layer);
    }
}
