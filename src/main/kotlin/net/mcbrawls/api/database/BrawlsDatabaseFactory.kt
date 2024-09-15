package net.mcbrawls.api.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
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
        val config = HikariConfig().apply {
            jdbcUrl = "jdbc:mysql://$url/$schema"
            driverClassName = "com.mysql.cj.jdbc.Driver"
            username = ConnectionConfig.username
            password = ConnectionConfig.password
            maximumPoolSize = 10
        }

        return Database.connect(HikariDataSource(config))
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
