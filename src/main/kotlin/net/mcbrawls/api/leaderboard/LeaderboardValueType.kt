package net.mcbrawls.api.leaderboard

import net.mcbrawls.api.database.schema.StatisticEvents
import net.mcbrawls.api.leaderboard.LeaderboardType.LeaderboardQueryFactory
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.alias
import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.sum

enum class LeaderboardValueType(
    val leaderboardQuery: Transaction.() -> LeaderboardQueryFactory
) {
    EXPERIENCE_SUM(
        {
            val valueExpression = StatisticEvents.experienceAmount.sum().alias("value")
            LeaderboardQueryFactory(
                StatisticEvents
                    .select(
                        StatisticEvents.playerId,
                        valueExpression
                    ),
                valueExpression
            )
        }
    ),

    EVENT_COUNT(
        {
            val valueExpression = StatisticEvents.playerId.count().alias("value")
            LeaderboardQueryFactory(
                StatisticEvents
                    .select(
                        StatisticEvents.playerId,
                        valueExpression
                    ),
                valueExpression
            )
        }
    )
}
