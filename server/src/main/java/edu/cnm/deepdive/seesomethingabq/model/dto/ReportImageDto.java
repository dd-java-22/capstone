package edu.cnm.deepdive.seesomethingabq.model.dto;

import edu.cnm.deepdive.seesomethingabq.model.entity.ReportImage;
import java.util.UUID;

/**
 * API DTO representing an image attached to an issue report.
 *
 * @param externalId image external identifier.
 * @param filename original filename.
 * @param mimeType image MIME type.
 * @param albumOrder album sort order within a report.
 */
public record ReportImageDto(
    UUID externalId,
    String filename,
    String mimeType,
    int albumOrder
) {

  /**
   * Creates a DTO view of the provided entity.
   *
   * @param entity source entity.
   * @return DTO populated from {@code entity}.
   */
  public static ReportImageDto from(ReportImage entity) {
    return new ReportImageDto(
        entity.getExternalId(),
        entity.getFilename(),
        entity.getMimeType(),
        entity.getAlbumOrder()
    );
  }
}
