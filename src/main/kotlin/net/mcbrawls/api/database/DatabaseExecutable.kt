package net.mcbrawls.api.database

import net.mcbrawls.api.database.PreparedStatementBuilder.Companion.createBuilder
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement

/**
 * Represents any object that can execute a database operation.
 */
interface DatabaseExecutable {
    /**
     * Provides an asynchronous function to perform an action on the
     * connection of the database.
     *
     * @return the deferred result of the action
     */
    suspend fun <T> execute(action: (Connection) -> T): T

    /**
     * Provides factories to create and build a prepared statement and
     * executes the result on the connection of the database.
     *
     * @return the deferred result of the execution of the prepared statement
     */
    suspend fun <T> executePrepared(
        /**
         * A factory that creates a prepared statement from the database connecton.
         */
        statementFactory: Connection.() -> PreparedStatement,

        /**
         * A factory that builds the statement parameters.
         */
        builderFactory: (builder: PreparedStatementBuilder) -> Unit,

        /**
         * The function which executes the statement.
         */
        executor: (statement: PreparedStatement) -> T
    ): T {
        return execute { connection ->
            val statement = statementFactory.invoke(connection)
            statement.closeOnCompletion()
            val builder = statement.createBuilder()
            builderFactory.invoke(builder)
            executor.invoke(statement)
        }
    }

    /**
     * Provides factories to create and build a prepared statement and
     * executes the result on the connection of the database.
     *
     * @return the deferred result of the execution of the prepared statement
     */
    suspend fun executePrepared(
        /**
         * A factory that creates a prepared statement from the database connecton.
         */
        statementFactory: Connection.() -> PreparedStatement,

        /**
         * A factory that builds the statement parameters.
         */
        builderFactory: (builder: PreparedStatementBuilder) -> Unit
    ): Boolean {
        return executePrepared(statementFactory, builderFactory, PreparedStatement::execute)
    }

    suspend fun <T> executeStatement(action: Statement.() -> T): T {
        return execute { connection ->
            val statement = connection.createStatement()
            statement.closeOnCompletion()
            action.invoke(statement)
        }
    }

    /**
     * Provides an asynchronous function to perform an action with a
     * pre-defined SQL statement on the connection of the database.
     *
     * @return the deferred result of the action
     */
    suspend fun <T> execute(sql: String, action: (Statement, String) -> T): T {
        return execute { connection ->
            val statement = connection.createStatement()
            statement.closeOnCompletion()
            action.invoke(statement, sql)
        }
    }

    /**
     * Executes a query on the database.
     * @return a result set
     */
    suspend fun executeQuery(sql: String): ResultSet {
        return execute(sql, Statement::executeQuery)
    }

    /**
     * Executes an update to the database.
     * @return a result of the amount of changes made to the database
     */
    suspend fun executeUpdate(sql: String): Int {
        return execute(sql, Statement::executeUpdate)
    }
}
