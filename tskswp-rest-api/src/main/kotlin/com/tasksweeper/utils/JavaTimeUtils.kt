package com.tasksweeper.utils

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.tasksweeper.controller.DateDTO
import com.tasksweeper.controller.TimeDTO
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter


fun instantOf(date: DateDTO, time: TimeDTO): Instant? =
    Instant.parse("${date.year}-${date.month}-${date.day}T${time.hour}:${time.minute}:${time.second}.000Z")

class InstantSerializer : JsonSerializer<Instant>() {
    private val instantFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneOffset.UTC)

    override fun serialize(instant: Instant, generator: JsonGenerator, serializer: SerializerProvider) {
        generator.writeString(instantFormatter.format(instant))
    }
}
