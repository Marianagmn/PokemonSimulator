package domain.services;

import domain.enums.StatType;

/**
 * Functional interface for applying post-calculation modifications
 * to individual stats.
 *
 * <p>This is the extension point for mechanics that alter a Pokémon's
 * stats after the base formula has been applied, such as:
 * <ul>
 *   <li><b>Nature</b> — ×1.1 boost / ×0.9 hinder</li>
 *   <li><b>Items</b> — Choice Band, Life Orb, etc.</li>
 *   <li><b>Abilities</b> — Huge Power, Pure Power, etc.</li>
 * </ul>
 *
 * <p>Modifiers are composable via {@link #andThen(StatModifier)},
 * allowing a chain of modifications to be applied in sequence.
 *
 * <p><b>Design note:</b> This is intentionally separate from
 * {@link StatCalculator} to keep the base formula pure. Modifiers
 * are applied externally by the caller.
 *
 * @see NatureModifier
 * @see StatCalculator
 */
@FunctionalInterface
public interface StatModifier {

    /**
     * Applies this modifier to a single stat value.
     *
     * @param stat the raw stat value (after base calculation)
     * @param type the stat being modified
     * @return the modified stat value
     */
    int apply(int stat, StatType type);

    /**
     * Composes this modifier with another, applying this one first.
     *
     * @param after the modifier to apply after this one
     * @return a composed modifier
     */
    default StatModifier andThen(StatModifier after) {
        return (stat, type) -> after.apply(this.apply(stat, type), type);
    }

    /**
     * Returns an identity modifier that returns the stat unchanged.
     *
     * @return a no-op modifier
     */
    static StatModifier identity() {
        return (stat, type) -> stat;
    }
}
