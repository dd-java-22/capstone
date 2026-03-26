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

  public UUID getExternalId() {
    return externalId;
  }

  public void setExternalId(UUID externalId) {
    this.externalId = externalId;
  }

  public UserProfileResponse getUserProfile() {
    return userProfile;
  }

  public void setUserProfile(UserProfileResponse userProfile) {
    this.userProfile = userProfile;
  }

  public ReportLocationResponse getReportLocation() {
    return reportLocation;
  }

  public void setReportLocation(ReportLocationResponse reportLocation) {
    this.reportLocation = reportLocation;
  }

  public AcceptedStateResponse getAcceptedState() {
    return acceptedState;
  }

  public void setAcceptedState(AcceptedStateResponse acceptedState) {
    this.acceptedState = acceptedState;
  }

  public List<IssueTypeResponse> getIssueTypes() {
    return issueTypes;
  }

  public void setIssueTypes(List<IssueTypeResponse> issueTypes) {
    this.issueTypes = issueTypes;
  }

  public Instant getTimeFirstReported() {
    return timeFirstReported;
  }

  public void setTimeFirstReported(Instant timeFirstReported) {
    this.timeFirstReported = timeFirstReported;
  }

  public Instant getTimeLastModified() {
    return timeLastModified;
  }

  public void setTimeLastModified(Instant timeLastModified) {
    this.timeLastModified = timeLastModified;
  }

  public String getTextDescription() {
    return textDescription;
  }

  public void setTextDescription(String textDescription) {
    this.textDescription = textDescription;
  }

  public List<ReportImageResponse> getReportImages() {
    return reportImages;
  }

  public void setReportImages(List<ReportImageResponse> reportImages) {
    this.reportImages = reportImages;
  }

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
            .map(ReportImageResponse::fromEntity)
            .toList()
    );
    return dto;
  }

  public static class UserProfileResponse {

    private UUID externalId;
    private String displayName;
    private String email;
    private URL avatar;
    private boolean isManager;
    private Instant timeCreated;
    private boolean userEnabled;

    public UUID getExternalId() {
      return externalId;
    }

    public void setExternalId(UUID externalId) {
      this.externalId = externalId;
    }

    public String getDisplayName() {
      return displayName;
    }

    public void setDisplayName(String displayName) {
      this.displayName = displayName;
    }

    public String getEmail() {
      return email;
    }

    public void setEmail(String email) {
      this.email = email;
    }

    public URL getAvatar() {
      return avatar;
    }

    public void setAvatar(URL avatar) {
      this.avatar = avatar;
    }

    public boolean isManager() {
      return isManager;
    }

    public void setManager(boolean manager) {
      isManager = manager;
    }

    public Instant getTimeCreated() {
      return timeCreated;
    }

    public void setTimeCreated(Instant timeCreated) {
      this.timeCreated = timeCreated;
    }

    public boolean isUserEnabled() {
      return userEnabled;
    }

    public void setUserEnabled(boolean userEnabled) {
      this.userEnabled = userEnabled;
    }

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

  public static class ReportLocationResponse {

    private Double latitude;
    private Double longitude;
    private String streetCoordinate;
    private String locationDescription;

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

  public static class AcceptedStateResponse {

    private String statusTag;
    private String statusTagDescription;

    public String getStatusTag() {
      return statusTag;
    }

    public void setStatusTag(String statusTag) {
      this.statusTag = statusTag;
    }

    public String getStatusTagDescription() {
      return statusTagDescription;
    }

    public void setStatusTagDescription(String statusTagDescription) {
      this.statusTagDescription = statusTagDescription;
    }

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

  public static class IssueTypeResponse {

    private String issueTypeTag;
    private String issueTypeDescription;

    public String getIssueTypeTag() {
      return issueTypeTag;
    }

    public void setIssueTypeTag(String issueTypeTag) {
      this.issueTypeTag = issueTypeTag;
    }

    public String getIssueTypeDescription() {
      return issueTypeDescription;
    }

    public void setIssueTypeDescription(String issueTypeDescription) {
      this.issueTypeDescription = issueTypeDescription;
    }

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

  public static class ReportImageResponse {

    private URI imageLocator;
    private String filename;
    private String mimeType;
    private int albumOrder;

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

    public static ReportImageResponse fromEntity(ReportImage reportImage) {
      if (reportImage == null) {
        return null;
      }
      ReportImageResponse dto = new ReportImageResponse();
      dto.setImageLocator(reportImage.getImageLocator());
      dto.setFilename(reportImage.getFilename());
      dto.setMimeType(reportImage.getMimeType());
      dto.setAlbumOrder(reportImage.getAlbumOrder());
      return dto;
    }

  }

}

