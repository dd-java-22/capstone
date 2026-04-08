package edu.cnm.deepdive.seesomethingabq.service.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import edu.cnm.deepdive.seesomethingabq.model.entity.UserProfile
import java.util.UUID

@Dao
/**
 * Room DAO for storing and retrieving [UserProfile] records.
 */
interface UserDao {

  /**
   * Inserts a user profile.
   *
   * @param userProfile profile to insert.
   * @return row ID of the inserted record.
   */
  @Insert
  suspend fun insert(userProfile:UserProfile): Long

  /**
   * Updates a user profile.
   *
   * @param userProfile profile to update.
   */
  @Update
  suspend fun update(userProfile:UserProfile)

  /**
   * Deletes a user profile.
   *
   * @param userProfile profile to delete.
   */
  @Delete
  suspend fun delete(userProfile:UserProfile)

  /**
   * Returns a user profile by local database ID.
   *
   * @param id local user ID.
   * @return live data stream of the user profile.
   */
  @Query("SELECT * FROM userProfile WHERE user_profile_id = :id")
  fun getById(id: Long): LiveData<UserProfile>

  /**
   * Returns a user profile by OAuth key.
   *
   * @param oauthKey OAuth subject key.
   * @return matching profile, or {@code null} if not found.
   */
  @Query("SELECT * FROM userProfile WHERE oauth_key = :oauthKey")
  suspend fun getByOauthKey(oauthKey: String): UserProfile?

  /**
   * Returns a user profile by external ID.
   *
   * @param externalId external ID.
   * @return matching profile, or {@code null} if not found.
   */
  @Query("SELECT * FROM userProfile WHERE external_id = :externalId")
  suspend fun getByExternalId(externalId: UUID): UserProfile?

  /**
   * Returns a user profile by display name.
   *
   * @param displayName display name.
   * @return matching profile, or {@code null} if not found.
   */
  @Query("SELECT * FROM userProfile WHERE display_name = :displayName")
  suspend fun getByDisplayName(displayName: String): UserProfile?
}
