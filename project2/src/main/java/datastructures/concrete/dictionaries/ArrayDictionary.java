package datastructures.concrete.dictionaries;

import java.util.Iterator;
import java.util.NoSuchElementException;

import datastructures.concrete.KVPair;
import datastructures.interfaces.IDictionary;
import misc.exceptions.NoSuchKeyException;

/**
 * See IDictionary for more details on what this class should do
 */
public class ArrayDictionary<K, V> implements IDictionary<K, V> {
    // You may not change or rename this field: we will be inspecting
    // it using our private tests.
    private Pair<K, V>[] pairs;

    // You're encouraged to add extra fields (and helper methods) though!
    private int size;
    
    // []
    public ArrayDictionary() {
        size = 0;
        pairs = this.makeArrayOfPairs(1);
    }
    // [<1,2>, <a,b>]
    /**
     * This method will return a new, empty array of the given size
     * that can contain Pair<K, V> objects.
     *
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private Pair<K, V>[] makeArrayOfPairs(int arraySize) {
        // It turns out that creating arrays of generic objects in Java
        // is complicated due to something known as 'type erasure'.
        //
        // We've given you this helper method to help simplify this part of
        // your assignment. Use this helper method as appropriate when
        // implementing the rest of this class.
        //
        // You are not required to understand how this method works, what
        // type erasure is, or how arrays and generics interact. Do not
        // modify this method in any way.
        return (Pair<K, V>[]) (new Pair[arraySize]);

    }
    
    private int find(K key) {
        for (int i = 0; i < size; i++) {
            if ((key == null && pairs[i].key == null) || (pairs[i].key != null && this.pairs[i].key.equals(key))) {
                return i;
            }
        }
        return -1;
    }
    
    @Override
    public V get(K key) {
        int position = this.find(key);
        if (position == -1) {
            throw new NoSuchKeyException();
        }
        return this.pairs[position].value;
    }
    
    private void resize() {
        if (this.pairs[this.pairs.length - 1] != null) {
            Pair<K, V>[] newPairs = makeArrayOfPairs(2 * this.pairs.length);
            for (int i = 0; i < this.pairs.length; i++) {
                newPairs[i] = this.pairs[i];
            }
            this.pairs = newPairs;
        }
    }

    @Override
    public void put(K key, V value) {
        int position = this.find(key);
        if (position != -1) {
            this.pairs[position].value = value;
        } else {
            this.resize();
            this.pairs[size] = new Pair<K, V>(key, value);
            this.size++;
        }
    }

    @Override
    public V remove(K key) {
        int position = this.find(key);
        if (position == -1) {
            throw new NoSuchKeyException();
        }
        V value = this.pairs[position].value;
        this.pairs[position] = null;
        for (int i = position; i < this.size - 1; i++) {
            this.pairs[i] = this.pairs[i + 1];
        }
        this.size--;
        return value;
    }

    @Override
    public boolean containsKey(K key) {
        int position = this.find(key);
        return position != -1;
    }

    @Override
    public int size() {
        return this.size;
    }
    
    @Override
    public Iterator<KVPair<K, V>> iterator() {
        return new ArrayDictionaryIterator<>(this.pairs);
    }

    private static class ArrayDictionaryIterator<K, V> implements Iterator<KVPair<K, V>> {
        private Pair<K, V>[] pairs;
        private Pair<K, V> current;
        private int index;

        public ArrayDictionaryIterator(Pair<K, V>[] pairs) {
            this.pairs = pairs;
            this.index = 0;
            this.current = this.pairs[index];
        }

        public boolean hasNext() {
            return current != null;
        }

        public KVPair<K, V> next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            index++;
            KVPair<K, V> data = new KVPair<K, V>(current.key, current.value);
            if (index < pairs.length) {
                this.current = this.pairs[index];
            } else  {
                this.current = null;
            }
            return data;
        }
    }

    private static class Pair<K, V> {
        public K key;
        public V value;

        // You may add constructors and methods to this class as necessary.
        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return this.key + "=" + this.value;
        }
    }
}
