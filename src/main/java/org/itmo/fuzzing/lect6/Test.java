package org.itmo.fuzzing.lect6;

public class Test {

    public static void main(String[] args) throws SimpleGrammarFuzzer.ExpansionError {
//        ExprGrammar.NonTerminals("<digit><integer>").forEach(System.out::println);
//        String res = SimpleGrammarFuzzer.simpleGrammarFuzzer(ExprGrammar.EXPR_GRAMMAR, ExprGrammar.START_SYMBOL, 3, 100, true);
//        System.out.println("GENERATED EXPRESSION = " + res);
//        String res = SimpleGrammarFuzzer.simpleGrammarFuzzer(URLGrammar.URL_GRAMMAR, ExprGrammar.START_SYMBOL, 10, 100, true);
//        System.out.println("GENERATED URL = " + res);
        String res = SimpleGrammarFuzzer.simpleGrammarFuzzer(ExprEBNFGrammar.EXPR_EBNF_GRAMMAR, ExprGrammar.START_SYMBOL, 3, 100, true);
        System.out.println("GENERATED EXPRESSION = " + res);
    }
}
