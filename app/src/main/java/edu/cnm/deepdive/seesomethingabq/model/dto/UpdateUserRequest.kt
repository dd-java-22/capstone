package edu.cnm.deepdive.seesomethingabq.model.dto

/**
 * Request payload for updating user profile information.
 *
 * @property displayName new display name (optional).
 * @property email new email address (optional).
 */
data class UpdateUserRequest(
  val displayName: String? = null,
  val email: String? = null
)
