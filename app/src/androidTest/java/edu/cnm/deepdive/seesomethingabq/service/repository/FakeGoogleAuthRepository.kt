package edu.cnm.deepdive.seesomethingabq.service.repository

import android.app.Activity
import android.os.Bundle
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import java.util.concurrent.CompletableFuture
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class FakeGoogleAuthRepository @Inject constructor() : GoogleAuthRepository {

    var failSignIn = false
    var fakeToken = "fake-id-token"
    var fakeEmail = "test@example.com"
    var fakeDisplayName = "Test User"

    override fun signInQuickly(activity: Activity): CompletableFuture<GoogleIdTokenCredential> {
        return if (failSignIn) {
            CompletableFuture.failedFuture(GoogleAuthRepository.SignInRequiredException("Simulated Failure", RuntimeException()))
        } else {
            CompletableFuture.completedFuture(createFakeCredential())
        }
    }

    override fun signIn(activity: Activity): CompletableFuture<GoogleIdTokenCredential> {
        return if (failSignIn) {
            CompletableFuture.failedFuture(GoogleAuthRepository.SignInRequiredException("Simulated Failure", RuntimeException()))
        } else {
            CompletableFuture.completedFuture(createFakeCredential())
        }
    }

    override fun refreshToken(
        activity: Activity,
        credential: GoogleIdTokenCredential
    ): CompletableFuture<GoogleIdTokenCredential> {
        return CompletableFuture.completedFuture(credential)
    }

    override fun signOut(): CompletableFuture<Void?> {
        return CompletableFuture.completedFuture(null)
    }

    private fun createFakeCredential(): GoogleIdTokenCredential {
        val data = Bundle().apply {
            putString("com.google.android.libraries.identity.googleid.BUNDLE_KEY_ID_TOKEN", fakeToken)
            putString("com.google.android.libraries.identity.googleid.BUNDLE_KEY_ID", fakeEmail)
            putString("com.google.android.libraries.identity.googleid.BUNDLE_KEY_DISPLAY_NAME", fakeDisplayName)
        }
        return GoogleIdTokenCredential.createFrom(data)
    }
}
