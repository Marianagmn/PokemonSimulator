package domain.model.vo;

import java.util.Objects;

/**
 * Value Object representing a Pokémon's level.
 *
 * <p>Domain properties:
 * <ul>
 *   <li>Bounded between {@value #MIN} and {@value #MAX}</li>
 *   <li>Discrete (integer values only)</li>
 *   <li>Monotonically increasing (never decreases)</li>
 * </ul>
 *
 * <p>Note: The logic for <i>when</i> to level up (based on experience)
 * does NOT belong in this class — that responsibility lies in
 * {@code ExperienceService}.
 *
 * @see Experience
 */
public record Level(int value) implements Comparable<Level> {

    public static final int MIN = 1;
    public static final int MAX = 100;

    public Level {
        if (value < MIN || value > MAX) {
            throw new IllegalArgumentException(
                    "Level must be between %d and %d, but was: %d"
                            .formatted(MIN, MAX, value));
        }
    }

    // ── Factory methods ──────────────────────────────────────

    public static Level of(int value) {
        return new Level(value);
    }

    public static Level min() {
        return new Level(MIN);
    }

    public static Level max() {
        return new Level(MAX);
    }

    // ── Domain behavior ──────────────────────────────────────

    /**
     * Returns the next level.
     *
     * <p>If already at max level, returns {@code this} (saturation).
     *
     * @return the next level, or this instance if already at max
     */
    public Level next() {
        return isMax() ? this : new Level(value + 1);
    }

    /**
     * Advances by multiple levels at once.
     *
     * <p>Useful for Rare Candy usage or massive experience gains
     * (e.g., defeating a boss). Saturates at {@value #MAX}.
     * Overflow-safe via {@code long} promotion.
     *
     * @param levels the number of levels to gain (must be {@code >= 0})
     * @return a new Level instance, capped at {@value #MAX}
     * @throws IllegalArgumentException if levels is negative
     */
    public Level increaseBy(int levels) {
        if (levels < 0) {
            throw new IllegalArgumentException(
                    "Level increment must be non-negative, but was: " + levels);
        }
        int newValue = (int) Math.min((long) value + levels, MAX);
        return newValue == this.value ? this : new Level(newValue);
    }

    // ── State queries ────────────────────────────────────────

    public boolean isMax() {
        return value == MAX;
    }

    public boolean isMin() {
        return value == MIN;
    }

    /**
     * Calculates the distance (in levels) to another level.
     *
     * <p>Useful for UI progress bars or experience calculations.
     * Always returns a non-negative value (absolute distance).
     *
     * @param other the target level
     * @return the absolute difference in levels
     */
    public int distanceTo(Level other) {
        Objects.requireNonNull(other, "Level to compare cannot be null");
        return Math.abs(this.value - other.value);
    }

    // ── Comparison ───────────────────────────────────────────

    @Override
    public int compareTo(Level other) {
        Objects.requireNonNull(other, "Level to compare cannot be null");
        return Integer.compare(this.value, other.value);
    }

    public boolean isGreaterOrEqual(Level other) {
        return this.compareTo(other) >= 0;
    }

    public boolean isLessOrEqual(Level other) {
        return this.compareTo(other) <= 0;
    }
}
