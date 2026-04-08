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

/**
 * JPA entity representing an image associated with an {@link IssueReport}.
 */
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

  /**
   * Returns the database identifier for this image.
   *
   * @return primary key value.
   */
  public Long getId() {
    return id;
  }

  /**
   * Returns the parent report.
   *
   * @return issue report.
   */
  public IssueReport getIssueReport() {
    return issueReport;
  }

  /**
   * Sets the parent report.
   *
   * @param issueReport issue report.
   */
  public void setIssueReport(IssueReport issueReport) {
    this.issueReport = issueReport;
  }

  /**
   * Returns the stored content locator for this image.
   *
   * @return image locator URI.
   */
  public URI getImageLocator() {
    return imageLocator;
  }

  /**
   * Sets the stored content locator for this image.
   *
   * @param imageLocator image locator URI.
   */
  public void setImageLocator(URI imageLocator) {
    this.imageLocator = imageLocator;
  }

  /**
   * Returns the original filename.
   *
   * @return filename.
   */
  public String getFilename() {
    return filename;
  }

  /**
   * Sets the original filename.
   *
   * @param filename filename.
   */
  public void setFilename(String filename) {
    this.filename = filename;
  }

  /**
   * Returns the MIME type of the image.
   *
   * @return MIME type.
   */
  public String getMimeType() {
    return mimeType;
  }

  /**
   * Sets the MIME type of the image.
   *
   * @param mimeType MIME type.
   */
  public void setMimeType(String mimeType) {
    this.mimeType = mimeType;
  }

  /**
   * Returns the album order index used for sorting images within a report.
   *
   * @return album order.
   */
  public int getAlbumOrder() {
    return albumOrder;
  }

  /**
   * Sets the album order index used for sorting images within a report.
   *
   * @param albumOrder album order.
   */
  public void setAlbumOrder(int albumOrder) {
    this.albumOrder = albumOrder;
  }

  /**
   * Returns the external identifier for this image.
   *
   * @return external ID.
   */
  public UUID getExternalId() {
    return externalId;
  }

  @PrePersist
  void onCreate() {
    this.externalId = UUID.randomUUID();
  }
}
