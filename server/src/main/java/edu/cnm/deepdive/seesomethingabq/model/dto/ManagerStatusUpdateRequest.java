package edu.cnm.deepdive.seesomethingabq.model.dto;

/**
 * Request body DTO for updating manager status of a user.
 */
public class ManagerStatusUpdateRequest {

  private boolean manager;

  /**
   * Returns whether the user should have manager privileges.
   *
   * @return {@code true} if manager; {@code false} otherwise.
   */
  public boolean isManager() {
    return manager;
  }

  /**
   * Sets whether the user should have manager privileges.
   *
   * @param manager {@code true} if manager; {@code false} otherwise.
   */
  public void setManager(boolean manager) {
    this.manager = manager;
  }

}

