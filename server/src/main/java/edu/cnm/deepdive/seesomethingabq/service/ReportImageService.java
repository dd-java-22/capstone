package edu.cnm.deepdive.seesomethingabq.service;

import edu.cnm.deepdive.seesomethingabq.model.dto.AddImageRequest;
import edu.cnm.deepdive.seesomethingabq.model.entity.ReportImage;
import java.io.IOException;
import java.util.UUID;
import org.springframework.core.io.Resource;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service interface for report image business logic operations.
 */
public interface ReportImageService {

  /**
   * Retrieves a specific image from an issue report.
   *
   * @param externalKey The external ID (UUID) of the issue report.
   * @param imageId     The external ID (UUID) of the image.
   * @return The requested report image.
   */
  ReportImage getImage(UUID externalKey, UUID imageId);

  /**
   * Adds a new image to an issue report.
   *
   * @param externalKey The external ID (UUID) of the issue report.
   * @param request     The image data to add.
   * @return The newly created report image.
   */
  ReportImage addImage(UUID externalKey, AddImageRequest request);

  /**
   * Uploads an image file and creates a corresponding {@link ReportImage} metadata entry.
   * <p>
   * The uploaded file is validated, stored using the configured {@code StorageService}, and the
   * generated storage key is saved as the image locator. Only the report owner may upload images.
   * </p>
   *
   * @param externalKey The external ID (UUID) of the issue report.
   * @param file        The uploaded image file.
   * @return The newly created {@link ReportImage} metadata.
   * @throws IOException            If an I/O error occurs while storing the file.
   * @throws HttpMediaTypeException If the uploaded file's MIME type is not allowed.
   */
  ReportImage uploadImage(UUID externalKey, MultipartFile file)
      throws IOException, HttpMediaTypeException;

  /**
   * Retrieves the raw image file associated with a stored image.
   * <p>
   * This method is used by the controller to serve the actual binary image data to the client.
   * The returned {@link Resource} may represent a file on disk or another storage backend.
   * </p>
   *
   * @param key The storage key (typically the generated filename) used to locate the file.
   * @return A {@link Resource} representing the stored image file.
   * @throws IOException If the file cannot be retrieved from storage.
   */
  Resource getImageFile(String key) throws IOException;

  /**
   * Deletes an image belonging to an issue report.
   * <p>
   * This operation removes both the metadata entry and the underlying stored file. Only the report
   * owner or a manager may delete images.
   * </p>
   *
   * @param externalKey The external ID (UUID) of the issue report.
   * @param imageId     The external ID (UUID) of the image to delete.
   * @throws IOException If an error occurs while deleting the stored file.
   */
  void deleteImage(UUID externalKey, UUID imageId) throws IOException;

}
