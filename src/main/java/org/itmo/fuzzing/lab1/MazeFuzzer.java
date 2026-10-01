package org.itmo.fuzzing.lab1;

import org.itmo.fuzzing.lect2.FunctionRunner;
import org.itmo.fuzzing.lect3.AFLFastSchedule;
import org.itmo.fuzzing.lect3.AdvancedMutationFuzzer;
import org.itmo.fuzzing.lect3.CountingGreyboxFuzzer;
import org.itmo.fuzzing.lect3.GreyBoxFuzzer;
import org.itmo.fuzzing.lect3.PowerSchedule;

import java.util.List;

/**
 * Точка входа первой лабораторной работы: поиск входа, для которого
 * {@link MazeGenerated#maze(String)} возвращает {@code SOLVED}.
 *
 * <p>Выполняйте работу последовательно. Все варианты должны использовать одинаковые начальные
 * сиды, {@link MazeMutator мутатор} и бюджет запусков. Отличаться должен только способ использования
 * обратной связи.</p>
 *
 * <h2>Что использовать из лекции 3</h2>
 * <p>Не переписывайте общий цикл mutation-based и greybox-фаззинга. Используйте и расширяйте
 * готовые классы из пакета {@code org.itmo.fuzzing.lect3}:</p>
 * <ul>
 *     <li>{@link AdvancedMutationFuzzer} — основа dumb black-box режима: генерация кандидата из
 *     начальных сидов и мутации без использования покрытия при пополнении corpus;</li>
 *     <li>{@link GreyBoxFuzzer} вместе с {@link PowerSchedule} — основа coverage-guided режима:
 *     сохранение входов, которые открыли новое покрытие, и выбор сида из corpus;</li>
 *     <li>{@link GreyBoxFuzzer} вместе с собственной стратегией, наследующей
 *     {@link PowerSchedule}, — основа directed-режима: энергия сида должна зависеть от расстояния
 *     до целевого метода;</li>
 *     <li>{@link Seed} — готовая модель сида с данными, покрытием, расстоянием и энергией;</li>
 *     <li>{@link CountingGreyboxFuzzer} и {@link AFLFastSchedule} — примеры того, как расширять
 *     greybox-фаззер, учитывать статистику путей и переопределять расчёт энергии;</li>
 *     <li>{@link FunctionRunner} — готовый адаптер для запуска целевой функции и получения
 *     покрытия; используйте его с {@code MazeGenerated::maze}.</li>
 * </ul>
 *
 * <h2>Этап 1. Dumb black-box</h2>
 * <ol>
 *     <li>Создайте {@link AdvancedMutationFuzzer} с начальным сидом, например {@code "D"}, и
 *     экземпляром {@link MazeMutator}.</li>
 *     <li>Мутируйте вход и запускайте целевую функцию.</li>
 *     <li>Не используйте покрытие, call graph или расстояния до цели.</li>
 *     <li>Остановитесь при {@code SOLVED} либо после исчерпания бюджета.</li>
 * </ol>
 *
 * <h2>Этап 2. Coverage-guided fuzzing</h2>
 * <ol>
 *     <li>Используйте {@link GreyBoxFuzzer}, {@link PowerSchedule} и тот же {@link MazeMutator}.</li>
 *     <li>Собирайте покрытие каждого запуска через выданную instrumentation-инфраструктуру.</li>
 *     <li>Добавляйте вход в corpus, только если он открыл новое покрытие.</li>
 *     <li>Выбирайте сиды из corpus без знания положения целевой клетки.</li>
 * </ol>
 *
 * <h2>Этап 3. Directed greybox fuzzing</h2>
 * <ol>
 *     <li>Дополните ASM-инструментацию сбором рёбер {@code caller -> callee}.</li>
 *     <li>Постройте call graph методов {@code tile_*} и вычислите кратчайшие расстояния до метода,
 *     имя которого возвращает {@link MazeGenerated#targetTile()}.</li>
 *     <li>Определите расстояние {@link Seed} до цели по покрытым им методам.</li>
 *     <li>Реализуйте собственную стратегию {@link PowerSchedule}, назначающую больше энергии
 *     сидам с меньшим расстоянием до цели.</li>
 * </ol>
 *
 * <h2>Что выдано</h2>
 * <ul>
 *     <li>целевая функция {@link MazeGenerated#maze(String)} и генератор лабиринта;</li>
 *     <li>{@link FunctionRunner} и instrumentation-инфраструктура сбора покрытия;</li>
 *     <li>общий цикл фаззинга, corpus, модель сида и базовые стратегии из лекции 3;</li>
 *     <li>контракты {@link MazeMutator} и этой точки входа.</li>
 * </ul>
 *
 * <h2>Что требуется реализовать</h2>
 * <ul>
 *     <li>операции {@link MazeMutator} для алфавита {@code L/R/U/D};</li>
 *     <li>конфигурацию и запуск трёх режимов на выданном каркасе;</li>
 *     <li>сбор рёбер call graph в ASM-инструментации;</li>
 *     <li>расчёт расстояний от методов до {@link MazeGenerated#targetTile()};</li>
 *     <li>расстояние сида и directed-стратегию назначения энергии;</li>
 *     <li>сбор и сравнение результатов эксперимента.</li>
 * </ul>
 *
 * <h2>Сравнение</h2>
 * <p>Для каждого режима проведите несколько запусков и сравните долю успешных запусков,
 * медианное число выполнений цели до {@code SOLVED} и время. Не включайте заранее известный
 * маршрут в начальный corpus.</p>
 */
public final class MazeFuzzer {

    static final int BLACK_MIN_MUTATIONS = 1;
    static final int BLACK_MAX_MUTATIONS = 64;
    static final int GREY_MIN_MUTATIONS = 1;
    static final int GREY_MAX_MUTATIONS = 5;

    private MazeFuzzer() {
    }

    /**
     * Реализуйте здесь конфигурацию и запуск трёх режимов на каркасе из лекции 3, а также вывод
     * сопоставимых результатов эксперимента.
     *
     * @param args необязательный бюджет каждого запуска (по умолчанию 10000)
     */
    public static void main(String[] args) {
        long budget = args.length == 0 ? 10_000 : Long.parseLong(args[0]);
        var seeds = List.of("D");
        var mutator = new MazeMutator();
        task1(budget, seeds, mutator);
        task2(budget, seeds, mutator);
        task3(budget, seeds, mutator);
        // TODO: добавить сравнение повторных экспериментов.
    }

    /**
     * Задача 1: dumb black-box фаззинг с остановкой при SOLVED или исчерпании бюджета.
     */
    private static void task1(long budget, List<String> seeds, MazeMutator mutator) {
        var fuzzer = new AdvancedMutationFuzzer(
                seeds, mutator, new PowerSchedule(), BLACK_MIN_MUTATIONS, BLACK_MAX_MUTATIONS);
        runExperiment("Task 1 / Dumb black-box", fuzzer, budget);
    }

    /**
     * Задача 2: grey-box с равномерной энергией и с приоритетом редких наборов покрытия.
     */
    private static void task2(long budget, List<String> seeds, MazeMutator mutator) {
        var uniformFuzzer = new GreyBoxFuzzer(
                seeds, mutator, new PowerSchedule(), GREY_MIN_MUTATIONS, GREY_MAX_MUTATIONS);
        runExperiment("Task 2 / Grey-box / Uniform energy", uniformFuzzer, budget);

        var rareCoverageFuzzer = new CountingGreyboxFuzzer(
                seeds, mutator, new AFLFastSchedule(5.0), GREY_MIN_MUTATIONS, GREY_MAX_MUTATIONS);
        runExperiment("Task 2 / Grey-box / AFLFast energy", rareCoverageFuzzer, budget);
    }

    /** Задача 3: энергия сида зависит от расстояния по call graph до targetTile(). */
    private static void task3(long budget, List<String> seeds, MazeMutator mutator) {
        var fuzzer = MazeDirected.createFuzzer(seeds, mutator, GREY_MIN_MUTATIONS, GREY_MAX_MUTATIONS);
        runExperiment("Task 3 / Directed grey-box", fuzzer, budget);
    }

    private static void runExperiment(String name, AdvancedMutationFuzzer fuzzer, long budget) {
        System.out.println("\n" + name);
        var runner = new FunctionRunner(MazeGenerated::maze);
        long startedAt = System.nanoTime();

        var result = fuzzer.fuzz(runner, budget,
                (input, output) -> {
                    if (fuzzer instanceof GreyBoxFuzzer
                            && runner.coverage.stream().noneMatch(point -> point.startsWith("tile_"))) {
                        throw new IllegalStateException(
                                "Grey-box requires maze coverage. Run MazeFuzzer with the coverage javaagent "
                                        + "(Gradle task runWithAgent).");
                    }
                    return output instanceof String text && text.startsWith("SOLVED\n");
                });
        double elapsedSeconds = (System.nanoTime() - startedAt) / 1_000_000_000.0;

        System.out.println("Status: " + (result.stoppedByCondition() ? "SOLVED" : "BUDGET EXHAUSTED"));
        System.out.println("Executions: " + result.executions());
        System.out.printf(java.util.Locale.ROOT, "Time: %.3f s%n", elapsedSeconds);
        System.out.println("Corpus size: " + fuzzer.population.size());
        if (result.stoppedByCondition()) {
            System.out.println("Input: " + result.input());
            System.out.println(result.result());
        }
    }
}
