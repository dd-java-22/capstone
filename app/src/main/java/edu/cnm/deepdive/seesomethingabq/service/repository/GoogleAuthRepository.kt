package edu.cnm.deepdive.seesomethingabq.service.repository

import android.app.Activity
import java.util.concurrent.CompletableFuture

interface GoogleAuthRepository {

    fun signInQuickly(activity: Activity): CompletableFuture<GoogleIdTokenCredential>

}