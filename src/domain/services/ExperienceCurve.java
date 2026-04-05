package domain.services;

import domain.enums.ExperienceCurveType;
import domain.model.vo.Experience;
import domain.model.vo.Level;

/**
 * Domain service interface defining an experience growth curve.
 *
 * <p>In Pokémon, different species follow different experience curves
 * that determine how much total experience is needed to reach each level.
 * The six official curves are: Erratic, Fast, Medium Fast, Medium Slow,
 * Slow, and Fluctuating.
 *
 * <p>This interface follows the <b>Strategy Pattern</b> — each curve
 * is a separate implementation, and the Pokémon species references
 * its curve to determine level-up thresholds.
 *
 * <p><b>Design note:</b> This is a pure domain service (no I/O,
 * no side effects). Implementations should be stateless and thread-safe.
 *
 * @see MediumFastCurve
 * @see ExperienceCurveType
 */
public interface ExperienceCurve {

    /**
     * Returns the total cumulative experience required to reach the given level.
     *
     * <p>For example, in Medium Fast: level 10 requires 1,000 total experience.
     *
     * @param level the target level
     * @return the total experience needed to reach that level
     */
    Experience experienceForLevel(Level level);

    /**
     * Returns the level corresponding to a given total experience amount.
     *
     * <p>Finds the highest level whose threshold is at or below the given
     * experience. For example, in Medium Fast: 999 exp → level 9,
     * 1000 exp → level 10.
     *
     * @param experience the total accumulated experience
     * @return the level achieved with that experience
     */
    Level levelForExperience(Experience experience);

    /**
     * Returns the experience needed to advance from the given level
     * to the next level.
     *
     * <p>Equivalent to:
     * {@code experienceForLevel(level.next()) - experienceForLevel(level)}
     *
     * @param level the current level
     * @return the experience delta needed for the next level
     * @throws IllegalArgumentException if the level is already at max
     */
    Experience experienceToNextLevel(Level level);

    /**
     * Returns the type identifier of this experience curve.
     *
     * <p>Replaces a raw string name with a type-safe enum,
     * enabling pattern matching, persistence, and factory lookups.
     *
     * @return the curve type (e.g., {@link ExperienceCurveType#MEDIUM_FAST})
     */
    ExperienceCurveType type();
}
