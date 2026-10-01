package org.itmo.fuzzing.lect6.fuzzer;

import org.itmo.fuzzing.lect6.URLGrammar;

public class URLGrammarFuzzerTest {

    public static void main(String[] args) {
        var grammar = URLGrammar.getBetterGrammar();
        var fuzzer = new GrammarFuzzer(grammar);
        var tree = fuzzer.initTree();
        var expanded = fuzzer.expandTree(tree);
        System.out.println(expanded.treeToString());
    }
}
