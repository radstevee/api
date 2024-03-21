package net.mcbrawls.api

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder

data class DatabaseConfiguration(
    val serverAddress: String,
    val serverPort: Int,
    val database: String,
    val username: String,
    val password: String
) {
    companion object {
        val CODEC: Codec<DatabaseConfiguration> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.STRING.fieldOf("address").forGetter(DatabaseConfiguration::serverAddress),
                Codec.INT.fieldOf("port").forGetter(DatabaseConfiguration::serverPort),
                Codec.STRING.fieldOf("database").forGetter(DatabaseConfiguration::database),
                Codec.STRING.fieldOf("username").forGetter(DatabaseConfiguration::username),
                Codec.STRING.fieldOf("password").forGetter(DatabaseConfiguration::password)
            ).apply(instance, ::DatabaseConfiguration)
        }
    }
}
