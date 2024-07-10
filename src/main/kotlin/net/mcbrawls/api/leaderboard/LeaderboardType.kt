package net.mcbrawls.api.leaderboard

import java.sql.ResultSet
import java.sql.Statement

data class LeaderboardType(
    /**
     * The title of this leaderboard.
     */
    val title: String,

    /**
     * Provides the leaderboard results.
     */
    val resultProvider: Statement.() -> ResultSet
)
