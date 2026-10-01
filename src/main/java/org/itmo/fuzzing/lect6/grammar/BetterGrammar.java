package org.itmo.fuzzing.lect6.grammar;

import java.util.*;
import java.util.stream.Collectors;

public class BetterGrammar {

    public Map<String, List<Expansion>> rules = new HashMap<>();

    public BetterGrammar() {}
    public BetterGrammar(LinkedHashMap<String, List<String>> grammarRules) {
        for (Map.Entry<String, List<String>> entry : grammarRules.entrySet()) {
            var expansions = entry.getValue().stream().map(Expansion::new).toList();
            rules.put(entry.getKey(), expansions);
        }
    }
}
