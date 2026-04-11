package edu.cnm.deepdive.seesomethingabq.service

import android.app.Activity
import android.net.Uri
import edu.cnm.deepdive.seesomethingabq.model.entity.UserProfile
import java.util.concurrent.CompletableFuture

/**
 * Service providing sign-in, sign-out, and profile update operations for the Android app.
 */
interface UserService {

  /**
   * Performs an interactive sign-in and returns the resolved user profile.
   *
   * @param activity activity used to launch sign-in flows.
   * @return future completing with the signed-in user's profile.
   */
  fun signIn(activity: Activity): CompletableFuture<UserProfile>

  /**
   * Signs out of the current session.
   *
   * @return future that completes when sign-out is done.
   */
  fun signOut(): CompletableFuture<Void?>

  /**
   * Updates the current user's profile information.
   *
   * @param activity activity used for authentication flows.
   * @param displayName new display name (optional).
   * @param email new email address (optional).
   * @return future completing with the updated user profile.
   */
  fun updateProfile(
    activity: Activity,
    displayName: String?,
    email: String?
  ): CompletableFuture<UserProfile>

  /**
   * Uploads a new avatar image for the current user.
   *
   * @param activity activity used for authentication flows.
   * @param uri URI of the image to upload.
   * @return future completing with the updated user profile.
   */
  fun uploadAvatar(
    activity: Activity,
    uri: Uri
  ): CompletableFuture<UserProfile>

}
