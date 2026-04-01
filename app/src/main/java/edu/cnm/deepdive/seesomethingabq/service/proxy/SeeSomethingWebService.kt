package edu.cnm.deepdive.seesomethingabq.service.proxy

import retrofit2.http.GET
import retrofit2.http.Header

interface SeeSomethingWebService {

  @GET("users/me")
  suspend fun getMe(@Header("Authorization") bearerToken: String): User

}