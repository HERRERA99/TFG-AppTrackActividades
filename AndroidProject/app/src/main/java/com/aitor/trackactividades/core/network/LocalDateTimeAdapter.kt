package com.aitor.trackactividades.core.network

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LocalDateTimeAdapter : TypeAdapter<LocalDateTime>() {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    @Throws(IOException::class)
    override fun write(out: JsonWriter, value: LocalDateTime?) {
        if (value == null) {
            out.nullValue()
        } else {
            out.value(formatter.format(value))
        }
    }

    @Throws(IOException::class)
    override fun read(`in`: JsonReader): LocalDateTime? {
        return try {
            if (`in`.peek() == JsonToken.NULL) {
                `in`.nextNull()
                null
            } else {
                LocalDateTime.parse(`in`.nextString(), formatter)
            }
        } catch (e: Exception) {
            null // o maneja el error como prefieras
        }
    }
}