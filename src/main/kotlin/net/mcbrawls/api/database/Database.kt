package net.mcbrawls.api.database

import java.sql.Connection

/**
 * A hosted database.
 */
abstract class Database : DatabaseExecutable {
    /**
     * The current connection of the database.
     */
    private var activeConnection: Connection? = null

    /**
     * Creates a connection to the database.
     * @return an sql connection
     */
    abstract suspend fun createConnection(): Connection

    /**
     * Connects/reconnects to the database.
     * Throws exceptions according to how [createConnection] is implemented.
     */
    suspend fun connect() {
        // clean up old connection
        val oldConnection = activeConnection
        if (oldConnection != null && !oldConnection.isClosed) {
            disconnect()
        }

        // create new connection
        activeConnection = createConnection()
    }

    /**
     * Disconnects the active connection to the database.
     * @return whether the connection was closed, or null if no connection was present
     */
    fun disconnect(): Boolean? {
        val connection = activeConnection ?: return null
        activeConnection = null

        if (connection.isClosed) {
            return false
        }

        connection.close()

        return true
    }

    override suspend fun <T> execute(action: (Connection) -> T): T {
        val connection = activeConnection
        if (connection != null) {
            return action.invoke(connection)
        }

        // throw exception
        throw IllegalStateException("Connection not present")
    }

    /**
     * Whether the connection is valid.
     */
    fun isConnectionValid(timeout: Int): Boolean {
        return activeConnection?.isValid(timeout) ?: false
    }
}
