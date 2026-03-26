package edu.cnm.deepdive.seesomethingabq.model.dto;

/**
 * Request body DTO for updating enabled/disabled status of a user.
 */
public class UserEnabledUpdateRequest {

  private boolean enabled;

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

}

