package net.mcbrawls.api

import kotlinx.serialization.Serializable

/**
 * Ranks on the MC Brawls Minecraft server.
 */
@Serializable(with = RankSerializer::class)
enum class Rank {
    ADMIN,
    BUILDER,
    MODERATOR,
    MCCIT,
    PARTNER,
    STAFF,
    DEFAULT;
}