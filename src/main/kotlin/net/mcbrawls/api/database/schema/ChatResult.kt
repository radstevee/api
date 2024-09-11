package net.mcbrawls.api.database.schema;

enum class ChatResult(val id: String) {
    /**
     * The chat message should be sent successfully.
     */
    SUCCESS("success"),

    /**
     * The chat mode has no audience with the given context.
     */
    INVALID_AUDIENCE("invalid_audience"),

    /**
     * The chat mode was filtered and the user was found as muted.
     */
    MUTED("muted"),

    /**
     * The chat message was filtered and found as inappropriate.
     */
    FILTERED_PROFANITY("filtered_profanity");

    companion object {
        private val BY_ID = entries.associateBy(ChatResult::id)

        fun fromId(id: Any) = BY_ID[id.toString()] ?: throw IllegalArgumentException("Unknown chat result: $id")
    }
}
