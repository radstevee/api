package net.mcbrawls.api.database

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

/**
 * A database value that does not need to be fetched every time it is required.
 * This object tracks changes to the database value as changes to the database are made.
 */
@Suppress("DeferredResultUnused")
class CachedDatabaseValue<T>(
    /**
     * The default value.
     */
    private val defaultValue: T,

    /**
     * The function to select the true value from the database.
     */
    private val databaseSelectionFunction: DatabaseSelectionFunction<T>
) {
    init {
        GlobalScope.async {
            // refresh value from database
            refreshValue()
        }
    }

    @field:Volatile
    private var currentValue: T = defaultValue

    /**
     * The value of this instance.
     */
    val value: T get() = currentValue

    /**
     * Modifies the value locally and on the database according to the function provided.
     * @return the new local value
     */
    suspend fun modifyValue(function: CalculatedDatabaseModifyFunction<T>): T {
        return function.modify(currentValue).also { currentValue = it }
    }

    /**
     * Refreshes the current value from the database.
     */
    suspend fun refreshValue() {
        val value = databaseSelectionFunction.select(defaultValue)
        currentValue = value
    }

    fun interface CalculatedDatabaseModifyFunction<T> {
        /**
         * Calculates the change to the database and makes the database change.
         * @return the new cached value
         */
        suspend fun modify(currentValue: T): T
    }

    fun interface DatabaseSelectionFunction<T> {
        /**
         * Selects the value from the database.
         * @return the deferred result
         */
        suspend fun select(defaultValue: T): T
    }
}
