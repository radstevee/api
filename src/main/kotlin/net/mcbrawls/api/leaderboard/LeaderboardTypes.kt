package net.mcbrawls.api.leaderboard

import net.mcbrawls.api.registry.BasicRegistry

object LeaderboardTypes : BasicRegistry<LeaderboardType>() {
    val TOTAL_EXPERIENCE = register(
        "total_experience",
        LeaderboardType("Total Experience Leaderboard") {
            executeQuery("SELECT player_id, SUM(experience_amount) value FROM StatisticEvents GROUP BY player_id ORDER BY value DESC LIMIT 10")
        }
    )

    val ROCKETS_FIRED = register(
        "rockets_fired",
        LeaderboardType("Rockets Fired Leaderboard") {
            executeQuery("SELECT player_id, COUNT(player_id) value FROM StatisticEvents WHERE game_type = 'rocket_spleef' AND cause_id = 'rocket_fired' GROUP BY player_id ORDER BY value DESC LIMIT 10")
        }
    )

    val DODGEBOLT_ROUNDS_WON = register(
        "dodgebolt_rounds_won",
        LeaderboardType("Dodgebolt Rounds Won Leaderboard") {
            executeQuery("SELECT player_id, COUNT(player_id) value FROM StatisticEvents WHERE game_type = 'dodgebolt' AND cause_id = 'round_win' GROUP BY player_id ORDER BY value DESC LIMIT 10")
        }
    )

    val OLD_RISE_POWDER_FLOORS = register(
        "old_rise_powder_floors",
        LeaderboardType("Rise Powder Floors Survived") {
            executeQuery("SELECT player_id, COUNT(player_id) value FROM StatisticEvents WHERE game_type = 'old_rise' AND cause_id = 'powder_floors' GROUP BY player_id ORDER BY value DESC LIMIT 10")
        }
    )

    val DODGEBOLT_HIT_RATIO = register(
        "dodgebolt_hit_ratio",
        LeaderboardType("Dodgebolt Arrow Hit Ratio (Shots Hit / Shots Fired)") {
            executeQuery("SELECT player_id, COUNT(CASE WHEN cause_id = 'arrow_hit' THEN 1 END) AS shots_hit, COUNT(CASE WHEN cause_id = 'arrow_fired' THEN 1 END) AS shots_fired, COALESCE(COUNT(CASE WHEN cause_id = 'arrow_hit' THEN 1 END) / NULLIF(COUNT(CASE WHEN cause_id = 'arrow_fired' THEN 1 END), 0), 0) * 100 AS value FROM StatisticEvents WHERE game_type = 'dodgebolt' GROUP BY player_id ORDER BY value DESC LIMIT 10")
        }
    )
}
