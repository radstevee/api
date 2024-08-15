package net.mcbrawls.api.registry

import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import com.mojang.serialization.Codec
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer
import kotlin.random.Random

/**
 * A basic string-object registry.
 */
open class BasicRegistry<T : Any>(
    /**
     * Whether this registry can be modified.
     */
    private val modifiable: Boolean = false
) {
    private val entries = mutableListOf<T>()
    private val keys = mutableListOf<String>()
    private val keyToEntryMap = mutableMapOf<String, T>()
    private val entryToKeyMap = mutableMapOf<T, String>()

    /**
     * The amount of registered entries in this registry.
     */
    val size: Int get() = entries.size

    /**
     * The default value of this registry.
     */
    open val defaultValue: T? = null

    /**
     * The codec for this registry.
     */
    val codec: Codec<T> = Codec.STRING.xmap(::get, ::get)

    /**
     * Registers [entry] to the registry under [key].
     * @return the passed [entry]
     */
    fun register(key: String, entry: T): T {
        if (keys.contains(key)) {
            if (modifiable) {
                // remove old entry object
                val oldEntry = this[key]
                entries.remove(oldEntry)
                entryToKeyMap.remove(oldEntry)
            } else {
                throw UnsupportedOperationException("Key $key already registered")
            }
        }

        entries.add(entry)
        keys.add(key)

        keyToEntryMap[key] = entry
        entryToKeyMap[entry] = key

        return entry
    }

    /**
     * @return the value that is assigned [key], or `null` if it is not registered
     */
    open operator fun get(key: String): T? {
        return keyToEntryMap[key]
    }

    /**
     * @return the key assigned to [entry], or `null` if it is not registered
     */
    operator fun get(entry: T): String? {
        return entryToKeyMap[entry]
    }

    /**
     * @return the value that is assigned to the index [index], or null if one is not present
     */
    operator fun get(index: Int): T? {
        return entries.getOrNull(index)
    }

    /**
     * @return the index of the entry
     */
    fun indexOf(entry: T): Int {
        return entries.indexOf(entry)
    }

    /**
     * Suggests all keys in this registry to [builder].
     */
    fun suggestKeys(builder: SuggestionsBuilder, filter: ((Map.Entry<String, T>) -> Boolean)? = null): CompletableFuture<Suggestions> {
        val filtered = if (filter != null) keyToEntryMap.filter(filter).keys else keys
        filtered.forEach(builder::suggest)
        return builder.buildFuture()
    }

    /**
     * Performs [action] on every registered entry.
     */
    fun forEach(action: Consumer<T>) {
        entries.forEach(action)
    }

    /**
     * Filters the entries list to the given [predicate].
     * @return a new list
     */
    fun firstOrNull(predicate: (T) -> Boolean): T? {
        return entries.firstOrNull(predicate)
    }

    /**
     * Returns a random entry of the registry.
     */
    fun random(random: Random = Random, filter: ((T) -> Boolean)? = null): T? {
        return (if (filter != null) entries.filter(filter) else entries).randomOrNull(random)
    }
}
