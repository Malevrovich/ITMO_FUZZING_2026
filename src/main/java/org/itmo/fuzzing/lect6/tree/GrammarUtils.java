package org.itmo.fuzzing.lect6.tree;

import java.util.regex.Pattern;

public class GrammarUtils {

    public static final Pattern RE_NONTERMINAL = Pattern.compile("(<[^<> ]*>)");

    public static boolean isNonTerminal(String s) {
        return RE_NONTERMINAL.matcher(s).matches();
    }

}
