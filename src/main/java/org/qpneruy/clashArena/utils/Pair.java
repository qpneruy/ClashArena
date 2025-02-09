package org.qpneruy.clashArena.utils;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter @Setter
public class Pair<L, R> {
    private L first;
    private R second;

    public Pair() {
    }

    public Pair(L first, R second) {
        this.first = first;
        this.second = second;
    }

    public void clear() {
        this.first = null;
        this.second = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(first, pair.first) && Objects.equals(second, pair.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }

    public static <T, U> Pair<T, U> of(T first, U second) {
        return new Pair<>(first, second);
    }
}