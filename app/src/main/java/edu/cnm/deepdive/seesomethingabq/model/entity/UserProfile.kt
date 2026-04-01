package edu.cnm.deepdive.seesomethingabq.model.entity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.net.URL
import java.time.Instant
import java.util.UUID

data class UserProfile(
  @Expose
  @SerializedName("externalId")
  val externalId: UUID,

  @Expose
  @SerializedName("displayName")
  val displayName: String,

  @Expose
  @SerializedName("email")
  val email: String,

  @Expose
  @SerializedName("avatar")
  val avatar: URL?,

  @Expose
  @SerializedName("manager")
  val manager: Boolean,

  @Expose
  @SerializedName("timeCreated")
  val timeCreated: Instant,

  @Expose
  @SerializedName("userEnabled")
  val userEnabled: Boolean,
)

