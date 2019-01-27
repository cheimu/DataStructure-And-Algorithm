package datastructures.sorting;

import misc.BaseTest;
import datastructures.concrete.DoubleLinkedList;
import datastructures.interfaces.IList;
import misc.Searcher;
import org.junit.Test;
import static org.junit.Assert.fail;

/**
* See spec for details on what kinds of tests this class should include.
*/
public class TestTopKSortFunctionality extends BaseTest {
    @Test(timeout=SECOND)
    public void testGeneral() {
        IList<Integer> list = new DoubleLinkedList<>();
        for (int i = 0; i < 20; i++) {
            list.add(i);
        }
        
        IList<Integer> listSorted = Searcher.topKSort(5, list);
        assertEquals(5, listSorted.size());
        for (int i = 0; i < listSorted.size(); i++) {
            assertEquals(15 + i, listSorted.get(i));
        }
    }
    
    @Test(timeout=SECOND)
    public void testChar() {
        IList<Character> list = new DoubleLinkedList<Character>();
        for (int i = 0; i < 20; i++) {
            list.add((char) ('a' + i));
        }
        IList<Character> listSorted = Searcher.topKSort(5, list);
        assertEquals(5, listSorted.size());
        for (int i = 0; i < 5; i++) {
            assertEquals((char) ('a' + 15 + i), listSorted.get(i));
        }
    }

    @Test(timeout=SECOND)
    public void testZeroK() {
        IList<Integer> list = new DoubleLinkedList<>();
        for (int i = 0; i < 20; i++) {
            list.add(i);
        }
        IList<Integer> listSorted = Searcher.topKSort(0, list);
        assertEquals(true, listSorted.isEmpty());
    }

    @Test(timeout=SECOND)
    public void testIllegalK() {
        IList<Integer> list = new DoubleLinkedList<>();
        for (int i = 0; i < 20; i++) {
            list.add(i);
        }
        try {
            Searcher.topKSort(-1, list);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // Do nothing: this is ok
        }
        
    }

    @Test(timeout=SECOND)
    public void testKGreaterThanSize() {
        IList<Integer> list = new DoubleLinkedList<>();
        for (int i = 0; i < 5; i++) {
            list.add(i);
        }
        IList<Integer> listSorted = Searcher.topKSort(20, list);
        assertEquals(5, listSorted.size());
        for (int i = 0; i < listSorted.size(); i++) {
            assertEquals(i, listSorted.get(i));
        }
    }

    @Test(timeout=SECOND)
    public void testEmptyInput() {
        IList<Integer> list = new DoubleLinkedList<>();
        IList<Integer> listSorted = Searcher.topKSort(5, list);
        assertEquals(true, listSorted.isEmpty());
    }
    
    @Test(timeout=SECOND)
    public void testGapInput() {
        IList<Integer> list = new DoubleLinkedList<>();
        list.add(1);
        list.add(2);
        list.add(null);
        list.add(3);
        try {
            Searcher.topKSort(4, list);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // Do nothing: this is ok
        }
    }
}
