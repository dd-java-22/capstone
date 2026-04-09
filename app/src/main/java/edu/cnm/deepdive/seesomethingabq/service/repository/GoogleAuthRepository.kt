package edu.cnm.deepdive.seesomethingabq.service.repository

import android.app.Activity
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import java.util.concurrent.CompletableFuture

/**
 * Repository handling Google identity credential acquisition and refresh.
 */
interface GoogleAuthRepository {

    /**
     * Attempts to sign in without user interaction where possible.
     *
     * @param activity activity used to launch sign-in flows.
     * @return future completing with a credential.
     */
    fun signInQuickly(activity: Activity): CompletableFuture<GoogleIdTokenCredential>

    /**
     * Performs an interactive Google sign-in.
     *
     * @param activity activity used to launch sign-in flows.
     * @return future completing with a credential.
     */
    fun signIn(activity: Activity): CompletableFuture<GoogleIdTokenCredential>

    /**
     * Returns a valid credential for the current user, signing in if needed.
     *
     * @param activity activity used to launch sign-in flows.
     * @return future completing with a valid credential.
     */
    fun getValidCredential(activity: Activity): CompletableFuture<GoogleIdTokenCredential>

    /**
     * Refreshes an existing credential.
     *
     * @param activity activity used to launch refresh flows.
     * @param credential credential to refresh.
     * @return future completing with an updated credential.
     */
    fun refreshToken(activity: Activity, credential: GoogleIdTokenCredential): CompletableFuture<GoogleIdTokenCredential>

    /**
     * Signs out of the current Google session.
     *
     * @return future that completes when sign-out is done.
     */
    fun signOut(): CompletableFuture<Void?>

    /**
     * Exception indicating the caller must perform an interactive sign-in to proceed.
     */
    class SignInRequiredException(message: String, cause: Throwable) : RuntimeException(message, cause)

}
