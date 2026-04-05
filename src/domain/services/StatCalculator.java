package domain.services;

import domain.model.Stats;
import domain.model.vo.Level;

/**
 * Domain service interface for calculating a Pokémon's actual stats.
 *
 * <p>In Pokémon, a creature's effective stats are derived from:
 * <ul>
 *   <li><b>Base stats</b> — inherent to the species (e.g., Pikachu's base Speed is 90)</li>
 *   <li><b>IVs</b> (Individual Values) — random per Pokémon, range [0, 31]</li>
 *   <li><b>EVs</b> (Effort Values) — gained through battle, range [0, 252] per stat</li>
 *   <li><b>Level</b> — the Pokémon's current level</li>
 * </ul>
 *
 * <p>This interface follows the <b>Strategy Pattern</b> — different
 * generations may use different formulas.
 *
 * <p><b>Design note:</b> This is a pure domain service (no I/O).
 * Implementations should be stateless and thread-safe.
 *
 * @see DefaultStatCalculator
 */
public interface StatCalculator {

    /**
     * Calculates the full stat block for a Pokémon at the given level.
     *
     * @param baseStats the species' base stats
     * @param ivs       the Pokémon's individual values (one per stat)
     * @param evs       the Pokémon's effort values (one per stat)
     * @param level     the Pokémon's current level
     * @return the calculated effective stats
     */
    Stats calculate(Stats baseStats, Stats ivs, Stats evs, Level level);

    /**
     * Calculates only the max HP for a Pokémon at the given level.
     *
     * <p>HP uses a different formula than other stats in official games,
     * so it is exposed separately for convenience.
     *
     * @param baseHp the species' base HP
     * @param iv     the HP individual value [0, 31]
     * @param ev     the HP effort value [0, 252]
     * @param level  the Pokémon's current level
     * @return the calculated max HP
     */
    int calculateHp(int baseHp, int iv, int ev, Level level);

    /**
     * Calculates a single non-HP stat for a Pokémon at the given level.
     *
     * @param baseStat the species' base stat value
     * @param iv       the stat's individual value [0, 31]
     * @param ev       the stat's effort value [0, 252]
     * @param level    the Pokémon's current level
     * @return the calculated stat value
     */
    int calculateStat(int baseStat, int iv, int ev, Level level);
}
