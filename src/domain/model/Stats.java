package domain.model;

import java.util.Objects;

/**
 * Composite Value Object representing a Pokémon's stat block.
 *
 * <p>Represents either base stats (from species) or calculated stats
 * (from level, IVs, EVs). Does NOT include dynamic battle state
 * such as current HP or temporary stat stage modifiers.
 *
 * <p>All operations return new instances (immutable).
 *
 * <p>Individual stats are bounded between 0 and {@value #MAX_STAT}
 * ({@code maxHp} minimum is 1).
 *
 * @param maxHp          the maximum hit points (must be {@code >= 1})
 * @param attack         the physical attack stat (must be {@code >= 0})
 * @param defense        the physical defense stat (must be {@code >= 0})
 * @param specialAttack  the special attack stat (must be {@code >= 0})
 * @param specialDefense the special defense stat (must be {@code >= 0})
 * @param speed          the speed stat (must be {@code >= 0})
 */
public record Stats(
        int maxHp,
        int attack,
        int defense,
        int specialAttack,
        int specialDefense,
        int speed) {

    /**
     * Upper bound for any individual stat.
     * In official Pokémon games, stats are effectively capped at 999.
     */
    public static final int MAX_STAT = 999;

    public Stats {
        validateStat(maxHp, "maxHp", 1, MAX_STAT);
        validateStat(attack, "attack", 0, MAX_STAT);
        validateStat(defense, "defense", 0, MAX_STAT);
        validateStat(specialAttack, "specialAttack", 0, MAX_STAT);
        validateStat(specialDefense, "specialDefense", 0, MAX_STAT);
        validateStat(speed, "speed", 0, MAX_STAT);
    }

    private static void validateStat(int value, String name, int min, int max) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(
                    "%s must be between %d and %d, but was: %d"
                            .formatted(name, min, max, value));
        }
    }

    // ── Factory methods ──────────────────────────────────────

    public static Stats of(
            int maxHp,
            int attack,
            int defense,
            int specialAttack,
            int specialDefense,
            int speed) {
        return new Stats(maxHp, attack, defense, specialAttack, specialDefense, speed);
    }

    /**
     * Creates a zeroed stat block.
     *
     * <p>Intended for use as an accumulator (e.g., for EVs or IVs).
     *
     * <p><b>Note:</b> {@code maxHp} is set to 1 (not 0) because the domain
     * invariant requires HP to always be positive. This is a deliberate
     * trade-off to keep a single {@code Stats} type instead of introducing
     * a separate {@code PartialStats} class.
     *
     * @return a Stats instance with minimal values
     */
    public static Stats zero() {
        return new Stats(1, 0, 0, 0, 0, 0);
    }

    // ── EV-specific factory ─────────────────────────────────

    /** Maximum EVs a single stat can have. */
    private static final int EV_STAT_MAX = 252;

    /** Maximum total EVs across all six stats. */
    private static final int EV_TOTAL_MAX = 510;

    /**
     * Creates a Stats block specifically for Effort Values (EVs).
     *
     * <p>Validates both per-stat and total EV constraints:
     * <ul>
     *   <li>Each individual EV must be in [0, 252]</li>
     *   <li>The sum of all EVs must not exceed 510</li>
     * </ul>
     *
     * <p><b>Note:</b> The {@code hp} parameter maps to the {@code maxHp}
     * field internally. When used as EVs, a value of 0 is semantically
     * valid, but the general {@code Stats} constructor requires HP ≥ 1.
     * This factory handles the mapping transparently — if HP EV is 0,
     * it stores 1 internally to satisfy the invariant.
     *
     * @param hp   HP effort value [0, 252]
     * @param atk  Attack effort value [0, 252]
     * @param def  Defense effort value [0, 252]
     * @param spAtk Special Attack effort value [0, 252]
     * @param spDef Special Defense effort value [0, 252]
     * @param spd  Speed effort value [0, 252]
     * @return a valid EV stat block
     * @throws IllegalArgumentException if any individual EV exceeds 252
     *         or the total exceeds 510
     */
    public static Stats ofEvs(int hp, int atk, int def,
                               int spAtk, int spDef, int spd) {
        validateEv(hp, "HP");
        validateEv(atk, "Attack");
        validateEv(def, "Defense");
        validateEv(spAtk, "Special Attack");
        validateEv(spDef, "Special Defense");
        validateEv(spd, "Speed");

        int total = hp + atk + def + spAtk + spDef + spd;
        if (total > EV_TOTAL_MAX) {
            throw new IllegalArgumentException(
                    "Total EVs cannot exceed %d, but was: %d"
                            .formatted(EV_TOTAL_MAX, total));
        }

        return new Stats(Math.max(1, hp), atk, def, spAtk, spDef, spd);
    }

    private static void validateEv(int value, String name) {
        if (value < 0 || value > EV_STAT_MAX) {
            throw new IllegalArgumentException(
                    "%s EV must be between 0 and %d, but was: %d"
                            .formatted(name, EV_STAT_MAX, value));
        }
    }

    // ── Domain operations ────────────────────────────────────

    /**
     * Adds two stat blocks together (component-wise).
     *
     * <p>Useful for combining base stats with EVs or IVs.
     * Each resulting stat is capped at {@value #MAX_STAT}.
     *
     * @param other the stats to add
     * @return a new Stats instance with summed values
     */
    public Stats add(Stats other) {
        Objects.requireNonNull(other, "Stats to add cannot be null");

        return new Stats(
                (int) Math.min((long) this.maxHp + other.maxHp, MAX_STAT),
                (int) Math.min((long) this.attack + other.attack, MAX_STAT),
                (int) Math.min((long) this.defense + other.defense, MAX_STAT),
                (int) Math.min((long) this.specialAttack + other.specialAttack, MAX_STAT),
                (int) Math.min((long) this.specialDefense + other.specialDefense, MAX_STAT),
                (int) Math.min((long) this.speed + other.speed, MAX_STAT));
    }

    /**
     * Subtracts one stat block from another (component-wise).
     *
     * <p>Useful for debuffs or intermediate calculations.
     * Each resulting stat is floored at its minimum (1 for HP, 0 for others).
     *
     * @param other the stats to subtract
     * @return a new Stats instance with reduced values
     */
    public Stats subtract(Stats other) {
        Objects.requireNonNull(other, "Stats to subtract cannot be null");

        return new Stats(
                Math.max(1, this.maxHp - other.maxHp),
                Math.max(0, this.attack - other.attack),
                Math.max(0, this.defense - other.defense),
                Math.max(0, this.specialAttack - other.specialAttack),
                Math.max(0, this.specialDefense - other.specialDefense),
                Math.max(0, this.speed - other.speed));
    }

    /**
     * Scales all stats by a multiplier.
     *
     * <p>Uses {@link Math#floor} (truncation), consistent with Game Freak's
     * official stat calculation formulas. Each stat is floored at
     * its minimum allowed value and capped at {@value #MAX_STAT}.
     *
     * @param factor the multiplier (must be {@code >= 0})
     * @return a new Stats instance with scaled values
     * @throws IllegalArgumentException if factor is negative
     */
    public Stats scale(double factor) {
        if (factor < 0) {
            throw new IllegalArgumentException(
                    "Scale factor cannot be negative: " + factor);
        }

        return new Stats(
                scaleStat(maxHp, factor, 1),
                scaleStat(attack, factor, 0),
                scaleStat(defense, factor, 0),
                scaleStat(specialAttack, factor, 0),
                scaleStat(specialDefense, factor, 0),
                scaleStat(speed, factor, 0));
    }

    /**
     * Scales a single stat by a factor, truncating and clamping the result.
     */
    private static int scaleStat(int value, double factor, int min) {
        return Math.max(min, Math.min((int) Math.floor(value * factor), MAX_STAT));
    }

    // ── Domain queries ───────────────────────────────────────

    /**
     * Compares speed with another stat block.
     *
     * @param other the stats to compare against
     * @return {@code true} if this Pokémon is faster
     */
    public boolean isFasterThan(Stats other) {
        Objects.requireNonNull(other, "Stats to compare cannot be null");
        return this.speed > other.speed;
    }

    /**
     * Returns the higher of physical or special attack.
     *
     * <p>Useful for AI decisions or quick comparisons. For actual
     * damage calculation, use the stat matching the move's category.
     *
     * @return the dominant offensive stat value
     */
    public int dominantOffensiveStat() {
        return Math.max(attack, specialAttack);
    }

    /**
     * Returns the higher of physical or special defense.
     *
     * <p>Useful for AI decisions or quick comparisons. For actual
     * damage calculation, use the stat matching the move's category.
     *
     * @return the dominant defensive stat value
     */
    public int dominantDefensiveStat() {
        return Math.max(defense, specialDefense);
    }

    /**
     * Returns the sum of all base stats.
     *
     * <p>Commonly known as BST (Base Stat Total) in competitive Pokémon.
     *
     * @return the total of all six stats
     */
    public int total() {
        return maxHp + attack + defense + specialAttack + specialDefense + speed;
    }
}