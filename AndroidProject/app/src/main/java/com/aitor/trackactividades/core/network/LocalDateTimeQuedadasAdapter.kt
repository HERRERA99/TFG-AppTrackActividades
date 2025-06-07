package com.aitor.trackactividades.core.network

import com.google.gson.JsonArray
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LocalDateTimeQuedadasAdapter : JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
    override fun serialize(src: LocalDateTime?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonArray().apply {
            src?.let {
                add(it.year)
                add(it.monthValue)
                add(it.dayOfMonth)
                add(it.hour)
                add(it.minute)
            }
        }
    }

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): LocalDateTime {
        val array = json?.asJsonArray
        return LocalDateTime.of(
            array?.get(0)?.asInt ?: 0,
            array?.get(1)?.asInt ?: 1,
            array?.get(2)?.asInt ?: 1,
            array?.get(3)?.asInt ?: 0,
            array?.get(4)?.asInt ?: 0
        )
    }
}