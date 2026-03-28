package edu.cnm.deepdive.seesomethingabq.service;

import edu.cnm.deepdive.seesomethingabq.model.dto.AddImageRequest;
import edu.cnm.deepdive.seesomethingabq.model.entity.ReportImage;
import java.util.UUID;

/**
 * Service interface for report image business logic operations.
 */
public interface ReportImageService {

  /**
   * Retrieves a specific image from an issue report.
   *
   * @param externalKey The external ID (UUID) of the issue report.
   * @param imageKey The external key (UUID) of the image.
   * @return The requested report image.
   */
  ReportImage getImage(UUID externalKey, UUID imageKey);

  /**
   * Adds a new image to an issue report.
   *
   * @param externalKey The external ID (UUID) of the issue report.
   * @param request The image data to add.
   * @return The newly created report image.
   */
  ReportImage addImage(UUID externalKey, AddImageRequest request);
}
