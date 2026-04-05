package domain.enums;

/**
 * Enumerates the six individual stats of a Pokémon.
 *
 * <p>Used by {@link domain.services.StatModifier} to discriminate
 * which stat is being modified (e.g., Nature boosts Attack but
 * lowers Speed).
 *
 * @see domain.model.Stats
 * @see domain.services.StatModifier
 */
public enum StatType {

    HP,
    ATTACK,
    DEFENSE,
    SPECIAL_ATTACK,
    SPECIAL_DEFENSE,
    SPEED
}
