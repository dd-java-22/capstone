package edu.cnm.deepdive.seesomethingabq.model.dto;

import edu.cnm.deepdive.seesomethingabq.model.entity.UserProfile;
import java.net.URL;
import java.time.Instant;
import java.util.UUID;

/**
 * Response DTO for {@code /manager/users} endpoints.
 */
public class ManagerUserResponse {

  private UUID externalId;
  private String displayName;
  private String email;
  private URL avatar;
  private boolean isManager;
  private Instant timeCreated;
  private boolean userEnabled;

  public UUID getExternalId() {
    return externalId;
  }

  public void setExternalId(UUID externalId) {
    this.externalId = externalId;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public URL getAvatar() {
    return avatar;
  }

  public void setAvatar(URL avatar) {
    this.avatar = avatar;
  }

  public boolean isManager() {
    return isManager;
  }

  public void setManager(boolean manager) {
    isManager = manager;
  }

  public Instant getTimeCreated() {
    return timeCreated;
  }

  public void setTimeCreated(Instant timeCreated) {
    this.timeCreated = timeCreated;
  }

  public boolean isUserEnabled() {
    return userEnabled;
  }

  public void setUserEnabled(boolean userEnabled) {
    this.userEnabled = userEnabled;
  }

  public static ManagerUserResponse fromEntity(UserProfile userProfile) {
    if (userProfile == null) {
      return null;
    }
    ManagerUserResponse dto = new ManagerUserResponse();
    dto.setExternalId(userProfile.getExternalId());
    dto.setDisplayName(userProfile.getDisplayName());
    dto.setEmail(userProfile.getEmail());
    dto.setAvatar(userProfile.getAvatar());
    dto.setManager(userProfile.isManager());
    dto.setTimeCreated(userProfile.getTimeCreated());
    dto.setUserEnabled(userProfile.getUserEnabled());
    return dto;
  }

}
