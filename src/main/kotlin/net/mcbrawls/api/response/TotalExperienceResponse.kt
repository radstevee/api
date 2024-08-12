package net.mcbrawls.api.response

import kotlinx.serialization.Serializable
import net.mcbrawls.api.UID

/**
 * A response for total experience counts.
 */
@Serializable
data class TotalExperienceResponse(
    /**
     * The uuid of the associated player.
     */
    val uuid: UID,

    /**
     * The total experience earned (lifetime).
     */
    val totalExperience: Int
)
