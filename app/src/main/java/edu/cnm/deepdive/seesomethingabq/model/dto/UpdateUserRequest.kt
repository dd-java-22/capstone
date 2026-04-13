package edu.cnm.deepdive.seesomethingabq.model.dto

/**
 * Request payload for updating user profile information.
 *
 * This DTO is used when sending profile update requests to the backend via
 * `PATCH /users/me`. Only the fields provided (non-null values) will be updated.
 * Fields left as `null` will be ignored by the backend and remain unchanged.
 *
 * @property displayName New display name for the user (nullable; optional).
 * @property email New email address for the user (nullable; optional).
 */
data class UpdateUserRequest(
  val displayName: String? = null,
  val email: String? = null
)
