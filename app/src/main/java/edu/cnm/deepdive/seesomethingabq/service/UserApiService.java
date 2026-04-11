package edu.cnm.deepdive.seesomethingabq.service;

import java.util.concurrent.CompletableFuture;
import org.apache.catalina.User;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface UserApiService {

  @PATCH("users/me")
  CompletableFuture<User> updateProfile(@Body UpdateUserRequest request);

  @Multipart
  @POST("users/me/avatar")
  CompletableFuture<User> uploadAvatar(@Part MultipartBody.Part avatar);
}
