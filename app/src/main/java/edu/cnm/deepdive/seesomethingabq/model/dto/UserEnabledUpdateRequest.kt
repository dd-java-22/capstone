package edu.cnm.deepdive.seesomethingabq.model.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Request body for PATCH /manager/users/{externalId}/enabled.
 *
 * Server expects a JSON object with a single boolean property named "enabled".
 */
data class UserEnabledUpdateRequest(
  @Expose
  @SerializedName("enabled")
  val enabled: Boolean,
)

