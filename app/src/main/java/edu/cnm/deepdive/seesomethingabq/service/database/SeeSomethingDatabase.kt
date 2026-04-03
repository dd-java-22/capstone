package edu.cnm.deepdive.seesomethingabq.service.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import edu.cnm.deepdive.seesomethingabq.model.entity.IssueType
import edu.cnm.deepdive.seesomethingabq.model.entity.UserProfile
import edu.cnm.deepdive.seesomethingabq.service.dao.IssueTypeDao
import edu.cnm.deepdive.seesomethingabq.service.dao.UserDao
import java.net.URI
import java.net.URL
import java.time.Instant
import java.util.UUID

@Database(entities = [UserProfile::class, IssueType::class], version = SeeSomethingDatabase.VERSION)
@TypeConverters(Converters::class)
abstract class SeeSomethingDatabase: RoomDatabase() {

  abstract fun getUserDao(): UserDao
  abstract fun getIssueTypeDao(): IssueTypeDao

  companion object {
    const val NAME = "seesomething-db"
    const val VERSION = 2

    val MIGRATION_1_2: Migration = object : Migration(1, 2) {
      override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
          """
          CREATE TABLE IF NOT EXISTS issueType (
            issue_type_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            issue_type_tag TEXT NOT NULL,
            issue_type_description TEXT NOT NULL
          )
          """.trimIndent()
        )
        db.execSQL(
          """
          CREATE UNIQUE INDEX IF NOT EXISTS index_issueType_issue_type_tag
          ON issueType (issue_type_tag)
          """.trimIndent()
        )
      }
    }
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
