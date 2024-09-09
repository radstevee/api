package net.mcbrawls.api.leaderboard

import net.mcbrawls.api.registry.BasicRegistry

object LeaderboardTypes : BasicRegistry<LeaderboardType>() {
    val TOTAL_EXPERIENCE = register(
        "total_experience",
        LeaderboardType("Total Experience Leaderboard") {
            executeQuery("""
                SELECT
                    player_id,
                    SUM(experience_amount) value
                FROM
                    StatisticEvents
                GROUP BY
                    player_id
                ORDER BY
                    value DESC
                LIMIT
                    10
                """.trimIndent())
        }
    )

    val DODGEBOLT_ROUNDS_WON = register(
        "dodgebolt_rounds_won",
        LeaderboardType("Dodgebolt Rounds Won Leaderboard") {
            executeQuery(
                """
                    SELECT 
                        player_id, 
                        COUNT(player_id) AS value 
                    FROM 
                        StatisticEvents 
                    WHERE 
                        game_type = 'dodgebolt' 
                        AND cause_id = 'round_win' 
                    GROUP BY 
                        player_id 
                    ORDER BY 
                        value DESC 
                    LIMIT 
                        10
                """.trimIndent()
            )
        }
    )

    val DODGEBOLT_GAMES_WON = register(
        "dodgebolt_games_won",
        LeaderboardType("Dodgebolt Games Won Leaderboard") {
            executeQuery(
                """
                    SELECT 
                        player_id, 
                        COUNT(player_id) AS value 
                    FROM 
                        StatisticEvents 
                    WHERE 
                        game_type = 'dodgebolt' 
                        AND cause_id = 'win' 
                    GROUP BY 
                        player_id 
                    ORDER BY 
                        value DESC 
                    LIMIT 
                        10
                """.trimIndent()
            )
        }
    )

    val DODGEBOLT_KILLS = register(
        "dodgebolt_kills",
        LeaderboardType("Dodgebolt Kills Leaderboard") {
            executeQuery(
                """
                    SELECT 
                        player_id, 
                        COUNT(player_id) AS value 
                    FROM 
                        StatisticEvents 
                    WHERE 
                        game_type = 'dodgebolt' 
                        AND cause_id = 'kill' 
                    GROUP BY 
                        player_id
                    ORDER BY
                        value DESC
                    LIMIT 
                        10
                """.trimIndent()
            )
        }
    )

    val DODGEBOLT_DEATHS = register(
        "dodgebolt_deaths",
        LeaderboardType("Dodgebolt Deaths Leaderboard") {
            executeQuery(
                """
                    SELECT 
                        player_id, 
                        COUNT(player_id) AS value 
                    FROM 
                        StatisticEvents 
                    WHERE 
                        game_type = 'dodgebolt' 
                        AND cause_id = 'death' 
                    GROUP BY 
                        player_id
                    ORDER BY
                        value DESC
                    LIMIT 
                        10
                """.trimIndent()
            )
        }
    )

    val DODGEBOLT_HIT_RATIO = register(
        "dodgebolt_hit_ratio",
        LeaderboardType("Dodgebolt Shots Hit / Fired Ratio Leaderboard") {
            executeQuery("""
                SELECT
                    player_id,
                    COUNT(CASE WHEN cause_id = 'arrow_hit' THEN 1 END) AS shots_hit,
                    COUNT(CASE WHEN cause_id = 'arrow_fired' THEN 1 END) AS shots_fired,
                    COALESCE(COUNT(CASE WHEN cause_id = 'arrow_hit' THEN 1 END) / NULLIF(COUNT(CASE WHEN cause_id = 'arrow_fired' THEN 1 END), 0), 0) * 100 AS value
                FROM
                    StatisticEvents
                WHERE
                    game_type = 'dodgebolt'
                GROUP BY
                    player_id
                HAVING
                    COUNT(CASE WHEN cause_id = 'arrow_fired' THEN 1 END) > 15
                ORDER BY
                    value DESC
                LIMIT
                    10
                """.trimIndent())
        }
    )

    val DODGEBOLT_KILL_DEATH_RATIO = register(
        "dodgebolt_kill_death_ratio",
        LeaderboardType("Dodgebolt Kill / Death Ratio Leaderboard") {
            executeQuery("""
                SELECT
                    player_id,
                    COUNT(CASE WHEN cause_id = 'kill' THEN 1 END) AS kills,
                    COUNT(CASE WHEN cause_id = 'death' THEN 1 END) AS deaths,
                    COALESCE(COUNT(CASE WHEN cause_id = 'kill' THEN 1 END) / NULLIF(COUNT(CASE WHEN cause_id = 'death' THEN 1 END), 0), 0) * 100 AS value
                FROM
                    StatisticEvents
                WHERE
                    game_type = 'dodgebolt'
                GROUP BY
                    player_id
                HAVING
                    COUNT(CASE WHEN cause_id = 'kill' THEN 1 END) > 10
                ORDER BY
                    value DESC
                LIMIT
                    10
                """.trimIndent())
        }
    )

    val OLD_RISE_SURVIVAL = register(
        "old_rise_survival",
        LeaderboardType("Rise Survival Leaderboard") {
            executeQuery(
                """
                    SELECT 
                        player_id, 
                        COUNT(player_id) AS value 
                    FROM 
                        StatisticEvents 
                    WHERE 
                        game_type = 'old_rise' 
                        AND cause_id = 'outlive' 
                    GROUP BY 
                        player_id 
                    ORDER BY 
                        value DESC 
                    LIMIT 
                        10
                """.trimIndent()
            )
        }
    )

    val OLD_RISE_ROUNDS_WON = register(
        "old_rise_rounds_won",
        LeaderboardType("Rise Rounds Won Leaderboard") {
            executeQuery(
                """
                    SELECT 
                        player_id, 
                        COUNT(player_id) AS value 
                    FROM 
                        StatisticEvents 
                    WHERE 
                        game_type = 'old_rise' 
                        AND cause_id = 'round_win' 
                    GROUP BY 
                        player_id 
                    ORDER BY 
                        value DESC 
                    LIMIT 
                        10
                """.trimIndent()
            )
        }
    )

    val OLD_RISE_DEATHS = register(
        "old_rise_deaths",
        LeaderboardType("Rise Deaths Leaderboard") {
            executeQuery(
                """
                    SELECT 
                        player_id, 
                        COUNT(player_id) AS value 
                    FROM 
                        StatisticEvents 
                    WHERE 
                        game_type = 'old_rise' 
                        AND cause_id = 'death' 
                    GROUP BY 
                        player_id 
                    ORDER BY 
                        value DESC 
                    LIMIT 
                        10
                """.trimIndent()
            )
        }
    )

    val OLD_RISE_POWDER_FLOORS = register(
        "old_rise_powder_floors",
        LeaderboardType("Rise Floor Drops Survived Leaderboard") {
            executeQuery(
                """
                    SELECT 
                        player_id, 
                        COUNT(player_id) AS value 
                    FROM 
                        StatisticEvents 
                    WHERE 
                        game_type = 'old_rise' 
                        AND cause_id = 'powder_floors' 
                    GROUP BY 
                        player_id 
                    ORDER BY 
                        value DESC 
                    LIMIT 
                        10
                """.trimIndent()
            )
        }
    )

    val ROCKETS_FIRED = register(
        "rockets_fired",
        LeaderboardType("Rockets Fired Leaderboard") {
            executeQuery(
                """
                    SELECT
                        player_id,
                        COUNT(player_id) value
                    FROM
                        StatisticEvents
                    WHERE
                        game_type = 'rocket_spleef' 
                        AND cause_id = 'rocket_fired'
                    GROUP BY
                        player_id
                    ORDER BY
                        value DESC
                    LIMIT
                        10
                """.trimIndent()
            )
        }
    )

    val ROCKETS_HIT = register(
        "rockets_hit",
        LeaderboardType("Direct Rockets Hit Leaderboard") {
            executeQuery(
                """
                    SELECT
                        player_id,
                        COUNT(player_id) value
                    FROM
                        StatisticEvents
                    WHERE
                        game_type = 'rocket_spleef' 
                        AND cause_id = 'rocket_hit'
                    GROUP BY
                        player_id
                    ORDER BY
                        value DESC
                    LIMIT
                        10
                """.trimIndent()
            )
        }
    )

    val ROCKET_SPLEEF_SURVIVAL = register(
        "rocket_spleef_survival",
        LeaderboardType("Rocket Spleef Survival Leaderboard") {
            executeQuery(
                """
                    SELECT
                        player_id,
                        COUNT(player_id) value
                    FROM
                        StatisticEvents
                    WHERE
                        game_type = 'rocket_spleef' 
                        AND cause_id = 'outlive'
                    GROUP BY
                        player_id
                    ORDER BY
                        value DESC
                    LIMIT
                        10
                """.trimIndent()
            )
        }
    )

    val ROCKET_SPLEEF_KILLS = register(
        "rocket_spleef_kills",
        LeaderboardType("Rocket Spleef Kills Leaderboard") {
            executeQuery(
                """
                    SELECT
                        player_id,
                        COUNT(player_id) value
                    FROM
                        StatisticEvents
                    WHERE
                        game_type = 'rocket_spleef' 
                        AND cause_id = 'kill'
                    GROUP BY
                        player_id
                    ORDER BY
                        value DESC
                    LIMIT
                        10
                """.trimIndent()
            )
        }
    )

    val ROCKET_SPLEEF_PLACEMENT = register(
        "rocket_spleef_placement",
        LeaderboardType("Rocket Spleef Placement Leaderboard") {
            executeQuery(
                """
                    SELECT
                        player_id,
                        SUM(experience_amount) value
                    FROM
                        StatisticEvents
                    WHERE
                        game_type = 'rocket_spleef' 
                        AND cause_id = 'placement'
                    GROUP BY
                        player_id
                    ORDER BY
                        value DESC
                    LIMIT
                        10
                """.trimIndent()
            )
        }
    )

    val RISE_CAPTURE_TIMES_CAPTURED = register(
        "rise_capture_times_captured",
        LeaderboardType("Rise Capture Times Captured Leaderboard") {
            executeQuery(
                """
                    SELECT
                        player_id,
                        SUM(experience_amount) value
                    FROM
                        StatisticEvents
                    WHERE
                        game_type = 'rise_capture' 
                        AND cause_id = 'capture'
                    GROUP BY
                        player_id
                    ORDER BY
                        value DESC
                    LIMIT
                        10
                """.trimIndent()
            )
        }
    )

    val RISE_CAPTURE_TIMES_COLLECTED = register(
        "rise_capture_times_collected",
        LeaderboardType("Rise Capture Times Collected Leaderboard") {
            executeQuery(
                """
                    SELECT
                        player_id,
                        SUM(experience_amount) value
                    FROM
                        StatisticEvents
                    WHERE
                        game_type = 'rise_capture' 
                        AND cause_id = 'capture_collected'
                    GROUP BY
                        player_id
                    ORDER BY
                        value DESC
                    LIMIT
                        10
                """.trimIndent()
            )
        }
    )

    val RISE_CAPTURE_TIMES_CAPTURE_LOST = register(
        "rise_capture_times_capture_lost",
        LeaderboardType("Rise Capture Times Capture Lost Leaderboard") {
            executeQuery(
                """
                    SELECT
                        player_id,
                        SUM(experience_amount) value
                    FROM
                        StatisticEvents
                    WHERE
                        game_type = 'rise_capture' 
                        AND cause_id = 'capture_lost'
                    GROUP BY
                        player_id
                    ORDER BY
                        value DESC
                    LIMIT
                        10
                """.trimIndent()
            )
        }
    )

    val RISE_CAPTURE_GAMES_WON = register(
        "rise_capture_games_won",
        LeaderboardType("Rise Capture Games Won Leaderboard") {
            executeQuery(
                """
                    SELECT
                        player_id,
                        SUM(experience_amount) value
                    FROM
                        StatisticEvents
                    WHERE
                        game_type = 'rise_capture' 
                        AND cause_id = 'win'
                    GROUP BY
                        player_id
                    ORDER BY
                        value DESC
                    LIMIT
                        10
                """.trimIndent()
            )
        }
    )

    val RISE_CAPTURE_GAMES_LOST = register(
        "rise_capture_games_lost",
        LeaderboardType("Rise Capture Games Lost Leaderboard") {
            executeQuery(
                """
                    SELECT
                        player_id,
                        SUM(experience_amount) value
                    FROM
                        StatisticEvents
                    WHERE
                        game_type = 'rise_capture' 
                        AND cause_id = 'loss'
                    GROUP BY
                        player_id
                    ORDER BY
                        value DESC
                    LIMIT
                        10
                """.trimIndent()
            )
        }
    )

    val RISE_CAPTURE_KILLS = register(
        "rise_capture_kills",
        LeaderboardType("Rise Capture Kills Leaderboard") {
            executeQuery(
                """
                    SELECT
                        player_id,
                        SUM(experience_amount) value
                    FROM
                        StatisticEvents
                    WHERE
                        game_type = 'rise_capture' 
                        AND cause_id = 'kill'
                    GROUP BY
                        player_id
                    ORDER BY
                        value DESC
                    LIMIT
                        10
                """.trimIndent()
            )
        }
    )

    val RISE_CAPTURE_FALLS = register(
        "rise_capture_falls",
        LeaderboardType("Rise Capture Falls Leaderboard") {
            executeQuery(
                """
                    SELECT
                        player_id,
                        SUM(experience_amount) value
                    FROM
                        StatisticEvents
                    WHERE
                        game_type = 'rise_capture' 
                        AND cause_id = 'fall'
                    GROUP BY
                        player_id
                    ORDER BY
                        value DESC
                    LIMIT
                        10
                """.trimIndent()
            )
        }
    )
}
