package net.mcbrawls.api.database

import com.google.gson.JsonElement
import java.sql.PreparedStatement
import java.util.UUID

/**
 * Builds a prepared statement incrementally.
 */
data class PreparedStatementBuilder(
    /**
     * The statememnt to build.
     */
    private val statement: PreparedStatement
) {
    /**
     * The rolling parameter index.
     */
    private var parameterIndex = 1

    /**
     * Adds a new parameter with the associated index.
     */
    fun <T> addNext(obj: T, setter: (PreparedStatement, Int, T) -> Unit) {
        setter.invoke(statement, parameterIndex, obj)
        parameterIndex++
    }

    /**
     * Adds a new parameter with the associated index allowing for null values.
     */
    fun <T> addNextNullable(objType: Int, obj: T?, setter: PreparedStatement.(Int, T) -> Unit) {
        statement.setNullable(parameterIndex, objType, obj, setter)
        parameterIndex++
    }

    companion object {
        /**
         * Creates a prepared statement builder for this prepared statement.
         */
        fun PreparedStatement.createBuilder() = PreparedStatementBuilder(this)

        /**
         * Sets the value of the designated parameter to the given uuid.
         */
        fun PreparedStatement.setUuid(parameterIndex: Int, uuid: UUID) {
            setString(parameterIndex, uuid.toString())
        }

        fun setStatementUuid(statement: PreparedStatement, parameterIndex: Int, uuid: UUID) {
            statement.setUuid(parameterIndex, uuid)
        }

        /**
         * Sets the value of the designated parameter to the given JSON element.
         */
        fun PreparedStatement.setJson(parameterIndex: Int, json: JsonElement) {
            setString(parameterIndex, json.toString())
        }

        fun setStatementJson(statement: PreparedStatement, parameterIndex: Int, json: JsonElement) {
            statement.setJson(parameterIndex, json)
        }

        /**
         * Sets the value of the designated parameter to the given object or the object type's null equivalent if null.
         * @param objType See [java.sql.Types]
         */
        fun <T> PreparedStatement.setNullable(
            parameterIndex: Int,
            objType: Int,
            obj: T?,
            setter: (PreparedStatement, Int, T) -> Unit
        ) {
            if (obj == null) {
                setNull(parameterIndex, objType)
            } else {
                setter.invoke(this, parameterIndex, obj)
            }
        }
    }
}
