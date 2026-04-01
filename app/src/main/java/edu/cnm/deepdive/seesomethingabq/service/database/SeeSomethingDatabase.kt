package edu.cnm.deepdive.seesomethingabq.service.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import edu.cnm.deepdive.seesomethingabq.model.entity.UserProfile
import edu.cnm.deepdive.seesomethingabq.service.dao.UserDao
import java.net.URI
import java.net.URL
import java.time.Instant
import java.util.UUID

@Database(entities = [UserProfile::class], version = SeeSomethingDatabase.VERSION)
@TypeConverters(Converters::class)
abstract class SeeSomethingDatabase: RoomDatabase() {

  abstract fun getUserDao(): UserDao

  companion object {
    const val NAME = "seesomething-db"
    const val VERSION = 1
  }
}

class Converters {

  @TypeConverter
  fun toLong(instant: Instant?): Long? = instant?.toEpochMilli()

  @TypeConverter
  fun toInstant(epochMilli: Long?): Instant? = epochMilli?.let { Instant.ofEpochMilli(it) }

  @TypeConverter
  fun toString(url: URL?): String? = url?.toString()

  @TypeConverter
  fun toUrl(string: String?): URL? = string?.let { URI(it).toURL() }

  @TypeConverter
  fun toString(uuid: UUID?): String? = uuid?.toString()

  @TypeConverter
  fun toUUID(string: String?): UUID? = string?.let { UUID.fromString(it) }

}