package net.mcbrawls.api

import com.google.gson.Gson

class JsonObjectBuilder {
    private val map = mutableMapOf<String, Any>()

    fun int(key: String, value: Int) {
        map[key] = value
    }

    fun string(key: String, value: String) {
        map[key] = value
    }

    fun array(key: String, vararg elements: String) {
        map[key] = elements.toList()
    }

    fun jsonObject(key: String, init: JsonObjectBuilder.() -> Unit) {
        val jsonObjectBuilder = JsonObjectBuilder()
        jsonObjectBuilder.init()
        map[key] = jsonObjectBuilder.build()
    }

    fun toJsonString(): String {
        val gson = Gson()
        return gson.toJson(map)
    }

    fun build(): Map<String, Any> {
        return map
    }
}

fun jsonObject(init: JsonObjectBuilder.() -> Unit): JsonObjectBuilder {
    val jsonObjectBuilder = JsonObjectBuilder()
    jsonObjectBuilder.init()
    return jsonObjectBuilder
}
