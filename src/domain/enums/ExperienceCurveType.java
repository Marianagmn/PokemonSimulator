package domain.enums;

/**
 * Enumerates the six official Pokémon experience growth curves.
 *
 * <p>Each curve defines a different relationship between total
 * accumulated experience and level. Using an enum instead of
 * raw strings provides type safety, enables pattern matching,
 * and simplifies persistence.
 *
 * @see domain.services.ExperienceCurve
 */
public enum ExperienceCurveType {

    ERRATIC("Erratic"),
    FAST("Fast"),
    MEDIUM_FAST("Medium Fast"),
    MEDIUM_SLOW("Medium Slow"),
    SLOW("Slow"),
    FLUCTUATING("Fluctuating");

    private final String displayName;

    ExperienceCurveType(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the human-readable display name.
     *
     * @return the curve's display name (e.g., "Medium Fast")
     */
    public String displayName() {
        return displayName;
    }
}
