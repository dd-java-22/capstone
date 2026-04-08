package edu.cnm.deepdive.seesomethingabq.model.dto;

import java.util.List;

/**
 * Request body DTO for creating or updating an issue report.
 *
 * <p>Note: Images are uploaded separately after report creation; therefore, reportImages are not
 * included here.
 */
public class IssueReportRequest {

  private String textDescription;
  private Double latitude;
  private Double longitude;
  private String streetCoordinate;
  private String locationDescription;
  private List<String> issueTypes;

  /**
   * Returns the user-supplied report description text.
   *
   * @return description text.
   */
  public String getTextDescription() {
    return textDescription;
  }

  /**
   * Sets the user-supplied report description text.
   *
   * @param textDescription description text.
   */
  public void setTextDescription(String textDescription) {
    this.textDescription = textDescription;
  }

  /**
   * Returns the latitude coordinate for the report location.
   *
   * @return latitude.
   */
  public Double getLatitude() {
    return latitude;
  }

  /**
   * Sets the latitude coordinate for the report location.
   *
   * @param latitude latitude.
   */
  public void setLatitude(Double latitude) {
    this.latitude = latitude;
  }

  /**
   * Returns the longitude coordinate for the report location.
   *
   * @return longitude.
   */
  public Double getLongitude() {
    return longitude;
  }

  /**
   * Sets the longitude coordinate for the report location.
   *
   * @param longitude longitude.
   */
  public void setLongitude(Double longitude) {
    this.longitude = longitude;
  }

  /**
   * Returns the street coordinate for the report location, if provided.
   *
   * @return street coordinate.
   */
  public String getStreetCoordinate() {
    return streetCoordinate;
  }

  /**
   * Sets the street coordinate for the report location.
   *
   * @param streetCoordinate street coordinate.
   */
  public void setStreetCoordinate(String streetCoordinate) {
    this.streetCoordinate = streetCoordinate;
  }

  /**
   * Returns a free-form location description, if provided.
   *
   * @return location description.
   */
  public String getLocationDescription() {
    return locationDescription;
  }

  /**
   * Sets a free-form location description.
   *
   * @param locationDescription location description.
   */
  public void setLocationDescription(String locationDescription) {
    this.locationDescription = locationDescription;
  }

  /**
   * Returns the issue type tags associated with this report.
   *
   * @return issue type tags.
   */
  public List<String> getIssueTypes() {
    return issueTypes;
  }

  /**
   * Sets the issue type tags associated with this report.
   *
   * @param issueTypes issue type tags.
   */
  public void setIssueTypes(List<String> issueTypes) {
    this.issueTypes = issueTypes;
  }
}

