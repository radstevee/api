package net.mcbrawls.api.database.schema;

enum class PartnerStatus(val id: String) {
    ACTIVE("active"),
    CANCELLED("cancelled");

    companion object {
        private val BY_ID = entries.associateBy(PartnerStatus::id)

        fun fromId(id: Any) = BY_ID[id.toString()] ?: throw IllegalArgumentException("Unknown partner status: $id")
    }
}
