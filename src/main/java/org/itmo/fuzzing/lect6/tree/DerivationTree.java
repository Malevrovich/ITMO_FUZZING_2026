package org.itmo.fuzzing.lect6.tree;

import org.itmo.fuzzing.lect6.ExprGrammar;

import java.util.List;
import java.util.Optional;

// Define the DerivationTree class
public class DerivationTree {
    private final DerivationTreeNode root;

    public DerivationTree(DerivationTreeNode root) {
        this.root = root;
    }


    public DerivationTreeNode getRoot() {
        return root;
    }

    public String allTerminals() {
        return root.allTerminals();
    }

    public String treeToString() {
        return treeToString(root);
    }

    private String treeToString(DerivationTreeNode node) {
        String symbol = node.getValue();
        var children = node.getChildren();

        if (children != null && !children.isEmpty()) {
            StringBuilder result = new StringBuilder();
            for (var child : children) {
                result.append(treeToString(child));
            }
            return result.toString();
        } else {
            // Check if it's a non-terminal symbol
            return GrammarUtils.isNonTerminal(symbol) ? "" : symbol;
        }
    }

}

