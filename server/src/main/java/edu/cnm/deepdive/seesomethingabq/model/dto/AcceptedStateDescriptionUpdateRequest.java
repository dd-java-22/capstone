package edu.cnm.deepdive.seesomethingabq.model.dto;

/**
 * Request body DTO for updating an {@link edu.cnm.deepdive.seesomethingabq.model.entity.AcceptedState}
 * description.
 */
public class AcceptedStateDescriptionUpdateRequest {

  private String statusTagDescription;

  public String getStatusTagDescription() {
    return statusTagDescription;
  }

  public void setStatusTagDescription(String statusTagDescription) {
    this.statusTagDescription = statusTagDescription;
  }

}

