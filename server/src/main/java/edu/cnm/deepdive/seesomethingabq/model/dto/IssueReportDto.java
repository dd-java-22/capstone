package edu.cnm.deepdive.seesomethingabq.model.dto;

import edu.cnm.deepdive.seesomethingabq.model.entity.IssueReport;
import edu.cnm.deepdive.seesomethingabq.model.entity.IssueType;
import java.util.List;
import java.util.UUID;

public record IssueReportDto(
    UUID externalId,
    String textDescription,
    double latitude,
    double longitude,
    String locationDescription,
    List<String> issueTypes,
    List<ReportImageDto> reportImages
) {

  public static IssueReportDto from(IssueReport entity) {
    return new IssueReportDto(
        entity.getExternalId(),
        entity.getTextDescription(),
        entity.getReportLocation().getLatitude(),
        entity.getReportLocation().getLongitude(),
        entity.getReportLocation().getLocationDescription(),
        entity.getIssueTypes().stream()
            .map(IssueType::getIssueTypeTag)   // ⭐ FIXED HERE
            .toList(),
        entity.getReportImages().stream()
            .map(ReportImageDto::from)
            .toList()
    );
  }

}
