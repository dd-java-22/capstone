package edu.cnm.deepdive.seesomethingabq.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.net.URL
import java.time.Instant
import java.util.UUID

@Entity(
  tableName = "userProfile",
  indices = [
    Index(value = ["display_name"], unique = true),
    Index(value = ["oauth_key"], unique = true),
    Index(value = ["external_id"], unique = true)
  ]
)
data class UserProfile(
  @PrimaryKey(autoGenerate = true)
  @ColumnInfo(name = "user_profile_id")
  val id: Long = 0,

  @Expose
  @SerializedName("externalId")
  @ColumnInfo("external_id")
  val externalId: UUID,

  @Expose
  @SerializedName("displayName")
  @ColumnInfo("display_name", collate = ColumnInfo.NOCASE)
  val displayName: String,

  @ColumnInfo(name = "oauth_key")
  val oauthKey: String,

  @Expose
  @SerializedName("email")
  @ColumnInfo("email")
  val email: String,

  @Expose
  @SerializedName("avatar")
  @ColumnInfo("avatar")
  val avatar: URL?,

  @Expose
  @SerializedName("manager")
  @ColumnInfo("manager")
  val manager: Boolean,

  @Expose
  @SerializedName("timeCreated")
  @ColumnInfo("time_created")
  val timeCreated: Instant,

  @Expose
  @SerializedName("userEnabled")
  @ColumnInfo("user_enabled")
  val userEnabled: Boolean,
)

