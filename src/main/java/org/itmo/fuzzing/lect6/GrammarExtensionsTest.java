package org.itmo.fuzzing.lect6;

import java.util.*;

public class GrammarExtensionsTest {

    public static void main(String[] args) {
        final LinkedHashMap<String, List<String>> rules = new LinkedHashMap<>() {
            {
                put("<start>", Arrays.asList("<nonterminal>"));

                put("<nonterminal>", Arrays.asList("<left-angle><identifier><right-angle>"));

                put("<left-angle>", Arrays.asList("<"));

                put("<right-angle>", Arrays.asList(">"));

                put("<identifier>", Arrays.asList("id"));

            }
        };
//        var grammar = new Grammar(rules);
//        System.out.println(grammar);
//        var extendedGrammar = grammar.extendGrammar(LinkedHashMap.of("<identifier>", Arrays.asList("<idchar>", "<identifier><idchar>"), "<idchar>", Arrays.asList("a", "b", "c", "d")));
//        System.out.println(extendedGrammar);
        var grammarExpr = ExprEBNFGrammar.getGrammar();
        var converted = GrammarConverter.convertEbnfGrammar(grammarExpr);
        System.out.println(converted);
        var r = GrammarUtils.isValidGrammar(converted, "<start>", Set.of());
        System.out.println("r = " + r);
        final LinkedHashMap<String, List<String>> wrongRules = new LinkedHashMap<>() {
            {
                put("<start>", Arrays.asList("<x>"));
                put("<y>", Arrays.asList("1"));
            }
        };
    }
}
