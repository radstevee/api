package net.mcbrawls.api.database.schema

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.json.json
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

private const val PLAYER_ID_KEY = "player_id"
private const val UUID_VARCHAR_LENGTH = 36

val jsonConfig = Json {
    ignoreUnknownKeys = true
}

enum class ChatMode(val id: String) {
    LOCAL("local"),
    PARTY("party"),
    TEAM("team"),
    MESSAGE("message"),
    STAFF("staff"),
    PARTNER("partner");

    companion object {
        private val BY_ID = entries.associateBy(ChatMode::id)

        fun fromId(id: Any) = BY_ID[id.toString()] ?: throw IllegalArgumentException("Unknown chat mode: $id")
    }
}

enum class ChatResult(val id: String) {
    SUCCESS("success"),
    INVALID_AUDIENCE("invalid_audiencec"),
    MUTED("muted"),
    FILTERED_PROFANITY("filtered_profanity");

    companion object {
        private val BY_ID = entries.associateBy(ChatResult::id)

        fun fromId(id: Any) = BY_ID[id.toString()] ?: throw IllegalArgumentException("Unknown chat result: $id")
    }
}

object ApiKeys : Table("ApiKeys") {
    val playerId = varchar(PLAYER_ID_KEY, UUID_VARCHAR_LENGTH)
    val apiKey = varchar("api_key", 32)
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

object ChatLogs : Table("ChatLogs") {
    val logId = integer("log_id").autoIncrement()
    val playerId = varchar(PLAYER_ID_KEY, UUID_VARCHAR_LENGTH)
    val worldId = varchar("world_id", 255)
    val contemporaryUsername = varchar("contemporary_username", 16)
    val recipients = json<JsonArray>("recipients", jsonConfig)
    val chatMode = customEnumeration("chat_mode", toDb = ChatMode::id, fromDb = ChatMode::fromId)
    val chatResult = customEnumeration("chat_result", toDb = ChatResult::id, fromDb = ChatResult::fromId)
    val gameInstanceUuid = varchar("game_instance_uuid", UUID_VARCHAR_LENGTH)
    val partyLeaderUuid = varchar("party_leader_uuid", UUID_VARCHAR_LENGTH)
    val timestamp = timestamp("timestamp")
}

object LuckPermsPlayers : Table("luckperms_players") {
    val uuid = varchar("uuid", UUID_VARCHAR_LENGTH)
    val username = varchar("username", 16)
    val primaryGroup = varchar("primary_group", 36)
}
