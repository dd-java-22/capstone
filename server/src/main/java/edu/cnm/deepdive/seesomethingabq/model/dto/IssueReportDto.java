package edu.cnm.deepdive.seesomethingabq.model.dto;

import edu.cnm.deepdive.seesomethingabq.model.entity.IssueReport;
import edu.cnm.deepdive.seesomethingabq.model.entity.IssueType;
import java.util.List;
import java.util.UUID;

public record IssueReportDto(
    UUID externalId,
    String textDescription,
    String acceptedState,
    double latitude,
    double longitude,
    String streetCoordinate,
    String locationDescription,
    List<String> issueTypes,
    List<ReportImageDto> reportImages
) {

  public static IssueReportDto from(IssueReport entity) {
    double latitude = 0.0;
    double longitude = 0.0;
    String streetCoordinate = null;
    String locationDescription = null;
    if (entity.getReportLocation() != null) {
      if (entity.getReportLocation().getLatitude() != null) {
        latitude = entity.getReportLocation().getLatitude();
      }
      if (entity.getReportLocation().getLongitude() != null) {
        longitude = entity.getReportLocation().getLongitude();
      }
      streetCoordinate = entity.getReportLocation().getStreetCoordinate();
      locationDescription = entity.getReportLocation().getLocationDescription();
    }
    String acceptedState = (entity.getAcceptedState() != null)
        ? entity.getAcceptedState().getStatusTag()
        : null;
    return new IssueReportDto(
        entity.getExternalId(),
        entity.getTextDescription(),
        acceptedState,
        latitude,
        longitude,
        streetCoordinate,
        locationDescription,
        entity.getIssueTypes().stream()
            .map(IssueType::getIssueTypeTag)   // ⭐ FIXED HERE
            .toList(),
        entity.getReportImages().stream()
            .map(ReportImageDto::from)
            .toList()
    );
  }

}
