package org.itmo.fuzzing.lect6;

import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;

public class GrammarUtils {

    // Method to find the defined and used nonterminals in a grammar
    public static Pair<Set<String>, Set<String>> defUsedNonterminals(Grammar grammar, String startSymbol) {
        Set<String> definedNonterminals = new HashSet<>();
        Set<String> usedNonterminals = new HashSet<>();
        usedNonterminals.add(startSymbol);

        for (String definedNonterminal : grammar.rules.keySet()) {
            definedNonterminals.add(definedNonterminal);
            List<String> expansions = grammar.rules.get(definedNonterminal);

            if (!(expansions instanceof List)) {
                System.err.println(definedNonterminal + ": expansion is not a list");
                return new Pair<>(null, null);
            }

            if (expansions.isEmpty()) {
                System.err.println(definedNonterminal + ": expansion list empty");
                return new Pair<>(null, null);
            }

            for (String expansion : expansions) {
                for (String usedNonterminal : nonterminals(expansion)) {
                    usedNonterminals.add(usedNonterminal);
                }
            }
        }
        return new Pair<>(definedNonterminals, usedNonterminals);
    }

    // Helper method to find nonterminals in an expansion (using regular expression)
    public static List<String> nonterminals(String expansion) {
        Pattern nonterminalPattern = Pattern.compile("<[^<>]*>");
        Matcher matcher = nonterminalPattern.matcher(expansion);
        List<String> nonterminals = new ArrayList<>();

        while (matcher.find()) {
            nonterminals.add(matcher.group());
        }

        return nonterminals;
    }

    // Method to find reachable nonterminals
    public static Set<String> reachableNonterminals(Grammar grammar, String startSymbol) {
        Set<String> reachable = new HashSet<>();
        findReachableNonterminals(grammar, startSymbol, reachable);
        return reachable;
    }

    // Recursive method to find reachable nonterminals
    private static void findReachableNonterminals(Grammar grammar, String symbol, Set<String> reachable) {
        reachable.add(symbol);
        for (String expansion : grammar.rules.getOrDefault(symbol, new ArrayList<>())) {
            for (String nonterminal : nonterminals(expansion)) {
                if (!reachable.contains(nonterminal)) {
                    findReachableNonterminals(grammar, nonterminal, reachable);
                }
            }
        }
    }

    // Method to find unreachable nonterminals
    public static Set<String> unreachableNonterminals(Grammar grammar, String startSymbol) {
        return grammar.rules.keySet().stream()
                .filter(nonterminal -> !reachableNonterminals(grammar, startSymbol).contains(nonterminal))
                .collect(Collectors.toSet());
    }

    // Method to check if a grammar is valid
    public static boolean isValidGrammar(Grammar grammar, String startSymbol, Set<String> supportedOpts) {
        Pair<Set<String>, Set<String>> definedUsed = defUsedNonterminals(grammar, startSymbol);
        Set<String> definedNonterminals = definedUsed.getKey();
        Set<String> usedNonterminals = definedUsed.getValue();

        if (definedNonterminals == null || usedNonterminals == null) {
            return false;
        }

        if (grammar.rules.containsKey("<start>")) {
            usedNonterminals.add("<start>");
        }

        for (String unused : definedNonterminals.stream().filter(n -> !usedNonterminals.contains(n)).collect(Collectors.toList())) {
            System.err.println(unused + ": defined, but not used.");
        }

        for (String undefined : usedNonterminals.stream().filter(n -> !definedNonterminals.contains(n)).collect(Collectors.toList())) {
            System.err.println(undefined + ": used, but not defined.");
        }

        Set<String> unreachable = unreachableNonterminals(grammar, startSymbol);
        if (grammar.rules.containsKey("<start>")) {
            unreachable.removeAll(reachableNonterminals(grammar, "<start>"));
        }

        for (String unreachableNonterminal : unreachable) {
            System.err.println(unreachableNonterminal + ": unreachable from " + startSymbol);
        }

        Set<String> usedOpts = optsUsed(grammar);
        usedOpts.removeAll(supportedOpts);
        for (String opt : usedOpts) {
            System.err.println("Warning: option " + opt + " is not supported.");
        }

        return definedNonterminals.equals(usedNonterminals) && unreachable.isEmpty();
    }

    // Method to find used options in a grammar
    public static Set<String> optsUsed(Grammar grammar) {
        Set<String> usedOpts = new HashSet<>();
        for (String symbol : grammar.rules.keySet()) {
            for (String expansion : grammar.rules.get(symbol)) {
                usedOpts.addAll(expOpts(expansion).keySet());
            }
        }
        return usedOpts;
    }

    // Mock method for extracting options from expansion (implementation depends on the grammar structure)
    public static Map<String, String> expOpts(String expansion) {
        return new HashMap<>();  // Placeholder: implement based on actual grammar requirements
    }

    // Method to trim unused and unreachable nonterminals from a grammar
    public static Grammar trimGrammar(Grammar grammar, String startSymbol) {
        Grammar newGrammar = grammar.extendGrammar(new LinkedHashMap<>());
        Pair<Set<String>, Set<String>> definedUsed = defUsedNonterminals(grammar, startSymbol);
        Set<String> definedNonterminals = definedUsed.getKey();
        Set<String> usedNonterminals = definedUsed.getValue();

        if (definedNonterminals == null || usedNonterminals == null) {
            return newGrammar;
        }

        Set<String> unused = definedNonterminals.stream().filter(n -> !usedNonterminals.contains(n)).collect(Collectors.toSet());
        Set<String> unreachable = unreachableNonterminals(grammar, startSymbol);

        unused.addAll(unreachable);

        for (String nonterminal : unused) {
            newGrammar.rules.remove(nonterminal);
        }

        return newGrammar;
    }
}

// A Pair class implementation, as Java doesn't have built-in tuple support
class Pair<K, V> {
    private K key;
    private V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }
}
