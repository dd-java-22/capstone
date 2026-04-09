package edu.cnm.deepdive.seesomethingabq.model.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.net.URL
import java.time.Instant
import java.util.UUID

/**
 * DTO representing a manager-visible view of a user profile.
 *
 * This intentionally avoids local-only fields (e.g., Room primary key) and auth-only fields
 * (e.g., OAuth subject key), matching what the server is expected to return for manager lists.
 *
 * @property externalId server-side external identifier.
 * @property displayName user display name.
 * @property email user email address.
 * @property avatar avatar URL, if set.
 * @property manager whether the user has manager privileges.
 * @property timeCreated timestamp when the profile was created.
 * @property userEnabled whether the user is enabled.
 */
data class UserProfileSummary(
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

