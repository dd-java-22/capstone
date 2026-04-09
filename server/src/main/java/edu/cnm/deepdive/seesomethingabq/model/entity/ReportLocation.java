package edu.cnm.deepdive.seesomethingabq.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * JPA entity representing the location associated with an {@link IssueReport}.
 */
@Entity
@Table(name = "report_location")
public class ReportLocation {

  @Id
  @Column(name = "report_location_id", updatable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JsonIgnore
  private Long id;

  private Double latitude;

  private Double longitude;

  private String streetCoordinate; // TODO: 3/14/2026 Needs client clarification

  private String locationDescription;

  // used AI to help with OneToOne annotation
  @OneToOne(mappedBy = "reportLocation", optional = false)
  @JsonIgnore
  private IssueReport issueReport;

  /**
   * Returns the database identifier for this location.
   *
   * @return primary key value.
   */
  public Long getId() {
    return id;
  }

  /**
   * Returns the latitude coordinate, if provided.
   *
   * @return latitude.
   */
  public Double getLatitude() {
    return latitude;
  }

  /**
   * Sets the latitude coordinate.
   *
   * @param latitude latitude.
   */
  public void setLatitude(Double latitude) {
    this.latitude = latitude;
  }

  /**
   * Returns the longitude coordinate, if provided.
   *
   * @return longitude.
   */
  public Double getLongitude() {
    return longitude;
  }

  /**
   * Sets the longitude coordinate.
   *
   * @param longitude longitude.
   */
  public void setLongitude(Double longitude) {
    this.longitude = longitude;
  }

  /**
   * Returns the street coordinate for this location, if provided.
   *
   * @return street coordinate.
   */
  public String getStreetCoordinate() {
    return streetCoordinate;
  }

  /**
   * Sets the street coordinate for this location.
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
   * Returns the report associated with this location.
   *
   * @return issue report.
   */
  public IssueReport getIssueReport() {
    return issueReport;
  }

  /**
   * Sets the report associated with this location.
   *
   * @param issueReport issue report.
   */
  public void setIssueReport(IssueReport issueReport) {
    this.issueReport = issueReport;
  }

  /**
   * Returns whether at least one location component is present.
   *
   * @return {@code true} if the location contains a coordinate pair, street coordinate, or description.
   */
  @AssertTrue(message = "Report location must include latitude+longitude, a street coordinate, or a location description.")
  public boolean isValidLocation() {
    boolean hasStreet = streetCoordinate != null && !streetCoordinate.trim().isBlank();
    boolean hasDescription = locationDescription != null && !locationDescription.trim().isBlank();
    boolean hasCoordinatePair = latitude != null && longitude != null;
    return hasCoordinatePair || hasStreet || hasDescription;
  }
}
