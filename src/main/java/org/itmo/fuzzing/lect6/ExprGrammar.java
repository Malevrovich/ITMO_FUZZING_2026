package org.itmo.fuzzing.lect6;

import org.itmo.fuzzing.lect6.grammar.BetterGrammar;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExprGrammar {

    public static final String START_SYMBOL = "<start>";

    public static final LinkedHashMap<String, List<String>> EXPR_GRAMMAR = new LinkedHashMap<>() {{
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
                "+<factor>",
                "-<factor>",
                "(<expr>)",
                "<integer>.<integer>",
                "<integer>"
        ));

        put("<integer>", Arrays.asList(
                "<digit><integer>",
                "<digit>"
        ));

        put("<digit>", Arrays.asList(
                "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"
        ));
    }};

    public static final Pattern RE_NONTERMINAL = Pattern.compile("(<[^<> ]*>)");

    public static List<String> NonTerminals(Object expansion) {
        String expansionStr;
        if (expansion instanceof String[]) {
            expansionStr = ((String[]) expansion)[0];
        } else {
            expansionStr = expansion.toString();
        }

        List<String> result = new ArrayList<>();
        Matcher matcher = RE_NONTERMINAL.matcher(expansionStr);
        while (matcher.find()) {
            result.add(matcher.group());
        }
        return result;
    }

    public static boolean isNonTerminal(String s) {
        return RE_NONTERMINAL.matcher(s).matches();
    }

    public static BetterGrammar getBetterGrammar() {
        return new BetterGrammar(EXPR_GRAMMAR);
    }
}