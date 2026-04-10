package edu.cnm.deepdive.seesomethingabq.service.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import edu.cnm.deepdive.seesomethingabq.model.entity.AcceptedState
import edu.cnm.deepdive.seesomethingabq.model.entity.IssueType
import edu.cnm.deepdive.seesomethingabq.model.entity.UserProfile
import edu.cnm.deepdive.seesomethingabq.service.dao.AcceptedStateDao
import edu.cnm.deepdive.seesomethingabq.service.dao.IssueTypeDao
import edu.cnm.deepdive.seesomethingabq.service.dao.UserDao
import java.net.URI
import java.net.URL
import java.time.Instant
import java.util.UUID

@Database(entities = [UserProfile::class, IssueType::class, AcceptedState::class], version = SeeSomethingDatabase.VERSION)
@TypeConverters(Converters::class)
/**
 * Room database for the Android app's cached server data.
 */
abstract class SeeSomethingDatabase: RoomDatabase() {

  /**
   * Returns the [UserDao].
   *
   * @return user DAO.
   */
  abstract fun getUserDao(): UserDao

  /**
   * Returns the [IssueTypeDao].
   *
   * @return issue type DAO.
   */
  abstract fun getIssueTypeDao(): IssueTypeDao

  /**
   * Returns the [AcceptedStateDao].
   *
   * @return accepted state DAO.
   */
  abstract fun getAcceptedStateDao(): AcceptedStateDao

  companion object {
    /** Database file name. */
    const val NAME = "seesomething-db"
    /** Current schema version. */
    const val VERSION = 3

    /**
     * Migration from schema version 1 to 2.
     */
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

    /**
     * Migration from schema version 2 to 3.
     */
    val MIGRATION_2_3: Migration = object : Migration(2, 3) {
      override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
          """
          CREATE TABLE IF NOT EXISTS acceptedState (
            accepted_state_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            status_tag TEXT NOT NULL,
            status_tag_description TEXT NOT NULL
          )
          """.trimIndent()
        )
        db.execSQL(
          """
          CREATE UNIQUE INDEX IF NOT EXISTS index_acceptedState_status_tag
          ON acceptedState (status_tag)
          """.trimIndent()
        )
      }
    }
  }
}

/**
 * Room type converters for non-primitive types stored in the database.
 */
class Converters {

  /**
   * Converts an [Instant] into epoch milliseconds.
   *
   * @param instant instant value.
   * @return epoch milliseconds, or {@code null}.
   */
  @TypeConverter
  fun toLong(instant: Instant?): Long? = instant?.toEpochMilli()

  /**
   * Converts epoch milliseconds into an [Instant].
   *
   * @param epochMilli epoch milliseconds.
   * @return instant value, or {@code null}.
   */
  @TypeConverter
  fun toInstant(epochMilli: Long?): Instant? = epochMilli?.let { Instant.ofEpochMilli(it) }

  /**
   * Converts a [URL] into a string.
   *
   * @param url URL value.
   * @return string value, or {@code null}.
   */
  @TypeConverter
  fun toString(url: URL?): String? = url?.toString()

  /**
   * Converts a string into a [URL].
   *
   * @param string string value.
   * @return URL value, or {@code null}.
   */
  @TypeConverter
  fun toUrl(string: String?): URL? = string?.let { URI(it).toURL() }

  /**
   * Converts a [UUID] into a string.
   *
   * @param uuid UUID value.
   * @return string value, or {@code null}.
   */
  @TypeConverter
  fun toString(uuid: UUID?): String? = uuid?.toString()

  /**
   * Converts a string into a [UUID].
   *
   * @param string string value.
   * @return UUID value, or {@code null}.
   */
  @TypeConverter
  fun toUUID(string: String?): UUID? = string?.let { UUID.fromString(it) }

}
