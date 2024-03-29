package net.mcbrawls.api

import com.mojang.serialization.Codec
import java.util.UUID as UID

object CustomCodecs {
    val UUID = Codec.STRING.xmap(UID::fromString, UID::toString)
}
