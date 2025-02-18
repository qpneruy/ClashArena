package org.qpneruy.clashArena.utils;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.qpneruy.clashArena.menu.Gui.mainMenu.UpdateMethod;

import java.util.*;

/**
 * Custom queue implementation with O(1) operations for add/remove/contains.
 * Maintains insertion order while allowing fast removals.
 *
 * @param <T> Type of elements in the queue
 */
public class godQueue<T> implements Iterable<T> {
    private final ArrayList<T> elements;
    private final Map<T, Integer> elementIndices;
    private int size = 0;

    // Record the previous action for add, remove method
    @Getter private UpdateMethod method;
    @Getter private int removedIndex;

    public godQueue() {
        this(16);
        method = UpdateMethod.ALL;
    }

    public godQueue(int initialCapacity) {
        this.elements = new ArrayList<>(initialCapacity);
        this.elementIndices = new HashMap<>(initialCapacity);
        method = UpdateMethod.ALL;
    }

    /**
     * Adds element to the end of queue if not already present
     * @param element Element to add
     * @return true if element was added, false if already exists
     */
    public boolean add(T element) {
        if (element == null) throw new NullPointerException("Null elements not allowed");
        if (contains(element)) return false;

        ensureCapacity(size + 1);
        elements.add(element);
        elementIndices.put(element, size);
        size++;
        method = UpdateMethod.NEW;
        return true;
    }

    /**
     * Removes element from queue if present
     * @param element Element to remove
     * @return true if element was removed, false if not found
     */
    public boolean remove(T element) {
        if (!contains(element)) return false;

        int index = elementIndices.get(element);
        int lastIndex = size - 1;

        // Swap with last element if needed
        if (index != lastIndex) {
            T lastElement = elements.get(lastIndex);
            elements.set(index, lastElement);
            elementIndices.put(lastElement, index);
        }

        // Remove last element
        elements.remove(lastIndex);
        elementIndices.remove(element);
        size--;
        method = UpdateMethod.REMOVE;
        removedIndex = index;
        return true;
    }

    /**
     * Gets element at specified position
     * @param index Position of element
     * @return Element at specified position
     * @throws IndexOutOfBoundsException if index is invalid
     */
    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        return elements.get(index);
    }

    public boolean contains(T element) {
        return elementIndices.containsKey(element);
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Clears all elements from the queue
     */
    public void clear() {
        elements.clear();
        elementIndices.clear();
        size = 0;
    }

    @Override
    public @NotNull Iterator<T> iterator() {
        return Collections.unmodifiableList(elements).iterator();
    }

    @Override
    public String toString() {
        return elements.subList(0, size).toString();
    }

    private void ensureCapacity(int minCapacity) {
        if (minCapacity > elements.size()) {
            int newCapacity = elements.size() * 2;
            if (newCapacity < minCapacity) newCapacity = minCapacity;
            elements.ensureCapacity(newCapacity);
        }
    }
}