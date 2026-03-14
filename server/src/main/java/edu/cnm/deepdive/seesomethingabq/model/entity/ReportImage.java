package edu.cnm.deepdive.seesomethingabq.model.entity;

import jakarta.persistence.*;

import java.net.URI;

@Entity
@Table(name = "report_image")
public class ReportImage {

  @Id
  @Column(name = "report_image_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long reportImageId;

  // used AI to help with ManyToOne annotation
  @ManyToOne
  @JoinColumn(name = "issue_report_id", nullable = false)
  private IssueReport issueReport;

  @Column(name = "image_locator", nullable = false)
  private URI imageLocator;

  @Column(name = "filename", nullable = false)
  private String filename;

  @Column(name = "mime_type", nullable = false)
  private String mimeType;
}
