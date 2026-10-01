package org.itmo.fuzzing.lab1;

import org.itmo.fuzzing.lect3.FuzzMutator;

/**
 * Мутатор маршрутов для {@link MazeGenerated#maze(String)}.
 *
 * <p>Класс наследует {@link FuzzMutator}, поэтому экземпляр {@code MazeMutator} можно напрямую
 * передавать фаззерам из пакета {@code org.itmo.fuzzing.lect3}. Готовый {@code FuzzMutator}
 * использовать без адаптации нельзя: он генерирует произвольные печатные символы, тогда как
 * маршрут лабиринта должен состоять только из {@code L}, {@code R}, {@code U} и {@code D}.</p>
 *
 * <p>Реализуйте четыре мутации:</p>
 * <ul>
 *     <li>{@link #append(String)} — добавить случайный ход в конец;</li>
 *     <li>{@link #insert(String)} — вставить случайный ход в случайную позицию;</li>
 *     <li>{@link #replace(String)} — заменить случайный существующий ход;</li>
 *     <li>{@link #delete(String)} — удалить случайный существующий ход.</li>
 * </ul>
 *
 * <p>{@link #mutate(String)} должен случайно выбирать одну из этих операций. Один и тот же
 * экземпляр мутационной стратегии необходимо использовать в dumb black-box, coverage-guided и
 * directed конфигурациях: иначе результаты экспериментов будут несопоставимы.</p>
 *
 * <h2>Граничные случаи</h2>
 * <ul>
 *     <li>Мутации не должны падать на пустой строке.</li>
 *     <li>Для пустой строки {@code replace} может работать как {@code append}, а {@code delete}
 *     должен вернуть пустую строку.</li>
 *     <li>Результат не должен быть длиннее {@link #maxLength}; при достижении лимита выбирайте
 *     операцию, которая не увеличивает строку.</li>
 *     <li>Конструктор должен отклонять некорректное ограничение длины через
 *     {@link IllegalArgumentException}.</li>
 * </ul>
 */
public final class MazeMutator extends FuzzMutator {

    public static final String ALPHABET = "LRUD";
    public static final int DEFAULT_MAX_LENGTH = 64;

    private final int maxLength;

    /**
     * Создаёт мутатор с ограничением длины {@link #DEFAULT_MAX_LENGTH}.
     */
    public MazeMutator() {
        this(DEFAULT_MAX_LENGTH);
    }

    /**
     * Создаёт мутатор с заданным ограничением длины маршрута.
     *
     * @param maxLength максимальная допустимая длина результата
     */
    public MazeMutator(int maxLength) {
        if (maxLength < 0) {
            throw new IllegalArgumentException("maxLength must be non-negative");
        }
        this.maxLength = maxLength;
    }

    /**
     * Выполняет одну случайную мутацию маршрута.
     *
     * @param input исходный маршрут
     * @return мутированный маршрут из символов {@link #ALPHABET}, не длиннее {@link #maxLength}
     */
    @Override
    public String mutate(String input) {
        java.util.Objects.requireNonNull(input, "input");

        mutators.clear();
        if (input.length() < maxLength) {
            mutators.add(this::append);
            mutators.add(this::insert);
        }
        if (!input.isEmpty()) {
            mutators.add(this::replace);
            mutators.add(this::delete);
        }

        return mutators.isEmpty() ? input : super.mutate(input);
    }

    /**
     * Добавляет случайный символ из {@link #ALPHABET} в конец строки, не превышая
     * {@link #maxLength}.
     */
    public String append(String input) {
        java.util.Objects.requireNonNull(input, "input");
        if (input.length() >= maxLength) {
            return input;
        }
        return input + randomMove();
    }

    /**
     * Вставляет случайный символ из {@link #ALPHABET} в случайную позицию, не превышая
     * {@link #maxLength}.
     */
    public String insert(String input) {
        java.util.Objects.requireNonNull(input, "input");
        if (input.length() >= maxLength) {
            return input;
        }
        int position = random.nextInt(input.length() + 1);
        return input.substring(0, position) + randomMove() + input.substring(position);
    }

    /**
     * Заменяет случайный символ маршрута на символ из {@link #ALPHABET}.
     */
    public String replace(String input) {
        java.util.Objects.requireNonNull(input, "input");
        if (input.isEmpty()) {
            return append(input);
        }
        int position = random.nextInt(input.length());
        return input.substring(0, position) + randomMove() + input.substring(position + 1);
    }

    /**
     * Удаляет случайный символ маршрута; для пустой строки возвращает пустую строку.
     */
    public String delete(String input) {
        java.util.Objects.requireNonNull(input, "input");
        if (input.isEmpty()) {
            return input;
        }
        int position = random.nextInt(input.length());
        return input.substring(0, position) + input.substring(position + 1);
    }

    private char randomMove() {
        return ALPHABET.charAt(random.nextInt(ALPHABET.length()));
    }
}
