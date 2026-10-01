package org.itmo.fuzzing.lect6;

import org.itmo.fuzzing.lect6.grammar.BetterGrammar;

import java.util.*;

public class ExprEBNFGrammar {
    public static final LinkedHashMap<String, List<String>> EXPR_EBNF_GRAMMAR = new LinkedHashMap<>() {{
        put("<start>", Arrays.asList("<expr>"));

        put("<expr>", Arrays.asList(
                "<term> + <expr>",
                "<term> - <expr>",
                "<term>"
        ));

        put("<term>", Arrays.asList(
                "<factor> * <term>",
                "<factor> / <term>",
                "<factor>"
        ));

        put("<factor>", Arrays.asList(
                "<sign>?<factor>",
                "(<expr>)",
                "<integer>(.<integer>)?"
        ));

        put("<sign>", Arrays.asList(
                "+", "-"
        ));

        put("<integer>", Arrays.asList(
                "<digit>+"
        ));

        put("<digit>", Arrays.asList(
                "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"
        ));
    }};

    public static Grammar getGrammar() {
        return new Grammar(EXPR_EBNF_GRAMMAR);
    }

    public static BetterGrammar getBetterGrammar() {
        var grammarExpr = ExprEBNFGrammar.getGrammar();
        var converted = GrammarConverter.convertEbnfGrammar(grammarExpr);
        return new BetterGrammar(converted.rules);
    }
}
