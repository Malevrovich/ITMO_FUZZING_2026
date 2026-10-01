package org.itmo.fuzzing.lect6;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Grammar {
    public LinkedHashMap<String, List<String>> rules = new LinkedHashMap<>();

    public Grammar(LinkedHashMap<String, List<String>> rules) {
        this.rules = rules;
    }

    public Grammar extendGrammar(LinkedHashMap<String, List<String>> additionalRules) {
        var newGrammarRules = new LinkedHashMap<String, List<String>>(rules);
        additionalRules.forEach((key, value) -> newGrammarRules.put(key, new ArrayList<>(value)));
        return new Grammar(newGrammarRules);
    }

    @Override
    public String toString() {
        return rules.entrySet().stream().map(stringListEntry ->
                "Rule: " + stringListEntry.getKey() + " -> " + stringListEntry.getValue().stream().map(s -> s + ", ").collect(Collectors.joining()) + "\n"
        ).collect(Collectors.joining());
    }

}
