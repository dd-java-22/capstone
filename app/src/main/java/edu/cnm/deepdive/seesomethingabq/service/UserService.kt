package edu.cnm.deepdive.seesomethingabq.service

import android.app.Activity
import edu.cnm.deepdive.seesomethingabq.model.entity.UserProfile
import java.util.concurrent.CompletableFuture

interface UserService {

  fun signIn(activity: Activity): CompletableFuture<UserProfile>

  fun signOut(): CompletableFuture<Void?>

}