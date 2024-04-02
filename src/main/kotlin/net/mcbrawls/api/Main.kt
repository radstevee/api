package net.mcbrawls.api

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.mojang.serialization.JsonOps
import dev.andante.codex.encodeQuick
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.basic
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import net.mcbrawls.api.database.CachedDatabaseValue
import net.mcbrawls.api.database.DatabaseController
import net.mcbrawls.api.database.PreparedStatementBuilder
import net.mcbrawls.api.response.MessageCountResponse
import net.mcbrawls.api.response.TotalExperienceResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Path
import java.sql.PreparedStatement
import java.util.UUID

private val logger: Logger = LoggerFactory.getLogger("Main")

/**
 * TODO cache fetched data ([CachedDatabaseValue])
 */

fun main() {
    logger.info("Connecting to database.")
    runBlocking { DatabaseController.connect() }
    logger.info("Connected.")

    logger.info("Starting server")
    embeddedServer(Netty, port = 37612) {
        install(Authentication) {
            // authentication
            basic("auth-basic") {
                realm = "Access to the '/' path"
                validate { credentials ->
                    val id = credentials.name
                    val apiKey = credentials.password
                    val result = DatabaseController.executePrepared(
                        { prepareStatement("SELECT * FROM ApiKeys WHERE player_id = ? AND api_key = ?") },
                        { builder ->
                            builder.addNext(id, PreparedStatement::setString)
                            builder.addNext(apiKey, PreparedStatement::setString)
                        },
                        PreparedStatement::executeQuery
                    )

                    if (result.next()) {
                        UserIdPrincipal(id)
                    } else {
                        null
                    }
                }
            }
        }

        // routes
        routing {
            get("/") {
                call.respond(HttpStatusCode.OK, "MC Brawls API https://api.mcbrawls.net")
            }

            authenticate("auth-basic") {
                get("/experience") {
                    // execute query
                    val result = DatabaseController.executeStatement {
                        executeQuery(
                            """
                                SELECT player_id, SUM(experience_amount) AS total_experience
                                FROM StatisticEvents
                                GROUP BY player_id
                                ORDER BY total_experience DESC
                            """.trimIndent()
                        )
                    }

                    // obtain results
                    val uuidToExperienceMap: Map<UUID, Int> = buildMap {
                        while (result.next()) {
                            try {
                                val playerId = UUID.fromString(result.getString("player_id"))
                                val totalExperience = result.getInt("total_experience")
                                this[playerId] = totalExperience
                            } catch (_: Exception) {
                            }
                        }
                    }

                    // compile
                    val responses = uuidToExperienceMap.map { (id, xp) -> TotalExperienceResponse(id, xp) }
                    val json = TotalExperienceResponse.CODEC.listOf().encodeQuick(JsonOps.INSTANCE, responses)

                    if (json == null) {
                        call.respond(HttpStatusCode.InternalServerError, "An exception occured on the server")
                        return@get
                    }

                    // respond
                    call.respondJson(json)
                }

                get("/experience/{uuid}") {
                    val uuidString = call.parameters["uuid"]

                    val uuid = try {
                        UUID.fromString(uuidString)
                    } catch (exception: IllegalArgumentException) {
                        call.respond(HttpStatusCode.BadRequest, "Not a valid uuid: $uuidString")
                        return@get
                    }

                    // execute query
                    val result = DatabaseController.executePrepared(
                        {
                            prepareStatement(
                                """
                                SELECT SUM(experience_amount) AS total_experience
                                FROM StatisticEvents
                                WHERE player_id = ?
                            """.trimIndent()
                            )
                        },
                        { builder ->
                            builder.addNext(uuid, PreparedStatementBuilder::setStatementUuid)
                        },
                        PreparedStatement::executeQuery
                    )

                    // obtain results
                    val experience = if (result.next()) {
                        try {
                            result.getInt("total_experience")
                        } catch (exception: Exception) {
                            call.respond(HttpStatusCode.InternalServerError, "An exception occured on the server")
                            return@get
                        }
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "Player not found: $uuid")
                        return@get
                    }

                    // compile
                    val response = TotalExperienceResponse(uuid, experience)
                    val json = TotalExperienceResponse.CODEC.encodeQuick(JsonOps.INSTANCE, response)

                    if (json == null) {
                        call.respond(HttpStatusCode.InternalServerError, "An exception occured on the server")
                        return@get
                    }

                    // respond
                    call.respondJson(json)
                }

                get("/chat_statistics") {
                    // execute query
                    val result = DatabaseController.executeStatement {
                        executeQuery(
                            """
                                SELECT
                                    SUM(chat_mode = 'local' AND chat_result = 'success') AS local_message_count,
                                    SUM(chat_result = 'filtered_profanity') AS filtered_message_count
                                FROM ChatLogs
                            """.trimIndent()
                        )
                    }

                    // obtain results
                    result.next()
                    val localMessageCount = result.getInt("local_message_count")
                    val filteredMessageCount = result.getInt("filtered_message_count")

                    // compile
                    val response = MessageCountResponse(localMessageCount, filteredMessageCount)
                    val json = MessageCountResponse.CODEC.encodeQuick(JsonOps.INSTANCE, response)

                    if (json == null) {
                        call.respond(HttpStatusCode.InternalServerError, "An exception occured on the server")
                        return@get
                    }

                    // respond
                    call.respondJson(json)
                }

                get("/chat_statistics/{uuid}") {
                    val uuidString = call.parameters["uuid"]

                    val uuid = try {
                        UUID.fromString(uuidString)
                    } catch (exception: IllegalArgumentException) {
                        call.respond(HttpStatusCode.BadRequest, "Not a valid uuid: $uuidString")
                        return@get
                    }

                    // execute query
                    val result = DatabaseController.executePrepared(
                        {
                            prepareStatement(
                                """
                                    SELECT
                                        SUM(player_id = ? AND chat_mode = 'local' AND chat_result = 'success') AS local_message_count,
                                        SUM(player_id = ? AND chat_result = 'filtered_profanity') AS filtered_message_count
                                    FROM ChatLogs
                                """.trimIndent()
                            )
                        },
                        { builder ->
                            builder.addNext(uuid, PreparedStatementBuilder::setStatementUuid)
                            builder.addNext(uuid, PreparedStatementBuilder::setStatementUuid)
                        },
                        PreparedStatement::executeQuery
                    )

                    // obtain results
                    result.next()
                    val localMessageCount = result.getInt("local_message_count")
                    val filteredMessageCount = result.getInt("filtered_message_count")

                    // compile
                    val response = MessageCountResponse(localMessageCount, filteredMessageCount)
                    val json = MessageCountResponse.CODEC.encodeQuick(JsonOps.INSTANCE, response)

                    if (json == null) {
                        call.respond(HttpStatusCode.InternalServerError, "An exception occured on the server")
                        return@get
                    }

                    // respond
                    call.respondJson(json)
                }
            }
        }
    }.start(wait = true)
}

@Suppress("DeferredResultUnused")
inline fun runAsync(crossinline block: suspend CoroutineScope.() -> Unit) {
    GlobalScope.async { block.invoke(this) }
}

/**
 * Retrieves a file from the run directory.
 */
fun file(path: String): File {
    return Path.of(path).toFile()
}

suspend fun ApplicationCall.respondJson(element: JsonElement) {
    respondText(element.toJsonString())
}

fun JsonElement.toJsonString(prettyPrint: Boolean = false): String {
    val gson = GsonBuilder()
    if (prettyPrint) {
        gson.setPrettyPrinting()
    }
    return gson.create().toJson(this)
}

fun File.toJson(): JsonElement? {
    if (exists() && isFile) {
        return reader().use(JsonParser::parseReader)
    }

    return null
}
