package org.qpneruy.clashArena.utils.enums;

import org.junit.jupiter.api.Test;
import org.qpneruy.clashArena.utils.Pair;
import org.qpneruy.clashArena.utils.godQueue;

import java.util.Iterator;
import java.util.NoSuchElementException;
import static org.junit.jupiter.api.Assertions.*;

public class godQueueTest {

    @Test
    public void testAddAndContains() {
        godQueue<String> queue = new godQueue<>();
        assertTrue(queue.add("A"));
        assertTrue(queue.contains("A"));
        assertFalse(queue.add("A")); // Adding the same element again should return false
        assertTrue(queue.add("B"));
        assertTrue(queue.contains("B"));
        assertEquals(2, queue.size());
    }

    @Test
    public void testRemove() {
        godQueue<Integer> queue = new godQueue<>();
        queue.add(1);
        queue.add(2);
        queue.add(3);
        assertTrue(queue.remove(2));
        assertFalse(queue.contains(2));
        assertEquals(2, queue.size());
        assertTrue(queue.remove(1));
        assertTrue(queue.remove(3));
        assertEquals(0, queue.size());
        assertTrue(queue.isEmpty());
        assertFalse(queue.remove(4)); // Removing a non-existent element
    }

    @Test
    public void testGet() {
        godQueue<String> queue = new godQueue<>();
        queue.add("X");
        queue.add("Y");
        queue.add("Z");
        assertEquals("X", queue.get(0));
        assertEquals("Y", queue.get(1));
        assertEquals("Z", queue.get(2));

        assertThrows(IndexOutOfBoundsException.class, () -> queue.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> queue.get(3));
    }

    @Test
    public void testSizeAndIsEmpty() {
        godQueue<String> queue = new godQueue<>();
        assertTrue(queue.isEmpty());
        assertEquals(0, queue.size());

        queue.add("One");
        assertFalse(queue.isEmpty());
        assertEquals(1, queue.size());

        queue.add("Two");
        assertEquals(2, queue.size());

        queue.remove("One");
        assertEquals(1, queue.size());
    }

    @Test
    public void testClear() {
        godQueue<Integer> queue = new godQueue<>(5);
        queue.add(10);
        queue.add(20);
        queue.add(30);
        queue.clear();
        assertTrue(queue.isEmpty());
        assertEquals(0, queue.size());
        assertFalse(queue.contains(10));
    }
    @Test
    public void testIterator() {
        godQueue<String> queue = new godQueue<>();
        queue.add("P");
        queue.add("Q");
        queue.add("R");

        Iterator<String> iterator = queue.iterator();
        assertTrue(iterator.hasNext());
        assertEquals("P", iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals("Q", iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals("R", iterator.next());
        assertFalse(iterator.hasNext());

        assertThrows(NoSuchElementException.class, iterator::next); // No more elements

        // Test that iterator reflects removals
        queue.remove("Q");
        Iterator<String> iterator2 = queue.iterator(); // Create *new* iterator after modification
        assertEquals("P", iterator2.next());
        assertEquals("R", iterator2.next());
        assertFalse(iterator2.hasNext());
    }


    @Test
    public void testIteratorRemove() { // Verify that the iterator does not support removal.
        godQueue<String> queue = new godQueue<>();
        queue.add("M");
        Iterator<String> it = queue.iterator();
        assertThrows(UnsupportedOperationException.class, it::remove);
    }

    @Test
    public void testToString() {
        godQueue<Integer> queue = new godQueue<>();
        queue.add(5);
        queue.add(15);
        queue.add(25);
        assertEquals("[5, 15, 25]", queue.toString());

        queue.remove(15);
        assertEquals("[5, 25]", queue.toString());

        queue.clear();
        assertEquals("[]", queue.toString());
    }

    @Test
    public void testInitialCapacity() {
        godQueue<String> queue = new godQueue<>(10);
        // No specific assertions here; just checks that constructor works.
        // Add more elements than the initial capacity to indirectly test resizing.
        for (int i = 0; i < 20; i++) {
            queue.add("Item" + i);
        }
        assertEquals(20, queue.size());
    }


    @Test
    public void testAddNull() {
        godQueue<String> queue = new godQueue<>();
        assertThrows(NullPointerException.class, () -> queue.add(null));
    }

    @Test
    public void testRemoveFromEmptyQueue(){
        godQueue<String> queue = new godQueue<>();
        assertFalse(queue.remove("abc"));
    }

    @Test
    public void testRemoveAndAddSameElement(){
        godQueue<Integer> queue = new godQueue<>();
        queue.add(5);
        queue.remove(5);
        assertTrue(queue.add(5));
        assertEquals(1, queue.size());
    }

    @Test
    public void testAddRemoveMultiple() {
        godQueue<Integer> queue = new godQueue<>();
        for (int i = 0; i < 1000; i++) {
            queue.add(i);
        }
        for (int i = 0; i < 1000; i += 2) {
            queue.remove(i);
        }
        assertEquals(500, queue.size());
        for (int i = 1; i < 1000; i += 2) {
            assertTrue(queue.contains(i));
        }
        for (int i = 0; i < 1000; i += 2) {
            assertFalse(queue.contains(i));
        }
    }

    @Test
    public void testRemoveHead() {
        godQueue<String> queue = new godQueue<>();
        queue.add("first");
        queue.add("second");
        assertTrue(queue.remove("first"));
        assertEquals(1, queue.size());
        assertEquals("second", queue.get(0));
    }
    @Test
    public void testRemoveTail() {
        godQueue<String> queue = new godQueue<>();
        queue.add("first");
        queue.add("second");
        assertTrue(queue.remove("second"));
        assertEquals(1, queue.size());
        assertEquals("first", queue.get(0));
    }

    @Test
    public void testGodQueueWithPairs() {
        godQueue<Pair<String, Integer>> queue = new godQueue<>();

        Pair<String, Integer> pair1 = Pair.of("A", 1);
        Pair<String, Integer> pair2 = Pair.of("B", 2);
        Pair<String, Integer> pair3 = Pair.of("C", 3);

        assertTrue(queue.add(pair1));
        assertTrue(queue.add(pair2));
        assertTrue(queue.add(pair3));

        assertEquals(3, queue.size());
        assertTrue(queue.contains(pair1));
        assertTrue(queue.contains(Pair.of("B", 2))); // Test contains with a new, equal Pair
        assertFalse(queue.contains(Pair.of("D", 4)));

        assertEquals(pair1, queue.get(0));
        assertEquals(pair2, queue.get(1));
        assertEquals(pair3, queue.get(2));

        assertTrue(queue.remove(pair2));
        assertEquals(2, queue.size());
        assertFalse(queue.contains(pair2));
        assertEquals(pair3, queue.get(1)); // Check index after removal

        //Test with iterator.
        Iterator<Pair<String, Integer>> iterator = queue.iterator();
        assertTrue(iterator.hasNext());
        assertEquals(pair1, iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(pair3, iterator.next());
        assertFalse(iterator.hasNext());

        queue.clear();
        assertTrue(queue.isEmpty());
        assertFalse(queue.contains(pair1));
    }

    @Test
    public void testGodQueueWithPairs_removeNonExisting(){
        godQueue<Pair<Integer, String>> queue = new godQueue<>();
        Pair<Integer, String> pair1 = new Pair<>(1, "A");
        queue.add(pair1);
        Pair<Integer, String> pair2 = new Pair<>(2, "B");
        assertFalse(queue.remove(pair2));
    }

    @Test
    public void testGodQueueWithPairs_removeAndAdd(){
        godQueue<Pair<Integer, String>> queue = new godQueue<>();
        Pair<Integer, String> pair1 = new Pair<>(1, "A");
        queue.add(pair1);
        queue.remove(pair1);
        assertTrue(queue.add(pair1));
        assertEquals(1, queue.size());
        assertEquals(pair1, queue.get(0));
    }

    @Test
    public void testGodQueueWithPairs_addSamePairTwice(){
        godQueue<Pair<Integer, String>> queue = new godQueue<>();
        Pair<Integer, String> pair1 = new Pair<>(1, "A");
        assertTrue(queue.add(pair1));
        assertFalse(queue.add(pair1));  // Attempting to add the *same* instance
        assertFalse(queue.add(new Pair<>(1, "A"))); //Attempting to add an equal but *different* instance
    }
}