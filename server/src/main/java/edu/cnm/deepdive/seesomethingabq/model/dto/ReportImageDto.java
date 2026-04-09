package edu.cnm.deepdive.seesomethingabq.model.dto;

import edu.cnm.deepdive.seesomethingabq.model.entity.ReportImage;
import java.util.UUID;

public record ReportImageDto(
    UUID externalId,
    String filename,
    String mimeType,
    int albumOrder
) {

  public static ReportImageDto from(ReportImage entity) {
    return new ReportImageDto(
        entity.getExternalId(),
        entity.getFilename(),
        entity.getMimeType(),
        entity.getAlbumOrder()
    );
  }
}
