package edu.cnm.deepdive.seesomethingabq.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "report_location")
public class ReportLocation {

  @Id
  @Column(name = "report_location_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long reportLocationId;

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
}
