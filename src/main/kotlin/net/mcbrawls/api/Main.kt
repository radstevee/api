package net.mcbrawls.api

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import io.ktor.http.HttpStatusCode
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
    logger.info("Connecting to database")
    runBlocking { DatabaseController.connect() }
    logger.info("Connected.")

    logger.info("Starting server")
    embeddedServer(Netty, port = 8080) {
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
            authenticate("auth-basic") {
                get("/chat_statistics") {
                    call.respondText {
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

                        // compile json
                        jsonObject {
                            jsonObject("message_counts") {
                                int("local", localMessageCount)
                                int("filtered", filteredMessageCount)
                            }
                        }.toJsonString()
                    }
                }

                get("/chat_statistics/{uuid}") {
                    val uuidString = call.parameters["uuid"]

                    val uuid = try {
                        UUID.fromString(uuidString)
                    } catch (exception: IllegalArgumentException) {
                        call.respond(HttpStatusCode.BadRequest, "Not a valid uuid: $uuidString")
                        return@get
                    }

                    call.respondText {
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

                        // compile json
                        jsonObject {
                            jsonObject("message_counts") {
                                int("local", localMessageCount)
                                int("filtered", filteredMessageCount)
                            }
                        }.toJsonString()
                    }
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

fun JsonElement.toJsonString(prettyPrint: Boolean): String {
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
