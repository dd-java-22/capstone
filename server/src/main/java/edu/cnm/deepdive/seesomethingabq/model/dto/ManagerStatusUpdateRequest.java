package edu.cnm.deepdive.seesomethingabq.model.dto;

/**
 * Request body DTO for updating manager status of a user.
 */
public class ManagerStatusUpdateRequest {

  private boolean manager;

  public boolean isManager() {
    return manager;
  }

  public void setManager(boolean manager) {
    this.manager = manager;
  }

}

