package org.itmo.fuzzing.lect6.tree;

import java.util.List;
import java.util.Optional;

public class DerivationTreePrinter {

    public static void printTree(DerivationTree tree) {
        printNode(tree.getRoot().getValue(), tree.getRoot().getChildren(), 0);
    }

    public static void printTree(DerivationTreeNode node) {
        printNode(node.getValue(), node.getChildren(), 0);
    }

    private static void printNode(String symbol, List<DerivationTreeNode> children, int level) {
        // Выводим символ с отступом в зависимости от уровня
        String indent = "  ".repeat(level); // Используем пробелы для отступа
        System.out.println(indent + symbol);

        // Если есть дочерние узлы, рекурсивно выводим их
        if (children != null && !children.isEmpty()) {
            for (DerivationTreeNode child : children) {
                printNode(child.getValue(), child.getChildren(), level + 1);
            }
        }
    }
}
