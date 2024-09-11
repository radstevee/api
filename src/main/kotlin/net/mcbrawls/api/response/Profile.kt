package net.mcbrawls.api.response

import kotlinx.serialization.Serializable
import net.mcbrawls.api.Rank
import net.mcbrawls.api.UID

/**
 * A response for profile-related queries.
 */
@Serializable
data class Profile(
    /**
     * The UUID of the associated player.
     */
    val uuid: UID,

    /**
     * The rank of the associated player.
     */
    val rank: Rank,

    /**
     * The amount of experience for the associated player.
     */
    val experience: Int
)
