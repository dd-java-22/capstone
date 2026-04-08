package edu.cnm.deepdive.seesomethingabq.model.dto;

import edu.cnm.deepdive.seesomethingabq.model.entity.AcceptedState;
import edu.cnm.deepdive.seesomethingabq.model.entity.IssueReport;
import edu.cnm.deepdive.seesomethingabq.model.entity.IssueType;
import edu.cnm.deepdive.seesomethingabq.model.entity.ReportImage;
import edu.cnm.deepdive.seesomethingabq.model.entity.ReportLocation;
import edu.cnm.deepdive.seesomethingabq.model.entity.UserProfile;
import java.net.URI;
import java.net.URL;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for {@code GET /manager/issue-reports}.
 *
 * <p>Uses nested DTOs for related entities to avoid recursive JSON serialization.
 */
public class ManagerIssueReportResponse {

  private UUID externalId;
  private UserProfileResponse userProfile;
  private ReportLocationResponse reportLocation;
  private AcceptedStateResponse acceptedState;
  private List<IssueTypeResponse> issueTypes;
  private Instant timeFirstReported;
  private Instant timeLastModified;
  private String textDescription;
  private List<ReportImageResponse> reportImages;

  /**
   * Returns the issue report external identifier.
   *
   * @return external ID.
   */
  public UUID getExternalId() {
    return externalId;
  }

  /**
   * Sets the issue report external identifier.
   *
   * @param externalId external ID.
   */
  public void setExternalId(UUID externalId) {
    this.externalId = externalId;
  }

  /**
   * Returns the reporting user profile details.
   *
   * @return user profile DTO.
   */
  public UserProfileResponse getUserProfile() {
    return userProfile;
  }

  /**
   * Sets the reporting user profile details.
   *
   * @param userProfile user profile DTO.
   */
  public void setUserProfile(UserProfileResponse userProfile) {
    this.userProfile = userProfile;
  }

  /**
   * Returns the report location details.
   *
   * @return report location DTO.
   */
  public ReportLocationResponse getReportLocation() {
    return reportLocation;
  }

  /**
   * Sets the report location details.
   *
   * @param reportLocation report location DTO.
   */
  public void setReportLocation(ReportLocationResponse reportLocation) {
    this.reportLocation = reportLocation;
  }

  /**
   * Returns the accepted-state details for the report.
   *
   * @return accepted-state DTO.
   */
  public AcceptedStateResponse getAcceptedState() {
    return acceptedState;
  }

  /**
   * Sets the accepted-state details for the report.
   *
   * @param acceptedState accepted-state DTO.
   */
  public void setAcceptedState(AcceptedStateResponse acceptedState) {
    this.acceptedState = acceptedState;
  }

  /**
   * Returns the issue types associated with the report.
   *
   * @return list of issue type DTOs.
   */
  public List<IssueTypeResponse> getIssueTypes() {
    return issueTypes;
  }

  /**
   * Sets the issue types associated with the report.
   *
   * @param issueTypes list of issue type DTOs.
   */
  public void setIssueTypes(List<IssueTypeResponse> issueTypes) {
    this.issueTypes = issueTypes;
  }

  /**
   * Returns the time the report was first created.
   *
   * @return first-reported timestamp.
   */
  public Instant getTimeFirstReported() {
    return timeFirstReported;
  }

  /**
   * Sets the time the report was first created.
   *
   * @param timeFirstReported first-reported timestamp.
   */
  public void setTimeFirstReported(Instant timeFirstReported) {
    this.timeFirstReported = timeFirstReported;
  }

  /**
   * Returns the time the report was last modified.
   *
   * @return last-modified timestamp.
   */
  public Instant getTimeLastModified() {
    return timeLastModified;
  }

  /**
   * Sets the time the report was last modified.
   *
   * @param timeLastModified last-modified timestamp.
   */
  public void setTimeLastModified(Instant timeLastModified) {
    this.timeLastModified = timeLastModified;
  }

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
   * Returns the images associated with the report.
   *
   * @return list of image DTOs.
   */
  public List<ReportImageResponse> getReportImages() {
    return reportImages;
  }

  /**
   * Sets the images associated with the report.
   *
   * @param reportImages list of image DTOs.
   */
  public void setReportImages(List<ReportImageResponse> reportImages) {
    this.reportImages = reportImages;
  }

  /**
   * Creates a response DTO from an {@link IssueReport} entity.
   *
   * @param report source entity.
   * @return populated DTO, or {@code null} if {@code report} is {@code null}.
   */
  public static ManagerIssueReportResponse fromEntity(IssueReport report) {
    if (report == null) {
      return null;
    }
    ManagerIssueReportResponse dto = new ManagerIssueReportResponse();
    dto.setExternalId(report.getExternalId());
    dto.setUserProfile(UserProfileResponse.fromEntity(report.getUserProfile()));
    dto.setReportLocation(ReportLocationResponse.fromEntity(report.getReportLocation()));
    dto.setAcceptedState(AcceptedStateResponse.fromEntity(report.getAcceptedState()));
    dto.setIssueTypes(
        report.getIssueTypes().stream()
            .map(IssueTypeResponse::fromEntity)
            .toList()
    );
    dto.setTimeFirstReported(report.getTimeFirstReported());
    dto.setTimeLastModified(report.getTimeLastModified());
    dto.setTextDescription(report.getTextDescription());
    dto.setReportImages(
        report.getReportImages().stream()
            .map((image) -> ReportImageResponse.fromEntity(report.getExternalId(), image))
            .toList()
    );
    return dto;
  }

  /**
   * Nested DTO exposing a subset of {@link UserProfile} fields for manager views.
   */
  public static class UserProfileResponse {

    private UUID externalId;
    private String displayName;
    private String email;
    private URL avatar;
    private boolean isManager;
    private Instant timeCreated;
    private boolean userEnabled;

    /**
     * Returns the user's external identifier.
     *
     * @return external ID.
     */
    public UUID getExternalId() {
      return externalId;
    }

    /**
     * Sets the user's external identifier.
     *
     * @param externalId external ID.
     */
    public void setExternalId(UUID externalId) {
      this.externalId = externalId;
    }

    /**
     * Returns the user's display name.
     *
     * @return display name.
     */
    public String getDisplayName() {
      return displayName;
    }

    /**
     * Sets the user's display name.
     *
     * @param displayName display name.
     */
    public void setDisplayName(String displayName) {
      this.displayName = displayName;
    }

    /**
     * Returns the user's email address.
     *
     * @return email address.
     */
    public String getEmail() {
      return email;
    }

    /**
     * Sets the user's email address.
     *
     * @param email email address.
     */
    public void setEmail(String email) {
      this.email = email;
    }

    /**
     * Returns the user's avatar URL, if available.
     *
     * @return avatar URL.
     */
    public URL getAvatar() {
      return avatar;
    }

    /**
     * Sets the user's avatar URL.
     *
     * @param avatar avatar URL.
     */
    public void setAvatar(URL avatar) {
      this.avatar = avatar;
    }

    /**
     * Returns whether the user has manager privileges.
     *
     * @return {@code true} if manager; {@code false} otherwise.
     */
    public boolean isManager() {
      return isManager;
    }

    /**
     * Sets whether the user has manager privileges.
     *
     * @param manager {@code true} if manager; {@code false} otherwise.
     */
    public void setManager(boolean manager) {
      isManager = manager;
    }

    /**
     * Returns the time the user profile was created.
     *
     * @return creation timestamp.
     */
    public Instant getTimeCreated() {
      return timeCreated;
    }

    /**
     * Sets the time the user profile was created.
     *
     * @param timeCreated creation timestamp.
     */
    public void setTimeCreated(Instant timeCreated) {
      this.timeCreated = timeCreated;
    }

    /**
     * Returns whether the user account is enabled.
     *
     * @return {@code true} if enabled; {@code false} otherwise.
     */
    public boolean isUserEnabled() {
      return userEnabled;
    }

    /**
     * Sets whether the user account is enabled.
     *
     * @param userEnabled {@code true} if enabled; {@code false} otherwise.
     */
    public void setUserEnabled(boolean userEnabled) {
      this.userEnabled = userEnabled;
    }

    /**
     * Creates a user profile DTO from an entity.
     *
     * @param userProfile source entity.
     * @return populated DTO, or {@code null} if {@code userProfile} is {@code null}.
     */
    public static UserProfileResponse fromEntity(UserProfile userProfile) {
      if (userProfile == null) {
        return null;
      }
      UserProfileResponse dto = new UserProfileResponse();
      dto.setExternalId(userProfile.getExternalId());
      dto.setDisplayName(userProfile.getDisplayName());
      dto.setEmail(userProfile.getEmail());
      dto.setAvatar(userProfile.getAvatar());
      dto.setManager(userProfile.isManager());
      dto.setTimeCreated(userProfile.getTimeCreated());
      dto.setUserEnabled(userProfile.getUserEnabled());
      return dto;
    }

  }

  /**
   * Nested DTO exposing a subset of {@link ReportLocation} fields for manager views.
   */
  public static class ReportLocationResponse {

    private Double latitude;
    private Double longitude;
    private String streetCoordinate;
    private String locationDescription;

    /**
     * Returns the location latitude.
     *
     * @return latitude.
     */
    public Double getLatitude() {
      return latitude;
    }

    /**
     * Sets the location latitude.
     *
     * @param latitude latitude.
     */
    public void setLatitude(Double latitude) {
      this.latitude = latitude;
    }

    /**
     * Returns the location longitude.
     *
     * @return longitude.
     */
    public Double getLongitude() {
      return longitude;
    }

    /**
     * Sets the location longitude.
     *
     * @param longitude longitude.
     */
    public void setLongitude(Double longitude) {
      this.longitude = longitude;
    }

    /**
     * Returns the street coordinate for the location, if provided.
     *
     * @return street coordinate.
     */
    public String getStreetCoordinate() {
      return streetCoordinate;
    }

    /**
     * Sets the street coordinate for the location.
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
     * Creates a location DTO from an entity.
     *
     * @param reportLocation source entity.
     * @return populated DTO, or {@code null} if {@code reportLocation} is {@code null}.
     */
    public static ReportLocationResponse fromEntity(ReportLocation reportLocation) {
      if (reportLocation == null) {
        return null;
      }
      ReportLocationResponse dto = new ReportLocationResponse();
      dto.setLatitude(reportLocation.getLatitude());
      dto.setLongitude(reportLocation.getLongitude());
      dto.setStreetCoordinate(reportLocation.getStreetCoordinate());
      dto.setLocationDescription(reportLocation.getLocationDescription());
      return dto;
    }

  }

  /**
   * Nested DTO exposing a subset of {@link AcceptedState} fields for manager views.
   */
  public static class AcceptedStateResponse {

    private String statusTag;
    private String statusTagDescription;

    /**
     * Returns the accepted-state status tag.
     *
     * @return status tag.
     */
    public String getStatusTag() {
      return statusTag;
    }

    /**
     * Sets the accepted-state status tag.
     *
     * @param statusTag status tag.
     */
    public void setStatusTag(String statusTag) {
      this.statusTag = statusTag;
    }

    /**
     * Returns the accepted-state description.
     *
     * @return description.
     */
    public String getStatusTagDescription() {
      return statusTagDescription;
    }

    /**
     * Sets the accepted-state description.
     *
     * @param statusTagDescription description.
     */
    public void setStatusTagDescription(String statusTagDescription) {
      this.statusTagDescription = statusTagDescription;
    }

    /**
     * Creates an accepted-state DTO from an entity.
     *
     * @param acceptedState source entity.
     * @return populated DTO, or {@code null} if {@code acceptedState} is {@code null}.
     */
    public static AcceptedStateResponse fromEntity(AcceptedState acceptedState) {
      if (acceptedState == null) {
        return null;
      }
      AcceptedStateResponse dto = new AcceptedStateResponse();
      dto.setStatusTag(acceptedState.getStatusTag());
      dto.setStatusTagDescription(acceptedState.getStatusTagDescription());
      return dto;
    }

  }

  /**
   * Nested DTO exposing a subset of {@link IssueType} fields for manager views.
   */
  public static class IssueTypeResponse {

    private String issueTypeTag;
    private String issueTypeDescription;

    /**
     * Returns the issue type tag.
     *
     * @return issue type tag.
     */
    public String getIssueTypeTag() {
      return issueTypeTag;
    }

    /**
     * Sets the issue type tag.
     *
     * @param issueTypeTag issue type tag.
     */
    public void setIssueTypeTag(String issueTypeTag) {
      this.issueTypeTag = issueTypeTag;
    }

    /**
     * Returns the issue type description.
     *
     * @return issue type description.
     */
    public String getIssueTypeDescription() {
      return issueTypeDescription;
    }

    /**
     * Sets the issue type description.
     *
     * @param issueTypeDescription issue type description.
     */
    public void setIssueTypeDescription(String issueTypeDescription) {
      this.issueTypeDescription = issueTypeDescription;
    }

    /**
     * Creates an issue type DTO from an entity.
     *
     * @param issueType source entity.
     * @return populated DTO, or {@code null} if {@code issueType} is {@code null}.
     */
    public static IssueTypeResponse fromEntity(IssueType issueType) {
      if (issueType == null) {
        return null;
      }
      IssueTypeResponse dto = new IssueTypeResponse();
      dto.setIssueTypeTag(issueType.getIssueTypeTag());
      dto.setIssueTypeDescription(issueType.getIssueTypeDescription());
      return dto;
    }

  }

  /**
   * Nested DTO exposing a subset of {@link ReportImage} fields for manager views.
   */
  public static class ReportImageResponse {

    private URI imageLocator;
    private String filename;
    private String mimeType;
    private int albumOrder;

    /**
     * Returns the API-facing URI locator for the report image content.
     *
     * @return image locator.
     */
    public URI getImageLocator() {
      return imageLocator;
    }

    /**
     * Sets the API-facing URI locator for the report image content.
     *
     * @param imageLocator image locator.
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
     * Creates a report image DTO from an entity.
     *
     * @param reportExternalId external identifier of the parent report (used to build an API locator).
     * @param reportImage source entity.
     * @return populated DTO, or {@code null} if {@code reportImage} is {@code null}.
     */
    public static ReportImageResponse fromEntity(UUID reportExternalId, ReportImage reportImage) {
      if (reportImage == null) {
        return null;
      }
      ReportImageResponse dto = new ReportImageResponse();
      // API-facing locator: canonical binary content endpoint.
      if (reportExternalId != null && reportImage.getExternalId() != null) {
        dto.setImageLocator(URI.create("/issue-reports/" + reportExternalId + "/images/" + reportImage.getExternalId()));
      }
      dto.setFilename(reportImage.getFilename());
      dto.setMimeType(reportImage.getMimeType());
      dto.setAlbumOrder(reportImage.getAlbumOrder());
      return dto;
    }

  }

}

