package edu.cnm.deepdive.seesomethingabq.model.dto;

/**
 * Request body DTO for updating an {@link edu.cnm.deepdive.seesomethingabq.model.entity.AcceptedState}
 * description.
 */
public class AcceptedStateDescriptionUpdateRequest {

  private String statusTagDescription;

  /**
   * Returns the accepted-state description.
   *
   * @return description value.
   */
  public String getStatusTagDescription() {
    return statusTagDescription;
  }

  /**
   * Sets the accepted-state description.
   *
   * @param statusTagDescription description value.
   */
  public void setStatusTagDescription(String statusTagDescription) {
    this.statusTagDescription = statusTagDescription;
  }

}

