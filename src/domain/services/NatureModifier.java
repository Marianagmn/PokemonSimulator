package domain.services;

import domain.enums.StatType;

import java.util.Objects;

/**
 * Stat modifier that applies a Pokémon's Nature bonuses.
 *
 * <p>In official Pokémon games, each Nature boosts one stat by 10%
 * and lowers another by 10%. Five neutral natures (Hardy, Docile,
 * Serious, Bashful, Quirky) have no effect.
 *
 * <p>Rules:
 * <ul>
 *   <li>Boosted stat: {@code floor(stat × 1.1)}</li>
 *   <li>Hindered stat: {@code floor(stat × 0.9)}</li>
 *   <li>HP is never affected by Nature</li>
 *   <li>If boosted == hindered → neutral (no modification)</li>
 * </ul>
 *
 * <p>Uses integer truncation ({@code Math.floor}) to match
 * Game Freak's official formulas.
 *
 * <p>Immutable and thread-safe.
 *
 * @see StatModifier
 */
public final class NatureModifier implements StatModifier {

    private static final double BOOST_MULTIPLIER = 1.1;
    private static final double HINDER_MULTIPLIER = 0.9;

    private final StatType boosted;
    private final StatType hindered;

    /**
     * Creates a Nature modifier.
     *
     * @param boosted  the stat that receives +10% (must not be HP)
     * @param hindered the stat that receives -10% (must not be HP)
     * @throws IllegalArgumentException if either stat is HP
     */
    public NatureModifier(StatType boosted, StatType hindered) {
        Objects.requireNonNull(boosted, "Boosted stat type cannot be null");
        Objects.requireNonNull(hindered, "Hindered stat type cannot be null");

        if (boosted == StatType.HP) {
            throw new IllegalArgumentException("HP cannot be boosted by Nature");
        }
        if (hindered == StatType.HP) {
            throw new IllegalArgumentException("HP cannot be hindered by Nature");
        }

        this.boosted = boosted;
        this.hindered = hindered;
    }

    /**
     * Creates a neutral Nature modifier (no stat changes).
     *
     * @return a modifier equivalent to {@link StatModifier#identity()}
     */
    public static NatureModifier neutral() {
        return new NatureModifier(StatType.ATTACK, StatType.ATTACK);
    }

    @Override
    public int apply(int stat, StatType type) {
        if (boosted == hindered) {
            return stat; // Neutral nature
        }

        if (type == boosted) {
            return (int) (stat * BOOST_MULTIPLIER);
        }
        if (type == hindered) {
            return (int) (stat * HINDER_MULTIPLIER);
        }

        return stat;
    }

    // ── Accessors ────────────────────────────────────────────

    public StatType boosted() {
        return boosted;
    }

    public StatType hindered() {
        return hindered;
    }

    public boolean isNeutral() {
        return boosted == hindered;
    }
}
