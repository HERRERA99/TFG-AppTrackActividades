package com.aitor.trackactividades.core.network

import com.google.gson.*
import java.lang.reflect.Type
import java.time.OffsetDateTime

class OffsetDateTimeAdapter : JsonSerializer<OffsetDateTime>, JsonDeserializer<OffsetDateTime> {
    override fun serialize(
        src: OffsetDateTime?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(src?.toString()) // ISO-8601
    }

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): OffsetDateTime {
        return OffsetDateTime.parse(json?.asString)
    }
}

