package edu.cnm.deepdive.seesomethingabq.controller;

import edu.cnm.deepdive.seesomethingabq.model.dto.AddImageRequest;
import edu.cnm.deepdive.seesomethingabq.model.entity.ReportImage;
import edu.cnm.deepdive.seesomethingabq.service.ReportImageService;
import java.io.IOException;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST controller for managing report images. Provides endpoints for retrieving, uploading,
 * serving, and deleting images associated with issue reports. Access control is enforced at the
 * service layer.
 */
@RestController
@RequestMapping("/issue-reports/{externalKey}/images")
public class ReportImageController {

  private final ReportImageService service;

  /**
   * Constructs an instance of {@code ReportImageController} with the specified service.
   *
   * @param service Report image service for business logic operations.
   */
  @Autowired
  public ReportImageController(ReportImageService service) {
    this.service = service;
  }

  /**
   * Retrieves metadata for a specific image belonging to an issue report. Access is restricted to
   * the report owner and managers.
   *
   * @param externalKey The external ID (UUID) of the issue report.
   * @param imageId     The external ID (UUID) of the image.
   * @return The requested {@link ReportImage} metadata.
   */
  @GetMapping(value = "/{imageId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ReportImage getImage(@PathVariable UUID externalKey, @PathVariable UUID imageId) {
    return service.getImage(externalKey, imageId);
  }

  /**
   * Adds a new image metadata entry to an issue report. This endpoint does not upload a file; it
   * only stores metadata. Only the report owner may add images.
   *
   * @param externalKey The external ID (UUID) of the issue report.
   * @param request     The image metadata to add.
   * @return The newly created {@link ReportImage}.
   */
  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public ReportImage addImage(@PathVariable UUID externalKey,
      @RequestBody AddImageRequest request) {
    return service.addImage(externalKey, request);
  }

  /**
   * Uploads an image file and creates a corresponding {@link ReportImage} metadata entry. The file
   * is stored using the configured {@code StorageService}, and the generated storage key is saved
   * as the image locator. Only the report owner may upload images.
   *
   * @param externalKey The external ID (UUID) of the issue report.
   * @param file        The uploaded image file.
   * @return The newly created {@link ReportImage} metadata.
   * @throws IOException             If an I/O error occurs while storing the file.
   * @throws HttpMediaTypeException  If the uploaded file's MIME type is not allowed.
   */
  @PostMapping(
      value = "/upload",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @ResponseStatus(HttpStatus.CREATED)
  public ReportImage uploadImage(
      @PathVariable UUID externalKey,
      @RequestPart("file") MultipartFile file
  ) throws IOException, HttpMediaTypeException {
    return service.uploadImage(externalKey, file);
  }

  /**
   * Serves the raw image file associated with a specific {@link ReportImage}. This endpoint returns
   * the actual binary image data, allowing the front-end to display the image. The MIME type is
   * dynamically set based on the stored metadata.
   *
   * <p>This endpoint is typically used by the UI to render images in the report details view.</p>
   *
   * @param externalKey The external ID (UUID) of the issue report.
   * @param imageId     The external ID (UUID) of the image.
   * @return A {@link ResponseEntity} containing the image file as a {@link Resource}.
   * @throws IOException If the file cannot be retrieved from storage.
   */
  @GetMapping(
      value = "/{imageId}/file",
      produces = MediaType.ALL_VALUE
  )
  public ResponseEntity<Resource> getImageFile(
      @PathVariable UUID externalKey,
      @PathVariable UUID imageId
  ) throws IOException {

    ReportImage image = service.getImage(externalKey, imageId);
    String key = image.getImageLocator().getSchemeSpecificPart();
    Resource resource = service.getImageFile(key);

    return ResponseEntity
        .ok()
        .contentType(MediaType.parseMediaType(image.getMimeType()))
        .body(resource);
  }

  /**
   * Deletes an image belonging to an issue report. This operation removes both the metadata entry
   * and the underlying stored file. Only the report owner or a manager may delete images.
   *
   * @param externalKey The external ID (UUID) of the issue report.
   * @param imageId     The external ID (UUID) of the image to delete.
   * @throws IOException If an error occurs while deleting the stored file.
   */
  @DeleteMapping("/{imageId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteImage(@PathVariable UUID externalKey, @PathVariable UUID imageId)
      throws IOException {
    service.deleteImage(externalKey, imageId);
  }
}
