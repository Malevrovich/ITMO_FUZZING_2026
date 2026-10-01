package org.itmo.fuzzing.lect6.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class DerivationTreeExample {

    public static void main(String[] args) {
        // Create the derivation tree
        List<DerivationTreeNode> exprChildren = new ArrayList<>();
        exprChildren.add(new DerivationTreeNode("<expr>", null));
        exprChildren.add(new DerivationTreeNode(" + ", List.of()));
        exprChildren.add(new DerivationTreeNode("<term>", null));
        DerivationTreeNode exprNode = new DerivationTreeNode("<expr>", exprChildren);
        List<DerivationTreeNode> rootChildren = new ArrayList<>();
        rootChildren.add(exprNode);
        var rootNode = new DerivationTreeNode("<start>", rootChildren);
        DerivationTree derivationTree = new DerivationTree(rootNode);

//        // Example output
        DerivationTreePrinter.printTree(derivationTree);
        System.out.println("----");
        System.out.println(derivationTree.treeToString());


    }
}
