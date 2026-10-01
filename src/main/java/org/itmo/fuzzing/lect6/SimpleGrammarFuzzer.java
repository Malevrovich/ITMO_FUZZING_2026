package org.itmo.fuzzing.lect6;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class SimpleGrammarFuzzer {
    private static final Random random = new Random();

    public static String simpleGrammarFuzzer(
            Map<String, List<String>> grammar,
            String startSymbol,
            int maxNonterminals,
            int maxExpansionTrials,
            boolean log
    ) throws ExpansionError {
        String term = startSymbol;
        int expansionTrials = 0;

        while (!ExprGrammar.NonTerminals(term).isEmpty()) {
            String symbolToExpand = ExprGrammar.NonTerminals(term).get(random.nextInt(ExprGrammar.NonTerminals(term).size()));
            List<String> expansions = grammar.get(symbolToExpand);
            String expansion = expansions.get(random.nextInt(expansions.size()));

            String newTerm = term.replaceFirst(symbolToExpand, expansion);

            if (ExprGrammar.NonTerminals(newTerm).size() < maxNonterminals) {
                term = newTerm;
                if (log) {
                    System.out.printf("%-40s %s%n", symbolToExpand + " -> " + expansion, term);
                }
                expansionTrials = 0;
            } else {
                expansionTrials++;
                if (expansionTrials >= maxExpansionTrials) {
                    throw new ExpansionError("Cannot expand " + term);
                }
            }
        }

        return term;
    }


    public static class ExpansionError extends Exception {
        public ExpansionError(String message) {
            super(message);
        }
    }
}
