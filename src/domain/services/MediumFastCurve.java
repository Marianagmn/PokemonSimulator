package domain.services;

import domain.enums.ExperienceCurveType;
import domain.model.vo.Experience;
import domain.model.vo.Level;

/**
 * Medium Fast experience curve implementation.
 *
 * <p>The most common experience group in Pokémon. Uses the formula:
 * <pre>
 *     totalExp = level³
 * </pre>
 *
 * <p>Examples:
 * <ul>
 *   <li>Level 5 → 125 exp</li>
 *   <li>Level 10 → 1,000 exp</li>
 *   <li>Level 50 → 125,000 exp</li>
 *   <li>Level 100 → 1,000,000 exp</li>
 * </ul>
 *
 * <p>This implementation is stateless and thread-safe (singleton-safe).
 *
 * @see ExperienceCurve
 * @see ExperienceCurveType#MEDIUM_FAST
 */
public final class MediumFastCurve implements ExperienceCurve {

    /** Singleton instance — this service is stateless. */
    public static final ExperienceCurve INSTANCE = new MediumFastCurve();

    private MediumFastCurve() {}

    /**
     * Precomputed experience thresholds for all 100 levels.
     *
     * <p>Index 0 = level 1, index 99 = level 100.
     * Computed once at class load for O(1) lookups.
     */
    private static final int[] THRESHOLDS = computeThresholds();

    private static int[] computeThresholds() {
        int[] table = new int[Level.MAX];
        for (int lvl = 1; lvl <= Level.MAX; lvl++) {
            long calculatedExp = (long) lvl * lvl * lvl;
            table[lvl - 1] = (int) Math.min(calculatedExp, Experience.MAX);
        }
        return table;
    }

    // ── ExperienceCurve implementation ───────────────────────

    @Override
    public Experience experienceForLevel(Level level) {
        return Experience.of(THRESHOLDS[level.value() - 1]);
    }

    @Override
    public Level levelForExperience(Experience experience) {
        int exp = experience.value();

        // Binary search on the precomputed table
        int low = 0;
        int high = THRESHOLDS.length - 1;
        int levelIndex = 0;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            if (THRESHOLDS[mid] <= exp) {
                levelIndex = mid;
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return Level.of(levelIndex + 1);
    }

    @Override
    public Experience experienceToNextLevel(Level level) {
        if (level.isMax()) {
            throw new IllegalArgumentException(
                    "Cannot compute experience to next level from max level (%d)"
                            .formatted(Level.MAX));
        }

        int current = THRESHOLDS[level.value() - 1];
        int next = THRESHOLDS[level.value()];

        return Experience.of(next - current);
    }

    @Override
    public ExperienceCurveType type() {
        return ExperienceCurveType.MEDIUM_FAST;
    }
}
