package org.itmo.fuzzing.lect2.instrumentation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/** Static call edges discovered by ASM at class transformation time, not only executed edges. */
public final class CallGraphTracker {
    private static final Map<MethodId, Set<MethodId>> graph = new ConcurrentHashMap<>();

    private CallGraphTracker() {
    }

    public record MethodId(String owner, String name, String descriptor) {
    }

    public static void registerMethod(String owner, String name, String descriptor) {
        graph.computeIfAbsent(new MethodId(owner, name, descriptor), key -> ConcurrentHashMap.newKeySet());
    }

    public static void registerCall(String callerOwner, String callerName, String callerDescriptor,
                                    String calleeOwner, String calleeName, String calleeDescriptor) {
        var caller = new MethodId(callerOwner, callerName, callerDescriptor);
        var callee = new MethodId(calleeOwner, calleeName, calleeDescriptor);
        graph.computeIfAbsent(caller, key -> ConcurrentHashMap.newKeySet()).add(callee);
    }

    /** Returns an immutable copy. Clearing runtime coverage does not clear this graph. */
    public static Map<MethodId, Set<MethodId>> snapshot() {
        var copy = new HashMap<MethodId, Set<MethodId>>();
        graph.forEach((method, callees) -> copy.put(method, Set.copyOf(callees)));
        return Map.copyOf(copy);
    }
}
