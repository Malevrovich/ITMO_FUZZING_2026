package org.itmo.fuzzing.lect3;

import java.util.List;
import java.util.Map;

/** Prefers seeds covering methods closer to the target in a static call graph. */
public final class DirectedPowerSchedule extends PowerSchedule {
    private final Map<String, Integer> methodDistances;

    public DirectedPowerSchedule(Map<String, Integer> methodDistances) {
        if (methodDistances.isEmpty() || methodDistances.values().stream().anyMatch(distance -> distance < 0)) {
            throw new IllegalArgumentException("Require non-empty, non-negative method distances");
        }
        this.methodDistances = Map.copyOf(methodDistances);
    }

    /** Minimum distance among covered methods; unrelated and unreachable methods are ignored. */
    public void updateDistance(Seed seed) {
        double distance = Double.POSITIVE_INFINITY;
        for (Location location : seed.coverage) {
            Integer methodDistance = methodDistances.get(location.getFunction());
            if (methodDistance != null) {
                distance = Math.min(distance, methodDistance);
            }
        }
        seed.setDistance(distance);
    }

    @Override
    public void assignEnergy(List<Seed> population) {
        for (Seed seed : population) {
            updateDistance(seed);
            double distance = seed.getDistance();
            // Positive fallback keeps selection defined even if every seed is unreachable.
            seed.setEnergy(Double.isFinite(distance) ? 1.0 / Math.pow(1.0 + distance, 2) : 1e-20);
        }
    }
}
