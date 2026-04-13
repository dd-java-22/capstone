package edu.cnm.deepdive.seesomethingabq.service

import android.app.Activity
import android.net.Uri
import edu.cnm.deepdive.seesomethingabq.model.entity.UserProfile
import java.util.concurrent.CompletableFuture

/**
 * Service providing sign-in, sign-out, and profile update operations for the Android app.
 *
 * This interface defines all user‑related operations exposed to the ViewModel layer,
 * including authentication, profile updates, and avatar uploads. Implementations of
 * this service are responsible for communicating with backend APIs (via Retrofit)
 * and returning results asynchronously using {@link CompletableFuture}.
 */
interface UserService {

  /**
   * Performs an interactive sign-in and returns the resolved user profile.
   *
   * @param activity Activity used to launch sign-in flows.
   * @return Future completing with the signed-in user's profile.
   */
  fun signIn(activity: Activity): CompletableFuture<UserProfile>

  /**
   * Signs out of the current session.
   *
   * @return Future that completes when sign-out is done.
   */
  fun signOut(): CompletableFuture<Void?>

  /**
   * Updates the current user's profile information.
   *
   * Implementations should send the updated fields to the backend and return the
   * updated {@link UserProfile}. Fields may be optional, but implementations must
   * ensure that null values are handled appropriately by the backend.
   *
   * @param activity Activity used for authentication flows.
   * @param displayName New display name (nullable if unchanged).
   * @param email New email address (nullable if unchanged).
   * @return Future completing with the updated user profile.
   */
  fun updateProfile(
    activity: Activity,
    displayName: String?,
    email: String?
  ): CompletableFuture<UserProfile>

  /**
   * Uploads a new avatar image for the current user.
   *
   * Implementations should read the image content from the provided URI, upload it
   * to the backend as multipart form data, and return the updated {@link UserProfile}
   * containing the new avatar URL.
   *
   * @param activity Activity used for authentication flows.
   * @param uri URI of the image to upload.
   * @return Future completing with the updated user profile.
   */
  fun uploadAvatar(
    activity: Activity,
    uri: Uri
  ): CompletableFuture<UserProfile>

  /**
   * Resolves the best avatar image URI for display.
   *
   * - For public/external URLs (e.g., Google-hosted avatars), returns the remote URL as a [Uri].
   * - For protected backend URLs (e.g., /users/{externalId}/avatar), downloads using the authenticated
   *   API client, caches to app cache, and returns a file [Uri].
   *
   * @param activity Activity used for authentication flows.
   * @param user current user profile.
   * @return Future completing with a displayable [Uri], or null if unavailable.
   */
  fun resolveAvatarUri(
    activity: Activity,
    user: UserProfile
  ): CompletableFuture<Uri?>
}
