package net.mcbrawls.api

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.UUID

object UUIDSerializer : KSerializer<UUID> {
    override val descriptor = PrimitiveSerialDescriptor("UniqueId", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder) = UUID.fromString(decoder.decodeString())

    override fun serialize(encoder: Encoder, value: UUID) = encoder.encodeString(value.toString())
}

typealias SerializableUUID = @Serializable(with = UUIDSerializer::class) UUID

object RankSerializer : KSerializer<Rank> {
    override val descriptor = PrimitiveSerialDescriptor("Rank", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder) = Rank.valueOf(decoder.decodeString().uppercase())

    override fun serialize(encoder: Encoder, value: Rank) = encoder.encodeString(value.name.lowercase())
}
