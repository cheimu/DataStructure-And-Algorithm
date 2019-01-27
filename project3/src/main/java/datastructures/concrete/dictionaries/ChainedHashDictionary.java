package datastructures.concrete.dictionaries;

import datastructures.concrete.KVPair;
import datastructures.interfaces.IDictionary;


import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * See the spec and IDictionary for more details on what each method should do
 */
public class ChainedHashDictionary<K, V> implements IDictionary<K, V> {
    // You may not change or rename this field: we will be inspecting
    // it using our private tests.
    private IDictionary<K, V>[] chains;

    // You're encouraged to add extra fields (and helper methods) though!
    private int size;
    private int arraySize;
    
    public ChainedHashDictionary() {
        this.size = 0;
        this.arraySize = 16;
        this.chains = makeArrayOfChains(this.arraySize);
        for (int i = 0; i < this.arraySize; i++) {
            chains[i] = new ArrayDictionary<K, V>();
        }
    }

    /**
     * This method will return a new, empty array of the given size
     * that can contain IDictionary<K, V> objects.
     *
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private IDictionary<K, V>[] makeArrayOfChains(int chainSize) {
        // Note: You do not need to modify this method.
        // See ArrayDictionary's makeArrayOfPairs(...) method for
        // more background on why we need this method.
        return (IDictionary<K, V>[]) new IDictionary[chainSize];
    }

    private int sort(K key) {
        int code;
        if (key == null) {
            code = 0;
        } else {
            code = Math.abs(key.toString().hashCode() % this.arraySize);
        }
        return code;
    }
    
    private void resize() {
        double lambda = size / arraySize;
        if (lambda > 0.75) {
            this.arraySize *= 2;
            IDictionary<K, V>[] newChains = makeArrayOfChains(arraySize);
            for (int i = 0; i < this.arraySize; i++) {
                newChains[i] = new ArrayDictionary<K, V>();
            }
            for (IDictionary<K, V> bucket : this.chains) {
                for (KVPair<K, V> pair : bucket) {
                    int code;
                    if (pair.getKey() == null) {
                        code = 0;
                    } else {
                        code = Math.abs(pair.getKey().toString().hashCode() % arraySize);
                    }
                    IDictionary<K, V> newBucket = newChains[code];
                    newBucket.put(pair.getKey(), pair.getValue());
                }
            }
            this.chains = newChains;
        }
    }
    
    @Override
    public V get(K key) {
        int inBucket = this.sort(key);
        V value = this.chains[inBucket].get(key);
        return value;
    }

    @Override
    public void put(K key, V value) {
        if (!this.containsKey(key)) {
            this.size++;
        }
        int toBucket = this.sort(key);
        IDictionary<K, V> bucket = this.chains[toBucket];
        bucket.put(key, value);
        this.resize();
    }

    @Override
    public V remove(K key) {
        int inBucket = this.sort(key);
        V value = this.chains[inBucket].remove(key);
        size--;
        return value;
    }

    @Override
    public boolean containsKey(K key) {
        int inBucket = this.sort(key);
        return this.chains[inBucket].containsKey(key);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Iterator<KVPair<K, V>> iterator() {
        // Note: you do not need to change this method
        return new ChainedIterator<>(this.chains);
    }

    /**
     * Hints:
     *
     * 1. You should add extra fields to keep track of your iteration
     *    state. You can add as many fields as you want. If it helps,
     *    our reference implementation uses three (including the one we
     *    gave you).
     *
     * 2. Think about what exactly your *invariants* are. Once you've
     *    decided, write them down in a comment somewhere to help you
     *    remember.
     *
     * 3. Before you try and write code, try designing an algorithm
     *    using pencil and paper and run through a few examples by hand.
     *
     *    We STRONGLY recommend you spend some time doing this before
     *    coding. Getting the invariants correct can be tricky, and
     *    running through your proposed algorithm using pencil and
     *    paper is a good way of helping you iron them out.
     *
     * 4. Think about what exactly your *invariants* are. As a 
     *    reminder, an *invariant* is something that must *always* be 
     *    true once the constructor is done setting up the class AND 
     *    must *always* be true both before and after you call any 
     *    method in your class.
     *
     *    Once you've decided, write them down in a comment somewhere to
     *    help you remember.
     *
     *    You may also find it useful to write a helper method that checks
     *    your invariants and throws an exception if they're violated.
     *    You can then call this helper method at the start and end of each
     *    method if you're running into issues while debugging.
     *
     *    (Be sure to delete this method once your iterator is fully working.)
     *
     * Implementation restrictions:
     *
     * 1. You **MAY NOT** create any new data structures. Iterators
     *    are meant to be lightweight and so should not be copying
     *    the data contained in your dictionary to some other data
     *    structure.
     *
     * 2. You **MAY** call the `.iterator()` method on each IDictionary
     *    instance inside your 'chains' array, however.
     */
    private static class ChainedIterator<K, V> implements Iterator<KVPair<K, V>> {
        private IDictionary<K, V>[] chains;
        private Iterator<KVPair<K, V>> current;
        private int index;

        public ChainedIterator(IDictionary<K, V>[] chains) {
            this.chains = chains;
            this.index = 0;
            this.current = chains[0].iterator();
            while (!current.hasNext() && this.hasNext()) {
                index++;
                current = chains[this.index].iterator();
            }
        }

        @Override
        public boolean hasNext() {
            if (this.current.hasNext()) {
                return true;
            } else {
                int n = this.index;
                while (n < this.chains.length - 1) {
                    n++;
                    if (!chains[n].isEmpty()) {
                        return true;
                    }
                }
                return false;
            }
        }

        @Override
        public KVPair<K, V> next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }  else {
                if (this.current.hasNext()) {
                    KVPair<K, V> data = this.current.next();
                    return data;
                } else {
                    this.index++;
                    while (this.index < this.chains.length - 1 && this.chains[this.index].isEmpty()) {
                        this.index++;
                    }
                    this.current = this.chains[this.index].iterator();
                    KVPair<K, V> data = this.current.next();
                    return data;
                }
            }
        }
    }
}
