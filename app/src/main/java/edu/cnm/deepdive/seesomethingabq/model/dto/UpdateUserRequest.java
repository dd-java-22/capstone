package edu.cnm.deepdive.seesomethingabq.model.dto;

public class UpdateUserRequest {
  private String displayName;
  private String email;

  public UpdateUserRequest(String displayName, String email) {
    this.displayName = displayName;
    this.email = email;
  }
}
