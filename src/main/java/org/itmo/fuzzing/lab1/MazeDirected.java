package org.itmo.fuzzing.lab1;

import org.itmo.fuzzing.lect2.instrumentation.CallGraphTracker;
import org.itmo.fuzzing.lect3.CallGraphDistances;
import org.itmo.fuzzing.lect3.GreyBoxFuzzer;
import org.itmo.fuzzing.lect3.DirectedPowerSchedule;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Configuration of the directed experiment; no precomputed route or cell coordinates. */
public final class MazeDirected {
    private MazeDirected() {
    }

    public static GreyBoxFuzzer createFuzzer(List<String> seeds, MazeMutator mutator,
                                                    int minMutations, int maxMutations) {
        return new GreyBoxFuzzer(seeds, mutator,
                new DirectedPowerSchedule(methodDistances()), minMutations, maxMutations);
    }

    public static Map<String, Integer> methodDistances() {
        // Calling targetTile loads/transforms the entire class before reading the static graph.
        String target = MazeGenerated.targetTile();
        String owner = MazeGenerated.class.getName().replace('.', '/');
        var graph = new HashMap<String, Set<String>>();
        CallGraphTracker.snapshot().forEach((caller, callees) -> {
            if (caller.owner().equals(owner) && caller.name().startsWith("tile_")) {
                var neighbours = new HashSet<String>();
                for (var callee : callees) {
                    if (callee.owner().equals(owner) && callee.name().startsWith("tile_")) {
                        neighbours.add(callee.name());
                    }
                }
                graph.put(caller.name(), neighbours);
            }
        });
        if (!graph.containsKey(target)) {
            throw new IllegalStateException("Directed fuzzing requires the ASM call graph. "
                    + "Run with the coverage javaagent including MazeGenerated.");
        }
        return CallGraphDistances.toTarget(graph, target);
    }
}
