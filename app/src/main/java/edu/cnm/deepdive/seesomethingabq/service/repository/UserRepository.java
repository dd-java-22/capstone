package edu.cnm.deepdive.seesomethingabq.service.repository;

import android.app.Activity;
import android.net.Uri;
import edu.cnm.deepdive.seesomethingabq.service.UserApiService;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

@Singleton
public class UserRepository {

  private final UserApiService api;

  @Inject
  public UserRepository(UserApiService api) {
    this.api = api;
  }

  public CompletableFuture<User> updateUser(String displayName, String email) {
    UpdateUserRequest request = new UpdateUserRequest(displayName, email);
    return api.updateProfile(request);
  }

  public CompletableFuture<User> uploadAvatar(Activity activity, Uri uri) {
    return CompletableFuture.supplyAsync(() -> {
      try {
        InputStream input = activity.getContentResolver().openInputStream(uri);
        byte[] bytes = input.readAllBytes();

        RequestBody requestBody = RequestBody.create(
            bytes,
            MediaType.parse("image/*")
        );

        MultipartBody.Part part = MultipartBody.Part.createFormData(
            "avatar",
            "avatar.jpg",
            requestBody
        );

        return api.uploadAvatar(part).get();
      } catch (Exception e) {
        throw new CompletionException(e);
      }
    });
  }
}
