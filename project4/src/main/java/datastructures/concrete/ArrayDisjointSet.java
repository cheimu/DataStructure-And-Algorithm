package datastructures.concrete;

import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IDisjointSet;

/**
 * See IDisjointSet for more details.
 */
public class ArrayDisjointSet<T> implements IDisjointSet<T> {
    // Note: do NOT rename or delete this field. We will be inspecting it
    // directly within our private tests.
    private int[] pointers;

    // However, feel free to add more methods and private helper methods.
    // You will probably need to add one or two more fields in order to
    // successfully implement this class.
    private IDictionary<T, Integer> items;
    private int length;

    public ArrayDisjointSet() {
        this.pointers = new int[1];
        this.items = new ChainedHashDictionary<T, Integer>();
        this.length = 0;
        
    }
    
    private void resize() {
        if (this.length == this.pointers.length) {
            int[] newP = new int[2 * this.length];
            for (int i = 0; i < this.length; i++) {
                newP[i] = this.pointers[i];
            }
            this.pointers = newP;
        }
    }

    @Override
    public void makeSet(T item) {
        this.pointers[length] = -1;
        this.items.put(item, length);
        length++;
        resize();
    }

    @Override
    public int findSet(T item) {
        if (!this.items.containsKey(item)) {
            throw new IllegalArgumentException();
        }
        int index = find(this.items.get(item));
        return index; 
    }
    
    private int find(int now) {
        if (this.pointers[now] >= 0) {
            int output = find(this.pointers[now]);
            this.pointers[now] = output;
            return output;
        } else {
            return now;
        }
    }

    @Override
    public void union(T item1, T item2) {
        int index1 = findSet(item1);
        int index2 = findSet(item2);
        if (index1 == index2) {
            throw new IllegalArgumentException();
        }
        int rank1 = 0 - (1 + this.pointers[index1]);
        int rank2 = 0 - (1 + this.pointers[index2]);
        if (rank1 > rank2) {
            this.pointers[index2] = index1;
        } else if (rank2 > rank1) {
            this.pointers[index1] = index2;
        } else {
            this.pointers[index2] = index1;
            this.pointers[index1]--;
        }
    }
}
