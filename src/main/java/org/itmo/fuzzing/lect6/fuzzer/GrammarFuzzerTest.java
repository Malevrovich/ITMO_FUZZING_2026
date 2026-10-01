package org.itmo.fuzzing.lect6.fuzzer;

import org.itmo.fuzzing.lect6.ExprEBNFGrammar;
import org.itmo.fuzzing.lect6.ExprGrammar;
import org.itmo.fuzzing.lect6.grammar.Expansion;
import org.itmo.fuzzing.lect6.tree.DerivationTree;
import org.itmo.fuzzing.lect6.tree.DerivationTreeNode;
import org.itmo.fuzzing.lect6.tree.DerivationTreePrinter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GrammarFuzzerTest {

    public static void main(String[] args) {
        var grammar = ExprGrammar.getBetterGrammar();
        var fuzzer = new GrammarFuzzer(grammar);
//        var expansion = new Expansion("<term> + <expr>");
//        fuzzer.expansionToChildren(expansion).forEach(System.out::println);
//        var node = new DerivationTreeNode("<integer>", null);
//        var expanded = fuzzer.expandNodeRandomly(node);
//        System.out.println(expanded);

//
//        var digitNode = new DerivationTreeNode("<digit>", null);
//        var expandedDigit = fuzzer.expandNodeRandomly(digitNode);
//        System.out.println(expandedDigit);
//
        var tree = createSimpleDerivationTree();
//        var expandedOnce = new DerivationTree(fuzzer.expandTreeOnce(tree.getRoot()));
//        DerivationTreePrinter.printTree(expandedOnce);
//        System.out.println("-----------");
//        DerivationTreePrinter.printTree(fuzzer.expandTreeOnce(expandedOnce.getRoot()));
//        System.out.println("----------");
//        var expandedTwice = new DerivationTree(fuzzer.expandTreeOnce(expandedOnce.getRoot()));
//        DerivationTreePrinter.printTree(expandedTwice);
//
//        System.out.println(fuzzer.symbolCost("<expr>", Set.of()));
//        DerivationTreePrinter.printTree(tree);
//        System.out.println("-------------");
        var expandedByMinCost = new DerivationTree(fuzzer.expandTreeOnce(tree.getRoot()));
//        DerivationTreePrinter.printTree(expandedByMinCost);
//        while (fuzzer.anyPossibleExpansions(expandedByMinCost.getRoot())) {
//            expandedByMinCost = new DerivationTree(fuzzer.expandTreeOnce(expandedByMinCost.getRoot()));
//            DerivationTreePrinter.printTree(expandedByMinCost);
//            System.out.println("---------------");
//        }
//        System.out.println(expandedByMinCost.treeToString());
//        System.exit(0);

        var expanded = fuzzer.expandTree(tree);
        DerivationTreePrinter.printTree(expanded);
        System.out.println("-----------");
        System.out.println(expanded.treeToString());
    }


    public static DerivationTree createSimpleDerivationTree() {
        List<DerivationTreeNode> exprChildren = new ArrayList<>();
        exprChildren.add(new DerivationTreeNode("<expr>", null));
        exprChildren.add(new DerivationTreeNode(" + ", List.of()));
        exprChildren.add(new DerivationTreeNode("<term>", null));
        DerivationTreeNode exprNode = new DerivationTreeNode("<expr>", exprChildren);
        List<DerivationTreeNode> rootChildren = new ArrayList<>();
        rootChildren.add(exprNode);
        var rootNode = new DerivationTreeNode("<start>", rootChildren);
        return new DerivationTree(rootNode);
    }
}
