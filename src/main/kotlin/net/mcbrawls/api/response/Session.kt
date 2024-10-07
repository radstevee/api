package net.mcbrawls.api.response

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import net.mcbrawls.api.SerializableUUID

@Serializable
data class Session(
    val uuid: SerializableUUID,
    val start: Instant,
    val end: Instant,
    val gamesPlayed: Long
)
