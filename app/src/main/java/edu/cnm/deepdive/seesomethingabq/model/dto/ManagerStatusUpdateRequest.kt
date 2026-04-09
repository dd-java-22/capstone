package edu.cnm.deepdive.seesomethingabq.model.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Request body for PATCH /manager/users/{externalId}/manager-status.
 *
 * Server expects a JSON object with a single boolean property named "manager".
 */
data class ManagerStatusUpdateRequest(
  @Expose
  @SerializedName("manager")
  val manager: Boolean,
)

