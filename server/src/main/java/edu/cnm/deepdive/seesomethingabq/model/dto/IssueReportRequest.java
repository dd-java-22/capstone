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

  public String getTextDescription() {
    return textDescription;
  }

  public void setTextDescription(String textDescription) {
    this.textDescription = textDescription;
  }

  public Double getLatitude() {
    return latitude;
  }

  public void setLatitude(Double latitude) {
    this.latitude = latitude;
  }

  public Double getLongitude() {
    return longitude;
  }

  public void setLongitude(Double longitude) {
    this.longitude = longitude;
  }

  public String getStreetCoordinate() {
    return streetCoordinate;
  }

  public void setStreetCoordinate(String streetCoordinate) {
    this.streetCoordinate = streetCoordinate;
  }

  public String getLocationDescription() {
    return locationDescription;
  }

  public void setLocationDescription(String locationDescription) {
    this.locationDescription = locationDescription;
  }

  public List<String> getIssueTypes() {
    return issueTypes;
  }

  public void setIssueTypes(List<String> issueTypes) {
    this.issueTypes = issueTypes;
  }
}

