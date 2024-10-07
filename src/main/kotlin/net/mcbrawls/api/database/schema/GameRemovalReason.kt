package net.mcbrawls.api.database.schema

/**
 * The reason for which a game instance was removed.
 */
enum class GameRemovalReason(val id: String) {
    /**
     * The game ended naturally.
     */
    ENDED("ended"),

    /**
     * The game instance was removed using a command.
     */
    COMMAND("command"),

    /**
     * The game instance threw an exception in its tick.
     */
    EXCEPTION("exception"),

    /**
     * The game instance was marked as invalid during its lifecycle.
     */
    INVALID("invalid"),

    /**
     * The game instance was inactive for too long.
     */
    INACTIVE("inactive");

    companion object {
        private val BY_ID = entries.associateBy(GameRemovalReason::id)

        fun fromId(id: Any) = BY_ID[id.toString()] ?: throw IllegalArgumentException("Unknown removal reason: $id")
    }
}
