package org.itmo.fuzzing.lect6;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class GrammarConverter {
    // Regular expressions for parenthesized expressions and extended nonterminals
    private static final Pattern RE_PARENTHESIZED_EXPR = Pattern.compile("\\([^()]*\\)[?+*]");
    private static final Pattern RE_EXTENDED_NONTERMINAL = Pattern.compile("<[^<> ]*>[?+*]");

    // Method to find parenthesized expressions with operators (?, +, *)
    private static List<String> parenthesizedExpressions(String expansion) {
        List<String> matches = new ArrayList<>();
        Matcher matcher = RE_PARENTHESIZED_EXPR.matcher(expansion);

        while (matcher.find()) {
            matches.add(matcher.group());
        }

        return matches;
    }

    // Convert a grammar in extended BNF to BNF, handling parentheses
    public static Grammar convertEbnfParentheses(Grammar ebnfGrammar) {
        Grammar grammar = ebnfGrammar.extendGrammar(new LinkedHashMap<>());

        for (String nonterminal : ebnfGrammar.rules.keySet()) {
            List<String> expansions = ebnfGrammar.rules.get(nonterminal);

            for (int i = 0; i < expansions.size(); i++) {
                String expansion = expansions.get(i);

                while (true) {
                    List<String> parenthesizedExprs = parenthesizedExpressions(expansion);
                    if (parenthesizedExprs.isEmpty()) break;

                    for (String expr : parenthesizedExprs) {
                        char operator = expr.charAt(expr.length() - 1); // Last character is the operator (?, +, *)
                        String contents = expr.substring(1, expr.length() - 2); // Inside the parentheses

                        String newSym = newSymbol(grammar, "<symbol>");

                        // Replace the expression with the new symbol and operator
                        String exp = grammar.rules.get(nonterminal).get(i);
                        expansion = exp.replace(expr, newSym + operator);

                        grammar.rules.put(newSym, Arrays.asList(contents));
                        grammar.rules.get(nonterminal).set(i, expansion);
                    }
                }
            }
        }

        return grammar;
    }

    // Convert a grammar in extended BNF to BNF, handling operators
    public static Grammar convertEbnfOperators(Grammar ebnfGrammar) {
        Grammar grammar = ebnfGrammar.extendGrammar(new LinkedHashMap<>());

        for (String nonterminal : ebnfGrammar.rules.keySet()) {
            List<String> expansions = ebnfGrammar.rules.get(nonterminal);

            for (int i = 0; i < expansions.size(); i++) {
                String expansion = expansions.get(i);
                List<String> extendedSymbols = extendedNonterminals(expansion);

                for (String extendedSymbol : extendedSymbols) {
                    char operator = extendedSymbol.charAt(extendedSymbol.length() - 1); // Last character is the operator (?, +, *)
                    String originalSymbol = extendedSymbol.substring(0, extendedSymbol.length() - 1); // The non-terminal before the operator
                    assert ebnfGrammar.rules.containsKey(originalSymbol) : originalSymbol + " is not defined in grammar";

                    String newSym = newSymbol(grammar, originalSymbol);

                    // Replace extended symbol with new non-terminal
                    String exp = grammar.rules.get(nonterminal).get(i);
                    String newExp = exp.replace(extendedSymbol, newSym);
                    grammar.rules.get(nonterminal).set(i, newExp);

                    // Apply BNF transformation rules for ?, +, *
                    if (operator == '?') {
                        grammar.rules.put(newSym, Arrays.asList("", originalSymbol));
                    } else if (operator == '*') {
                        grammar.rules.put(newSym, Arrays.asList("", originalSymbol + newSym));
                    } else if (operator == '+') {
                        grammar.rules.put(newSym, Arrays.asList(originalSymbol, originalSymbol + newSym));
                    }
                }
            }
        }

        return grammar;
    }

    // Helper method to find extended non-terminals with operators (?, +, *)
    private static List<String> extendedNonterminals(String expansion) {
        List<String> matches = new ArrayList<>();
        Matcher matcher = RE_EXTENDED_NONTERMINAL.matcher(expansion);

        while (matcher.find()) {
            matches.add(matcher.group());
        }

        return matches;
    }

    public static String newSymbol(Grammar grammar, String symbolName) {
        // Return a new symbol for `grammar` based on `symbolName`
        if (!grammar.rules.containsKey(symbolName)) {
            return symbolName;
        }

        int count = 1;
        while (true) {
            // Modify the symbol name with a count to create a new one
            String tentativeSymbolName = symbolName.substring(0, symbolName.length() - 1) + "-" + count + ">";
            if (!grammar.rules.containsKey(tentativeSymbolName)) {
                return tentativeSymbolName;
            }
            count++;
        }
    }

//    // Helper method to generate a new unique symbol for the grammar
//    private static String newSymbol(Grammar grammar) {
//        return "NewSymbol" + (grammar.rules.size() + 1);
//    }
//
//    // Helper method to generate a new symbol based on an original symbol
//    private static String newSymbol(Grammar grammar, String baseSymbol) {
//        return baseSymbol + "_New" + (grammar.rules.size() + 1);
//    }

    // Main method to convert EBNF to BNF by applying both parentheses and operator conversions
    public static Grammar convertEbnfGrammar(Grammar ebnfGrammar) {
        return convertEbnfOperators(convertEbnfParentheses(ebnfGrammar));
    }
}

