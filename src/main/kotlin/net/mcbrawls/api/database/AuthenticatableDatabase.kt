package net.mcbrawls.api.database

import java.sql.Connection
import java.sql.DriverManager

/**
 * A database that can be connected to, optionally taking a username and a password.
 */
class AuthenticatableDatabase(
    /**
     * The url address of the database.
     */
    private val address: String,

    /**
     * The name of the database within the given connection information.
     */
    private val databaseName: String,

    /**
     * The database driver managing this connection.
     */
    private val driver: String,

    /**
     * The username to authenticate the database.
     */
    private val user: String? = null,

    /**
     * The password to authenticate the database.
     */
    private val password: String? = null
) : Database() {
    override suspend fun createConnection(): Connection {
        return DriverManager.getConnection("jdbc:$driver://$address/$databaseName", user, password)
    }
}
