package net.mcbrawls.api.response

import kotlinx.serialization.Serializable
import net.mcbrawls.api.UID

/**
 * A response for leaderboard-related queries.
 */
@Serializable
data class LeaderboardResponse(
    /**
     * The uuid of the associated player.
     */
    val uuid: UID,

    /**
     * The position of the associated player on the leaderboard.
     */
    val position: Int,

    /**
     * The value on the leaderboard.
     */
    val value: Int
)
