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
interface UserDao {

  @Insert
  suspend fun insert(userProfile:UserProfile): Long

  @Update
  suspend fun update(userProfile:UserProfile)

  @Delete
  suspend fun delete(userProfile:UserProfile)

  @Query("SELECT * FROM userProfile WHERE user_profile_id = :id")
  fun getById(id: Long): LiveData<UserProfile>

  @Query("SELECT * FROM userProfile WHERE oauth_key = :oauthKey")
  suspend fun getByOauthKey(oauthKey: String): UserProfile?

  @Query("SELECT * FROM userProfile WHERE external_id = :externalId")
  suspend fun getByExternalId(externalId: UUID): UserProfile?

  @Query("SELECT * FROM userProfile WHERE display_name = :displayName")
  suspend fun getByDisplayName(displayName: String): UserProfile?
}
