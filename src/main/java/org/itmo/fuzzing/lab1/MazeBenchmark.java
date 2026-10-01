package org.itmo.fuzzing.lab1;

import org.itmo.fuzzing.lect2.FunctionRunner;
import org.itmo.fuzzing.lect3.AFLFastSchedule;
import org.itmo.fuzzing.lect3.AdvancedMutationFuzzer;
import org.itmo.fuzzing.lect3.CountingGreyboxFuzzer;
import org.itmo.fuzzing.lect3.GreyBoxFuzzer;
import org.itmo.fuzzing.lect3.PowerSchedule;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Runs the selected fuzzers sequentially on the same randomly generated initial route.
 * Each fuzzer gets its own time budget and stops immediately when it finds SOLVED.
 * No target position or known solution is used to generate the initial route.
 *
 * <p>Options use {@code --name=value}. {@code --random-seed} reproduces the initial
 * route, not the subsequent random choices made by the fuzzers.</p>
 */
public final class MazeBenchmark {
    private static final int MIN_MUTATIONS = 64;
    private static final int MAX_MUTATIONS = 128;

    private MazeBenchmark() {
    }

    public static void main(String[] args) {
        for (String arg : args) {
            if (arg.equals("--help")) {
                System.out.println("MazeBenchmark --fuzzers=black,grey,aflfast --seconds=3600 "
                        + "--report-every=100000 --seed-length=32 [--random-seed=42] [--input-seed=DDRR]");
                System.out.println("Time limit is per fuzzer. Grey-box modes require the coverage javaagent.");
                System.out.println("seed-length is the maximum initial route length (chosen randomly in 1..seed-length).");
                System.out.println("input-seed supplies the route directly and overrides random route generation.");
                return;
            }
        }
        Options options = Options.parse(args);
        String inputSeed = options.inputSeed() != null ? options.inputSeed()
                : randomRoute(options.seedLength(), new Random(options.randomSeed()));
        var seeds = List.of(inputSeed);
        var mutator = new MazeMutator();

        String seedSource = options.inputSeed() != null ? "explicit"
                : "random-seed=" + options.randomSeed();
        System.out.printf(Locale.ROOT, "seed=%s source=%s limit=%ds/fuzzer report-every=%d%n",
                inputSeed, seedSource, options.seconds(), options.reportEvery());
        for (String mode : options.fuzzers()) {
            AdvancedMutationFuzzer fuzzer = switch (mode) {
                case "black" -> new AdvancedMutationFuzzer(
                        seeds, mutator, new PowerSchedule(), MIN_MUTATIONS, MAX_MUTATIONS);
                case "grey" -> new GreyBoxFuzzer(
                        seeds, mutator, new PowerSchedule(), MIN_MUTATIONS, MAX_MUTATIONS);
                case "aflfast" -> new CountingGreyboxFuzzer(
                        seeds, mutator, new AFLFastSchedule(5.0), MIN_MUTATIONS, MAX_MUTATIONS);
                default -> throw new IllegalArgumentException("Unknown fuzzer: " + mode);
            };
            run(mode, fuzzer, options);
        }
    }

    private static String randomRoute(int maxLength, Random random) {
        int length = random.nextInt(1, maxLength + 1);
        var route = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            route.append(MazeMutator.ALPHABET.charAt(random.nextInt(MazeMutator.ALPHABET.length())));
        }
        return route.toString();
    }

    private static void run(String mode, AdvancedMutationFuzzer fuzzer, Options options) {
        fuzzer.setRecordInputs(false);
        var runner = new FunctionRunner(MazeGenerated::maze);
        boolean[] coverageChecked = {false};
        long[] outcomes = new long[2]; // VALID, INVALID; SOLVED учитывается отдельно остановкой.
        long startedAt = System.nanoTime();
        var result = fuzzer.fuzz(runner, Duration.ofSeconds(options.seconds()), (input, output) -> {
            if (output instanceof String text) {
                if (text.startsWith("VALID\n")) {
                    outcomes[0]++;
                } else if (text.startsWith("INVALID\n")) {
                    outcomes[1]++;
                }
            }
            if (!coverageChecked[0] && fuzzer instanceof GreyBoxFuzzer) {
                if (runner.coverage.stream().noneMatch(point -> point.startsWith("tile_"))) {
                    throw new IllegalStateException("Grey-box requires maze coverage. "
                            + "Run with the coverage javaagent (Gradle task runWithAgent).");
                }
                coverageChecked[0] = true;
            }
            return output instanceof String text && text.startsWith("SOLVED\n");
        }, options.reportEvery(), progress -> printStatus(mode, "RUNNING", fuzzer,
                progress.executions(), progress.elapsed().toNanos(), outcomes[0], outcomes[1]));

        printStatus(mode, result.stoppedByCondition() ? "SOLVED" : "TIMEOUT", fuzzer,
                result.executions(), System.nanoTime() - startedAt, outcomes[0], outcomes[1]);
        if (result.stoppedByCondition()) {
            System.out.println(mode + " solution=" + result.input());
        }
    }

    private static void printStatus(String mode, String status, AdvancedMutationFuzzer fuzzer,
                                    long executions, long elapsedNanos, long valid, long invalid) {
        double seconds = elapsedNanos / 1_000_000_000.0;
        double rate = seconds == 0 ? 0 : executions / seconds;
        System.out.printf(Locale.ROOT, "%s %s executions=%d elapsed=%.2fs rate=%.0f/s corpus=%d coverage=%d valid=%d invalid=%d%n",
                mode, status, executions, seconds, rate, fuzzer.population.size(), fuzzer.coveragesSeen.size(),
                valid, invalid);
    }

    private record Options(List<String> fuzzers, long seconds, long reportEvery,
                           int seedLength, long randomSeed, String inputSeed) {
        private static Options parse(String[] args) {
            List<String> fuzzers = List.of("black", "grey", "aflfast");
            long seconds = 3600;
            long reportEvery = 100_000;
            int seedLength = 16;
            long randomSeed = new Random().nextLong();
            String inputSeed = null;
            for (String arg : args) {
                String[] parts = arg.split("=", 2);
                if (parts.length != 2) {
                    throw new IllegalArgumentException("Expected --name=value, got: " + arg);
                }
                switch (parts[0]) {
                    case "--fuzzers" -> {
                        var selected = new ArrayList<String>();
                        for (String mode : parts[1].split(",", -1)) {
                            mode = mode.trim();
                            if (!List.of("black", "grey", "aflfast").contains(mode)) {
                                throw new IllegalArgumentException("Unknown fuzzer: " + mode);
                            }
                            if (!selected.contains(mode)) {
                                selected.add(mode);
                            }
                        }
                        fuzzers = List.copyOf(selected);
                    }
                    case "--seconds" -> seconds = Long.parseLong(parts[1]);
                    case "--report-every" -> reportEvery = Long.parseLong(parts[1]);
                    case "--seed-length" -> seedLength = Integer.parseInt(parts[1]);
                    case "--random-seed" -> randomSeed = Long.parseLong(parts[1]);
                    case "--input-seed" -> inputSeed = parts[1];
                    default -> throw new IllegalArgumentException("Unknown option: " + parts[0]);
                }
            }
            if (seconds <= 0 || seconds > Long.MAX_VALUE / 1_000_000_000L || reportEvery <= 0
                    || seedLength < 1 || seedLength > MazeMutator.DEFAULT_MAX_LENGTH) {
                throw new IllegalArgumentException("seconds and report-every must be positive; "
                        + "seconds must fit nanoseconds; seed-length must be in 1..64");
            }
            if (inputSeed != null && !inputSeed.matches("[LRUD]{0,64}")) {
                throw new IllegalArgumentException("input-seed must contain only L/R/U/D and be at most 64 characters");
            }
            return new Options(fuzzers, seconds, reportEvery, seedLength, randomSeed, inputSeed);
        }
    }
}
