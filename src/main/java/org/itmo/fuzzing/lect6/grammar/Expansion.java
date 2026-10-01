package org.itmo.fuzzing.lect6.grammar;

import org.itmo.fuzzing.lect2.instrumentation.Pair;

public class Expansion {
    private final Pair<String, Option> expansionValue;

    public Expansion(Pair<String, Option> pairValue) {
        this.expansionValue = pairValue;
    }

    public Expansion(String value, Option option) {
        this.expansionValue = new Pair<>(value, option);
    }

    public Expansion(String stringValue) {
        this.expansionValue = new Pair<>(stringValue, null);
    }

    @Override
    public String toString() {
        return expansionValue.first;
    }
}