package net.mcbrawls.api.database

import org.jetbrains.exposed.sql.Database

/**
 * Manages the remote Brawls MySQL database.
 */
object BrawlsDatabaseFactory {
    /**
     * Loads the configuration from disk.
     */
    fun createDatabase(schema: String): Database {
        val url = ConnectionConfig.url
        return Database.connect(
            url = "jdbc:mysql://$url/$schema",
            user = ConnectionConfig.username,
            password = ConnectionConfig.password,
        )
    }

    private object ConnectionConfig {
        val url = env("URL")
        val username = env("USERNAME")
        val password = env("PASSWORD")

        private fun env(name: String): String {
            val fullName = "BRAWLS_DB_$name"
            return System.getenv(fullName) ?: throw IllegalArgumentException("Environment variable not found: $fullName")
        }
    }
}
