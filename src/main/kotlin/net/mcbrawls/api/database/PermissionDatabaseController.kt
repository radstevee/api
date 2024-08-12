package net.mcbrawls.api.database

import kotlinx.serialization.json.Json
import net.mcbrawls.api.file
import net.mcbrawls.api.runAsync
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.sql.Connection
import kotlin.concurrent.thread

/**
 * Manages the remote LuckPerms MySQL database.
 */
object PermissionDatabaseController : DatabaseExecutable {
    private val logger: Logger = LoggerFactory.getLogger("Permissions Database Controller")

    private const val DATABASE_CONFIG_PATH = "database_config.json"

    var database: AuthenticatableDatabase = loadConfiguration()

    init {
        runAsync { connect() }

        thread(name = "Permissions Database Heartbeat") {
            // start heartbeat
            runAsync {
                while (true) {
                    Thread.sleep(5000)
                    connectionHeartbeat()
                }
            }
        }
    }

    /**
     * Verifies that the connection is valid after a 5 second timeout.
     */
    private suspend fun connectionHeartbeat() {
        // check 1 second timeout
        if (!database.isConnectionValid(3)) {
            logger.warn("No permissions database connection found. Attempting to connect to the Brawls permissions database.")

            // try reconnect
            try {
                database.connect()
                logger.info("Connected to Brawls permissions database.")
            } catch (exception: Exception) {
                logger.error("Something went wrong connecting to the permissions database", exception)
            }
        }
    }

    /**
     * Loads the configuration from disk.
     */
    fun loadConfiguration(): AuthenticatableDatabase {
        val file = file(DATABASE_CONFIG_PATH)
        try {
            val json = file.readText()
            val connectionInfo = Json.decodeFromString<DatabaseController.ConnectionInfo>(json)
            return AuthenticatableDatabase(
                connectionInfo.address,
                connectionInfo.permissionDatabase,
                "mysql",
                connectionInfo.user,
                connectionInfo.password
            ).also { database = it }
        } catch (exception: Exception) {
            logger.error("Could not load permissions database information", exception)
            throw exception
        }
    }

    override suspend fun <T> execute(action: (Connection) -> T): T {
        return database.execute(action) ?: throw NullPointerException("Permissions database was null: $this")
    }

    /**
     * Connects the database.
     */
    suspend fun connect() {
        database.connect()
    }

    /**
     * Disconnects the database.
     */
    fun disconnect(): Boolean? {
        return database.disconnect()
    }
}
