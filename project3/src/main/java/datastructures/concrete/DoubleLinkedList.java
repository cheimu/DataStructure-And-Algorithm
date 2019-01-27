package datastructures.concrete;

import datastructures.interfaces.IList;
import misc.exceptions.EmptyContainerException;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Note: For more info on the expected behavior of your methods, see
 * the source code for IList.
 */
public class DoubleLinkedList<T> implements IList<T> {
    // You may not rename these fields or change their types.
    // We will be inspecting these in our private tests.
    // You also may not add any additional fields.
    private Node<T> front;
    private Node<T> back;
    private int size;

    public DoubleLinkedList() {
        this.front = null;
        this.back = null;
        this.size = 0;
    }

    @Override
    // This method adds a new item into the list at the end of the list.
    public void add(T item) {
        Node<T> newNode = new Node<T>(item);
        if (this.size() == 0) { // edge case when the list has nothing in
            this.front = newNode;
            this.back = newNode;
        } else { // normal case
            this.back.next = newNode;
            newNode.prev = this.back;
            this.back = this.back.next;
        }
        this.size++;
    }

    @Override
    // This method removes the last node in the list and return the item in 
    // that node. It will throw EmptyContainerException when the list has 
    // nothing to be removed.
    public T remove() {
        if (this.size() == 0) {
            throw new EmptyContainerException();
        }
        Node<T> newNode = this.back;
        if (this.size() == 1) { // remove the last object in the list
            this.front = null;
            this.back = null;
        } else { // normal case
            this.back = this.back.prev;
            this.back.next.prev = null;
            this.back.next = null;
        }
        this.size--;
        return newNode.data;
    }
    
    // This is a helper method we wrote to get a node in the list at given
    // index. It will throw the IndexOutOfBoundsException by itself, so in
    // some of the public methods when we need to throw this exception we can
    // just use this instead.
    private Node<T> getNode(int index) {
        if (index < 0 | index >= this.size()) {
            throw new IndexOutOfBoundsException();
        }// when the index given is out of bound for the list.
        Node<T> newNode;
        // For efficiency concern, we want to loop through the list in reverse 
        // order when the index we need to find is in the latter half of the list
        // since the list we have can traverse in both directions.
        if (index < this.size() / 2) { // loop through the list from the front
            newNode = this.front;
            for (int i = 0; i < index; i++) {
                newNode = newNode.next;
            }
        } else { // loop through the list from the back
            newNode = this.back;
            for (int i = 0; i < this.size() - index - 1; i++) {
                newNode = newNode.prev;
            }
        }
        return newNode;
    }
    
    @Override
    // This method gets the data inside the node at the given index. 
    public T get(int index) {
        Node<T> newNode = this.getNode(index);
        return newNode.data;
    }

    @Override
    // This method replaces the node at the given index with a new node
    // that contains the new item we have.
    public void set(int index, T item) {
        Node<T> info = new Node<T>(item);
        if (index == this.size() - 1) { // at the end of the list
            this.remove();
            this.add(item);
        } else if (index == 0) { // at the front of the list
            info.next = this.front.next;
            this.front.next.prev = info;
            this.front = info;
        } else { // in the middle
            Node<T> newNode = this.getNode(index);
            info.next = newNode.next;
            info.prev = newNode.prev;
            newNode.next.prev = info;
            newNode.prev.next = info;
            newNode.next = null;
            newNode.prev = null;
        }
    }

    @Override
    public void insert(int index, T item) {
        if (index < 0 | index >= (this.size() + 1)) {
            throw new IndexOutOfBoundsException();
        }
        Node<T> info = new Node<T>(item);
        if (index == this.size()) {
            this.add(item);
        } else {
            if (index == 0) {
                info.next = this.front;
                this.front.prev = info;
                this.front = this.front.prev;
            } else {
                Node<T> newNode = getNode(index);
                info.next = newNode;
                info.prev = newNode.prev;
                newNode.prev.next = info;
                newNode.prev = info;
            }
            this.size++;
        }
    }

    @Override
    public T delete(int index) {
        Node<T> newNode = getNode(index);
        if (index == this.size() - 1) {
            this.remove();
        } else {
            if (index == 0) {
                this.front = this.front.next;
                this.front.prev = null;
            } else {
                newNode.next.prev = newNode.prev;
                newNode.prev.next = newNode.next;
            }
            this.size--;
        }
        return newNode.data;
    }

    @Override
    public int indexOf(T item) {
        Node<T> point = this.front;
        for (int count = 0; count < this.size(); count++) {
            if ((item == null & point.data == null) || (point.data.equals(item))) {
                return count;
             } else {
                 point = point.next;
             }
        }
        return -1;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean contains(T other) {
        return this.indexOf(other) != -1;
    }

    @Override
    public Iterator<T> iterator() {
        // Note: we have provided a part of the implementation of
        // an iterator for you. You should complete the methods stubs
        // in the DoubleLinkedListIterator inner class at the bottom
        // of this file. You do not need to change this method.
        return new DoubleLinkedListIterator<>(this.front);
    }

    private static class Node<E> {
        // You may not change the fields in this node or add any new fields.
        public final E data;
        public Node<E> prev;
        public Node<E> next;

        public Node(Node<E> prev, E data, Node<E> next) {
            this.data = data;
            this.prev = prev;
            this.next = next;
        }

        public Node(E data) {
            this(null, data, null);
        }

        // Feel free to add additional constructors or methods to this class.
    }

    private static class DoubleLinkedListIterator<T> implements Iterator<T> {
        // You should not need to change this field, or add any new fields.
        private Node<T> current;

        public DoubleLinkedListIterator(Node<T> current) {
            // You do not need to make any changes to this constructor.
            this.current = current;
        }

        /**
         * Returns 'true' if the iterator still has elements to look at;
         * returns 'false' otherwise.
         */
        public boolean hasNext() {
            return current != null;
        }

        /**
         * Returns the next item in the iteration and internally updates the
         * iterator to advance one element forward.
         *
         * @throws NoSuchElementException if we have reached the end of the iteration and
         *         there are no more elements to look at.
         */
        public T next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            T data = current.data;
            current = current.next;
            return data;
        }
    }
}
