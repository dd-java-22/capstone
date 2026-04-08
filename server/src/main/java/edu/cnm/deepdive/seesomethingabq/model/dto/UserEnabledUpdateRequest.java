package edu.cnm.deepdive.seesomethingabq.model.dto;

/**
 * Request body DTO for updating enabled/disabled status of a user.
 */
public class UserEnabledUpdateRequest {

  private boolean enabled;

  /**
   * Returns the desired enabled/disabled state for the user.
   *
   * @return {@code true} to enable; {@code false} to disable.
   */
  public boolean isEnabled() {
    return enabled;
  }

  /**
   * Sets the desired enabled/disabled state for the user.
   *
   * @param enabled {@code true} to enable; {@code false} to disable.
   */
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

}

