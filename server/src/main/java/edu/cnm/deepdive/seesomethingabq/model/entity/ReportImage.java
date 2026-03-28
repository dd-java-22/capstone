package edu.cnm.deepdive.seesomethingabq.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import edu.cnm.deepdive.seesomethingabq.model.converter.UriAttributeConverter;
import java.net.URI;
import java.util.UUID;

@Entity
@Table(
  name = "report_image",
  indexes = {
    @Index(name = "ix_report_image_issue_report_id", columnList = "issue_report_id")
  }
)
public class ReportImage {

  @Id
  @Column(name = "report_image_id", updatable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JsonIgnore
  private Long id;

  @Column(name = "report_image_external_id", updatable = false)
  private UUID externalId;

  // used AI to help with ManyToOne annotation
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "issue_report_id", nullable = false, updatable = false)
  @JsonIgnore
  private IssueReport issueReport;

  @Convert(converter = UriAttributeConverter.class)
  @Column(nullable = false, updatable = false, length = 255, columnDefinition = "varchar(255)")
  private URI imageLocator;

  @Column(nullable = false, updatable = false)
  private String filename;

  @Column(nullable = false, updatable = false)
  private String mimeType;

  @Column(nullable = false, updatable = false)
  private int albumOrder;

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

  public int getAlbumOrder() {
    return albumOrder;
  }

  public void setAlbumOrder(int albumOrder) {
    this.albumOrder = albumOrder;
  }

  public UUID getExternalId() {
    return externalId;
  }

  @PrePersist
  void onCreate() {
    this.externalId = UUID.randomUUID();
  }
}
