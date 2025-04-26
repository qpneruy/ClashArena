package org.qpneruy.clashArena.utils.collections;

import lombok.AllArgsConstructor;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@AllArgsConstructor
public class RandomAccessList<E> {

    private final Map<E, Integer> underlyingList;

    public Optional<E> nextElement() {
        if (underlyingList.isEmpty()) return Optional.empty();
        int listSize = underlyingList.values().stream().mapToInt(Integer::intValue).sum();
        int randomIndex = ThreadLocalRandom.current().nextInt(listSize);
        int counter = 0;
        for (E e : underlyingList.keySet()) {
            if (randomIndex >= counter && randomIndex < counter + underlyingList.get(e)) {
                return Optional.of(e);
            }
            counter += underlyingList.get(e);
        }
        return Optional.empty();
    }

}