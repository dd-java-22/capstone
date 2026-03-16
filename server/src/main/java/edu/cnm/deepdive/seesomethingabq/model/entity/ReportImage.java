package edu.cnm.deepdive.seesomethingabq.model.entity;

import jakarta.persistence.*;

import java.net.URI;

@Entity
@Table(
  name = "report_image",
  indexes = {
    @Index(name = "ix_report_image_issue_report_id", columnList = "issue_report_id")
  }
)
public class ReportImage {

  @Id
  @Column(name = "report_image_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

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

  public Long getId() {
    return id;
  }

  public IssueReport getIssueReport() {
    return issueReport;
  }

  public void setIssueReport(IssueReport issueReport) {
    this.issueReport = issueReport;
  }

  public URI getImageLocator() {
    return imageLocator;
  }

  public void setImageLocator(URI imageLocator) {
    this.imageLocator = imageLocator;
  }

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public String getMimeType() {
    return mimeType;
  }

  public void setMimeType(String mimeType) {
    this.mimeType = mimeType;
  }
}
