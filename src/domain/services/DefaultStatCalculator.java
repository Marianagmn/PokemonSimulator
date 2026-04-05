package domain.services;

import domain.model.Stats;
import domain.model.vo.Level;

/**
 * Default stat calculator using the Generation III+ formula.
 *
 * <p><b>HP formula:</b>
 * <pre>
 *     HP = floor((2 × Base + IV + floor(EV / 4)) × Level / 100) + Level + 10
 * </pre>
 *
 * <p><b>Other stats formula:</b>
 * <pre>
 *     Stat = floor((2 × Base + IV + floor(EV / 4)) × Level / 100) + 5
 * </pre>
 *
 * <p><b>Note:</b> Nature modifiers (×1.1 / ×0.9) are NOT applied here.
 * Use {@link StatModifier} (e.g., {@link NatureModifier}) to apply
 * Nature bonuses externally after calling this calculator.
 *
 * <p>The calculation is intentionally <b>pure</b> — no artificial clamping
 * is applied. The {@link Stats} record's constructor enforces its own
 * domain invariants independently.
 *
 * <p>Each step is decomposed into named intermediate values
 * ({@code base}, {@code scaled}) for readability and debuggability.
 *
 * <p>Stateless and thread-safe (singleton-safe).
 *
 * @see StatCalculator
 * @see StatModifier
 * @see NatureModifier
 * @see <a href="https://bulbapedia.bulbagarden.net/wiki/Stat#Generation_III_onward">Bulbapedia — Stat Calculation</a>
 */
public final class DefaultStatCalculator implements StatCalculator {

    /** Singleton instance — this service is stateless. */
    public static final StatCalculator INSTANCE = new DefaultStatCalculator();

    private DefaultStatCalculator() {}

    private static final int IV_MAX = 31;
    private static final int EV_MAX = 252;

    @Override
    public Stats calculate(Stats baseStats, Stats ivs, Stats evs, Level level) {
        return Stats.of(
                calculateHp(baseStats.maxHp(), ivs.maxHp(), evs.maxHp(), level),
                calculateStat(baseStats.attack(), ivs.attack(), evs.attack(), level),
                calculateStat(baseStats.defense(), ivs.defense(), evs.defense(), level),
                calculateStat(baseStats.specialAttack(), ivs.specialAttack(), evs.specialAttack(), level),
                calculateStat(baseStats.specialDefense(), ivs.specialDefense(), evs.specialDefense(), level),
                calculateStat(baseStats.speed(), ivs.speed(), evs.speed(), level));
    }

    /**
     * {@inheritDoc}
     *
     * <p>Formula: {@code floor((2 × base + iv + floor(ev / 4)) × level / 100) + level + 10}
     */
    @Override
    public int calculateHp(int baseHp, int iv, int ev, Level level) {
        validateIv(iv);
        validateEv(ev);

        int lvl = level.value();
        int base = 2 * baseHp + iv + (ev / 4);
        int scaled = base * lvl / 100;

        return scaled + lvl + 10;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Formula: {@code floor((2 × base + iv + floor(ev / 4)) × level / 100) + 5}
     */
    @Override
    public int calculateStat(int baseStat, int iv, int ev, Level level) {
        validateIv(iv);
        validateEv(ev);

        int lvl = level.value();
        int base = 2 * baseStat + iv + (ev / 4);
        int scaled = base * lvl / 100;

        return scaled + 5;
    }

    // ── Validation ───────────────────────────────────────────

    private static void validateIv(int iv) {
        if (iv < 0 || iv > IV_MAX) {
            throw new IllegalArgumentException(
                    "IV must be between 0 and %d, but was: %d"
                            .formatted(IV_MAX, iv));
        }
    }

    private static void validateEv(int ev) {
        if (ev < 0 || ev > EV_MAX) {
            throw new IllegalArgumentException(
                    "EV must be between 0 and %d, but was: %d"
                            .formatted(EV_MAX, ev));
        }
    }
}
