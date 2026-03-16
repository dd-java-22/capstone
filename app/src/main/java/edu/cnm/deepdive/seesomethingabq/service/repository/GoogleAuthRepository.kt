package edu.cnm.deepdive.seesomethingabq.service.repository

import android.app.Activity
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import java.util.concurrent.CompletableFuture

interface GoogleAuthRepository {

    fun signInQuickly(activity: Activity): CompletableFuture<GoogleIdTokenCredential>

    fun signIn(activity: Activity): CompletableFuture<GoogleIdTokenCredential>

    fun refreshToken(activity: Activity, credential: GoogleIdTokenCredential): CompletableFuture<GoogleIdTokenCredential>

    fun signOut(activity: Activity): CompletableFuture<Void?>

    class SignInRequiredException(message: String, cause: Throwable) : RuntimeException(message, cause)

}