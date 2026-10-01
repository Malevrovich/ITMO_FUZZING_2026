package org.itmo.fuzzing.lect6.util;

import java.util.Objects;

public class Triple<A, B, C> {
    private final A first;   // First element
    private final B second;  // Second element
    private final C third;   // Third element

    // Constructor
    public Triple(A first, B second, C third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    // Getters
    public A getFirst() {
        return first;
    }

    public B getSecond() {
        return second;
    }

    public C getThird() {
        return third;
    }

    // Override equals
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Triple)) return false;
        Triple<?, ?, ?> triple = (Triple<?, ?, ?>) o;
        return Objects.equals(first, triple.first) &&
                Objects.equals(second, triple.second) &&
                Objects.equals(third, triple.third);
    }

    // Override hashCode
    @Override
    public int hashCode() {
        return Objects.hash(first, second, third);
    }

    // Override toString
    @Override
    public String toString() {
        return "Triple{" +
                "first=" + first +
                ", second=" + second +
                ", third=" + third +
                '}';
    }
}