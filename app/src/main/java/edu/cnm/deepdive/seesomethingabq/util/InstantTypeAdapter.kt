package edu.cnm.deepdive.seesomethingabq.util

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import jakarta.inject.Inject
import java.time.Instant

class InstantTypeAdapter @Inject constructor() : TypeAdapter<Instant>() {

  override fun write(out: JsonWriter, value: Instant?) {
    out.value(value?.toString())
  }

  override fun read(`in`: JsonReader): Instant? =
    `in`.nextString()?.let { Instant.parse(it) }

}