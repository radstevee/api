package net.mcbrawls.api.response

import kotlinx.serialization.Serializable

/**
 * A response for message counts.
 */
@Serializable
data class MessageCountResponse(
    /**
     * The amount of unfiltered, local messages sent.
     */
    val local: Int,

    /**
     * The amount of messages filtered from any chat mode.
     */
    val filtered: Int
)