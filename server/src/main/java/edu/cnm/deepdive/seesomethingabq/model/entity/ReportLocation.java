package edu.cnm.deepdive.seesomethingabq.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "report_location")
public class ReportLocation {

  @Id
  @Column(name = "report_location_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "latitude")
  private Double latitude;

  @Column(name = "longitude")
  private Double longitude;

  @Column(name = "street_coordinate")
  private String streetCoordinate; // TODO: 3/14/2026 Needs client clarification

  @Column(name = "location_description")
  private String locationDescription;

  // used AI to help with OneToOne annotation
  @OneToOne(mappedBy = "reportLocation")
  private IssueReport issueReport;

  public Long getId() {
    return id;
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

  public IssueReport getIssueReport() {
    return issueReport;
  }

  public void setIssueReport(IssueReport issueReport) {
    this.issueReport = issueReport;
  }
}
