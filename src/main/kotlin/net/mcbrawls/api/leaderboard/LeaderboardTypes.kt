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
            executeQuery("SELECT player_id, COUNT(player_id) value FROM StatisticEvents WHERE game_type = 'rocket_spleef' AND cause_id = 'rockets_fired' GROUP BY player_id ORDER BY value DESC LIMIT 10")
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
}
