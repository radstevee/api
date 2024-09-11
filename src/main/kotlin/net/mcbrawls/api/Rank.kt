package net.mcbrawls.api

import kotlinx.serialization.Serializable

/**
 * Ranks on the MC Brawls Minecraft server.
 */
@Serializable(with = RankSerializer::class)
enum class Rank(private val rankId: String) {
    ADMIN("admin"),
    BUILDER("builder"),
    MODERATOR("moderator"),
    MCCIT("mccit"),
    PARTNER("partner"),
    STAFF("staff"),
    DEFAULT("default");

    companion object {
        private val BY_ID = entries.associateBy(Rank::rankId)

        fun fromId(id: String): Rank? = BY_ID[id]
    }
}
