package org.itmo.fuzzing.lect3;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class CallGraphDistances {
    private CallGraphDistances() {
    }

    /** Reverse BFS: shortest number of call edges from each reachable method to the target. */
    public static Map<String, Integer> toTarget(Map<String, Set<String>> graph, String target) {
        if (!graph.containsKey(target)) {
            throw new IllegalArgumentException("Target method is absent from the call graph: " + target);
        }
        var callers = new HashMap<String, Set<String>>();
        graph.forEach((caller, callees) -> callees.forEach(callee ->
                callers.computeIfAbsent(callee, key -> new HashSet<>()).add(caller)));
        var distances = new HashMap<String, Integer>();
        var queue = new ArrayDeque<String>();
        distances.put(target, 0);
        queue.add(target);
        while (!queue.isEmpty()) {
            String callee = queue.remove();
            for (String caller : callers.getOrDefault(callee, Set.of())) {
                if (!distances.containsKey(caller)) {
                    distances.put(caller, distances.get(callee) + 1);
                    queue.add(caller);
                }
            }
        }
        return Map.copyOf(distances);
    }
}
