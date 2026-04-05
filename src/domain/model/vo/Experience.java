package domain.model.vo;

import java.util.Objects;

/**
 * Value Object representing the accumulated experience of a Pokémon.
 *
 * <p>Domain properties:
 * <ul>
 *   <li>Monotonically increasing (never decreases)</li>
 *   <li>Bounded between {@value #MIN} and {@value #MAX}</li>
 *   <li>Saturates silently when reaching the maximum</li>
 * </ul>
 *
 * @see Level
 */
public record Experience(int value) implements Comparable<Experience> {

    public static final int MIN = 0;
    public static final int MAX = 1_000_000;

    public Experience {
        if (value < MIN || value > MAX) {
            throw new IllegalArgumentException(
                    "Experience must be between %d and %d, but was: %d"
                            .formatted(MIN, MAX, value));
        }
    }

    // ── Factory methods ──────────────────────────────────────

    public static Experience of(int value) {
        return new Experience(value);
    }

    public static Experience zero() {
        return new Experience(MIN);
    }

    public static Experience max() {
        return new Experience(MAX);
    }

    // ── Domain behavior ──────────────────────────────────────

    /**
     * Applies an experience gain.
     *
     * <p>Rules:
     * <ul>
     *   <li>Amount must be non-negative</li>
     *   <li>If the result exceeds {@value #MAX}, it saturates silently</li>
     * </ul>
     *
     * @param amount experience gained (must be {@code >= 0})
     * @return a new {@code Experience} instance with the updated value
     * @throws IllegalArgumentException if amount is negative
     */
    public Experience gain(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException(
                    "Experience gain must be non-negative, but was: " + amount);
        }

        int newValue = (int) Math.min(safeAdd(amount), MAX);
        return newValue == this.value ? this : new Experience(newValue);
    }

    /**
     * Returns the lower of two experience values.
     *
     * <p>Useful in progression logic (e.g., capping EXP to a threshold).
     *
     * @param other the experience to compare against
     * @return the lesser of the two experiences
     */
    public Experience min(Experience other) {
        Objects.requireNonNull(other, "Experience to compare cannot be null");
        return this.value <= other.value ? this : other;
    }

    /**
     * Returns the higher of two experience values.
     *
     * <p>Useful in progression logic (e.g., enforcing minimum EXP floors).
     *
     * @param other the experience to compare against
     * @return the greater of the two experiences
     */
    public Experience max(Experience other) {
        Objects.requireNonNull(other, "Experience to compare cannot be null");
        return this.value >= other.value ? this : other;
    }

    // ── State queries ────────────────────────────────────────

    public boolean isMax() {
        return value == MAX;
    }

    public boolean isZero() {
        return value == MIN;
    }

    /**
     * Determines whether a gain of the given amount will reach or exceed the cap.
     *
     * <p>Useful for UI or other systems to warn before applying the gain,
     * since experience beyond the cap is silently discarded.
     *
     * @param amount the potential experience gain
     * @return {@code true} if gaining this amount reaches or exceeds {@value #MAX}
     * @throws IllegalArgumentException if amount is negative
     */
    public boolean willReachOrExceedMax(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException(
                    "Experience gain must be non-negative, but was: " + amount);
        }
        return safeAdd(amount) >= MAX;
    }

    // ── Comparison ───────────────────────────────────────────

    @Override
    public int compareTo(Experience other) {
        Objects.requireNonNull(other, "Experience to compare cannot be null");
        return Integer.compare(this.value, other.value);
    }


    public boolean isGreaterOrEqual(Experience other) {
        return this.compareTo(other) >= 0;
    }

    public boolean isLessOrEqual(Experience other) {
        return this.compareTo(other) <= 0;
    }

    // ── Internal helpers ─────────────────────────────────────

    /**
     * Promotes to {@code long} to prevent integer overflow on addition.
     */
    private long safeAdd(int amount) {
        return (long) value + amount;
    }
}