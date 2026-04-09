package edu.cnm.deepdive.seesomethingabq.service

import android.app.Activity
import edu.cnm.deepdive.seesomethingabq.model.entity.UserProfile
import java.util.concurrent.CompletableFuture

/**
 * Service providing sign-in and sign-out operations for the Android app.
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

}
