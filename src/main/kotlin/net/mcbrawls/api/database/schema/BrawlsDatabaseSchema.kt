@file:Suppress("unused")

package net.mcbrawls.api.database.schema

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.json.json
import org.jetbrains.exposed.sql.kotlin.datetime.date
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

private const val PLAYER_ID_KEY = "player_id"
private const val UUID_VARCHAR_LENGTH = 36

val jsonConfig = Json {
    ignoreUnknownKeys = true
}

enum class DbChatMode(val id: String) {
    LOCAL("local"),
    PARTY("party"),
    TEAM("team"),
    MESSAGE("message"),
    STAFF("staff"),
    PARTNER("partner");

    companion object {
        private val BY_ID = entries.associateBy(DbChatMode::id)

        fun fromId(id: Any) = BY_ID[id.toString()] ?: throw IllegalArgumentException("Unknown chat mode: $id")
    }
}

enum class DbChatResult(val id: String) {
    SUCCESS("success"),
    INVALID_AUDIENCE("invalid_audiencec"),
    MUTED("muted"),
    FILTERED_PROFANITY("filtered_profanity");

    companion object {
        private val BY_ID = entries.associateBy(DbChatResult::id)

        fun fromId(id: Any) = BY_ID[id.toString()] ?: throw IllegalArgumentException("Unknown chat result: $id")
    }
}

enum class DbPartnerStatus(val id: String) {
    ACTIVE("active"),
    CANCELLED("cancelled");

    companion object {
        private val BY_ID = entries.associateBy(DbPartnerStatus::id)

        fun fromId(id: Any) = BY_ID[id.toString()] ?: throw IllegalArgumentException("Unknown partner status: $id")
    }
}

object ApiKeys : Table("ApiKeys") {
    val playerId = varchar(PLAYER_ID_KEY, UUID_VARCHAR_LENGTH)
    val apiKey = varchar("api_key", 32)
}

object ChatLogs : Table("ChatLogs") {
    val logId = integer("log_id").autoIncrement()
    val playerId = varchar(PLAYER_ID_KEY, UUID_VARCHAR_LENGTH)
    val worldId = varchar("world_id", 255)
    val contemporaryUsername = varchar("contemporary_username", 16)
    val recipients = json<JsonArray>("recipients", jsonConfig)
    val chatMode = customEnumeration("chat_mode", toDb = DbChatMode::id, fromDb = DbChatMode::fromId)
    val chatResult = customEnumeration("chat_result", toDb = DbChatResult::id, fromDb = DbChatResult::fromId)
    val gameInstanceUuid = varchar("game_instance_uuid", UUID_VARCHAR_LENGTH)
    val partyLeaderUuid = varchar("party_leader_uuid", UUID_VARCHAR_LENGTH)
    val timestamp = timestamp("timestamp")
}

object Friends : Table("Friends") {
    val relationId = integer("relation_id").autoIncrement()
    val initiator = varchar("initiator", UUID_VARCHAR_LENGTH)
    val recipient = varchar("recipient", UUID_VARCHAR_LENGTH)
    val since = timestamp("since")
    val mutual = bool("mutual")
}

object GameInstances : Table("GameInstances") {
    val instanceId = integer("instance_id").autoIncrement()
    val uuid = varchar("uuid", UUID_VARCHAR_LENGTH)
    val participants = json<JsonArray>("participants", jsonConfig)
    val additionalData = json<JsonArray>("additional_data", jsonConfig)
}

object IgnoredPlayers : Table("IgnoredPlayers") {
    val receiver = varchar("receiver", UUID_VARCHAR_LENGTH)
    val target = varchar("target", UUID_VARCHAR_LENGTH)
}

object Medals : Table("Medals") {
    val id = integer("id").autoIncrement()
    val playerId = varchar(PLAYER_ID_KEY, UUID_VARCHAR_LENGTH)
    val medalId = varchar("medal_id", 100)
    val timestamp = timestamp("timestamp")
}

object Partnerships : Table("Partnerships") {
    val partnerId = integer("partner_id").autoIncrement()
    val partnerName = text("partner_name")
    val status = customEnumeration("status", toDb = DbPartnerStatus::id, fromDb = DbPartnerStatus::fromId)
    val createdDate = date("created_date")
    val discordId = varchar("discord_id", 19)
    val partneredUsers = text("partnered_users")
    val partneredUserIds = text("partnered_user_ids")
    val partneredPlayerIds = text("partnered_player_ids")
    val notes = text("notes")
}

object PlayerCosmetics : Table("PlayerCosmetics") {
    val playerId = integer(PLAYER_ID_KEY).autoIncrement()
    val title = varchar("title", 100)
}

object PlayerReports : Table("PlayerReports") {
    val reportId = integer("report_id").autoIncrement()
    val reporterId = varchar("reporter_id", UUID_VARCHAR_LENGTH)
    val offenderId = varchar("offender_id", UUID_VARCHAR_LENGTH)
    val reason = text("reason")
    val timestamp = timestamp("timestamp")
    val resolved = bool("resolved")
}

object Players : Table("Players") {
    val playerId = varchar(PLAYER_ID_KEY, UUID_VARCHAR_LENGTH)
    val firstJoin = timestamp("first_join")
    val lastJoin = timestamp("last_join")
}

object PlayerSettings : Table("PlayerSettings") {
    val playerId = varchar(PLAYER_ID_KEY, UUID_VARCHAR_LENGTH)
    val settingKey = varchar("setting_key", 50)
    val settingValue = varchar("setting_value", 50)
}

object Punishments : Table("Punishments") {
    val punishmentId = integer("punishment_id").autoIncrement()
    val playerId = varchar(PLAYER_ID_KEY, UUID_VARCHAR_LENGTH)
    val contemporaryName = varchar("contemporary_name", 16)
    val officerId = varchar("officer_id", UUID_VARCHAR_LENGTH)
    val punishmentType = customEnumeration("punishment_type", toDb = PunishmentType::id, fromDb = PunishmentType::fromId)
    val reason = text("reason")
    val playerMadeAware = bool("player_made_aware")
    val acknowledged = bool("acknowledged")
    val timestamp = timestamp("timestamp")
    val duration = long("duration")
}

object Purchases : Table("Purchases") {
    val purchaseId = integer("purchase_id").autoIncrement()
    val transactionId = varchar("transaction_id", 40)
    val playerId = varchar(PLAYER_ID_KEY, UUID_VARCHAR_LENGTH)
    val packageId = integer("package_id")
    val packageExpiry = integer("package_expiry")
    val packageName = text("package_name")
    val timestamp = timestamp("timestamp")
}

object StatisticEvents : Table("StatisticEvents") {
    val eventId = integer("event_id").autoIncrement()
    val playerId = varchar(PLAYER_ID_KEY, UUID_VARCHAR_LENGTH)
    val causeId = text("cause_id")
    val gameType = varchar("game_type", 100)
    val gameUuid = varchar("game_uuid", UUID_VARCHAR_LENGTH)
    val experienceAmount = integer("experience_amount")
    val timestamp = timestamp("timestamp")
}

object LuckPermsPlayers : Table("luckperms_players") {
    val uuid = varchar("uuid", UUID_VARCHAR_LENGTH)
    val username = varchar("username", 16)
    val primaryGroup = varchar("primary_group", 36)
}
