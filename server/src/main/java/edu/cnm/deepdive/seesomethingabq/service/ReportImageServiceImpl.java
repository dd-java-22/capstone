package edu.cnm.deepdive.seesomethingabq.service;

import edu.cnm.deepdive.seesomethingabq.exception.AccessDeniedException;
import edu.cnm.deepdive.seesomethingabq.exception.ResourceNotFoundException;
import edu.cnm.deepdive.seesomethingabq.model.dto.AddImageRequest;
import edu.cnm.deepdive.seesomethingabq.model.entity.IssueReport;
import edu.cnm.deepdive.seesomethingabq.model.entity.ReportImage;
import edu.cnm.deepdive.seesomethingabq.model.entity.UserProfile;
import edu.cnm.deepdive.seesomethingabq.service.repository.IssueReportRepository;
import edu.cnm.deepdive.seesomethingabq.service.repository.ReportImageRepository;
import edu.cnm.deepdive.seesomethingabq.service.storage.StorageService;
import java.io.IOException;
import java.net.URI;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of {@link ReportImageService} for managing report images. This service handles
 * access control, metadata persistence, file storage integration, and retrieval of image data.
 */
@Service
public class ReportImageServiceImpl implements ReportImageService {

  private final ReportImageRepository reportImageRepository;
  private final IssueReportRepository issueReportRepository;
  private final UserService userService;
  private final StorageService storageService;

  /**
   * Constructs an instance of {@code ReportImageServiceImpl} with the required repositories,
   * user service, and storage service.
   *
   * @param reportImageRepository Repository for {@link ReportImage} entities.
   * @param issueReportRepository Repository for {@link IssueReport} entities.
   * @param userService           Service for retrieving the current authenticated user.
   * @param storageService        Service for storing and retrieving image files.
   */
  @Autowired
  public ReportImageServiceImpl(ReportImageRepository reportImageRepository,
      IssueReportRepository issueReportRepository, UserService userService,
      StorageService storageService) {
    this.reportImageRepository = reportImageRepository;
    this.issueReportRepository = issueReportRepository;
    this.userService = userService;
    this.storageService = storageService;
  }

  @Override
  public ReportImage getImage(UUID externalId, UUID imageId) {
    UserProfile currentUser = userService.getCurrentUser();
    IssueReport report = issueReportRepository.findByExternalId(externalId)
        .orElseThrow(() -> new ResourceNotFoundException("Issue report not found"));

    // Check access: user must own the report or be a manager
    if (!report.getUserProfile().getId().equals(currentUser.getId()) && !currentUser.isManager()) {
      throw new AccessDeniedException("You do not have permission to view this image");
    }

    return reportImageRepository.findByIssueReportAndExternalId(report, imageId)
        .orElseThrow(() -> new ResourceNotFoundException("Image not found"));
  }

  @Override
  public ReportImage addImage(UUID externalId, AddImageRequest request) {
    UserProfile currentUser = userService.getCurrentUser();
    IssueReport report = issueReportRepository.findByExternalId(externalId)
        .orElseThrow(() -> new ResourceNotFoundException("Issue report not found"));

    // Check access: user must own the report
    if (!report.getUserProfile().getId().equals(currentUser.getId())) {
      throw new AccessDeniedException("You can only add images to your own reports");
    }

    // Create and populate the new image
    ReportImage image = new ReportImage();
    image.setIssueReport(report);
    image.setImageLocator(request.getImageLocator());
    image.setFilename(request.getFilename());
    image.setMimeType(request.getMimeType());
    image.setAlbumOrder(request.getAlbumOrder());

    return reportImageRepository.save(image);
  }

  /**
   * Uploads an image file and creates a corresponding {@link ReportImage} metadata entry.
   * <p>
   * This method performs the following steps:
   * <ol>
   *   <li>Validates that the current user owns the associated issue report.</li>
   *   <li>Stores the uploaded file using the configured {@link StorageService}.</li>
   *   <li>Creates and persists a {@link ReportImage} entity containing metadata such as filename,
   *       MIME type, and storage key.</li>
   * </ol>
   *
   * @param externalKey The external ID (UUID) of the issue report.
   * @param file        The uploaded image file.
   * @return The newly created {@link ReportImage} metadata.
   * @throws IOException            If an I/O error occurs while storing the file.
   * @throws HttpMediaTypeException If the uploaded file's MIME type is not allowed.
   */
  @Override
  public ReportImage uploadImage(UUID externalKey, MultipartFile file)
      throws IOException, HttpMediaTypeException {

    if (file == null || file.isEmpty()) {
      throw new IllegalArgumentException("Upload file must not be empty.");
    }

    UserProfile currentUser = userService.getCurrentUser();
    IssueReport report = getReportOrThrow(externalKey);

    if (!report.getUserProfile().getId().equals(currentUser.getId())) {
      throw new AccessDeniedException("You can only add images to your own reports");
    }

    // Store the file and obtain the generated storage key
    String storageKey = storageService.store(file);

    // Create metadata entry
    ReportImage image = new ReportImage();
    image.setIssueReport(report);
    image.setFilename(file.getOriginalFilename());
    image.setMimeType(file.getContentType());
    // Store an internal locator; do not expose filesystem paths to clients.
    image.setImageLocator(URI.create("stored:" + storageKey));
    image.setAlbumOrder(report.getReportImages().size());

    return reportImageRepository.save(image);
  }

  /**
   * Retrieves the raw image file associated with the given storage key.
   * <p>
   * This method delegates to the {@link StorageService} to load the file as a Spring
   * {@link Resource}, which can then be streamed directly to the client.
   * </p>
   *
   * @param key The storage key (typically the generated filename).
   * @return A {@link Resource} representing the stored image file.
   * @throws IOException If the file cannot be retrieved.
   */
  @Override
  public Resource getImageFile(String key) throws IOException {
    return storageService.retrieve(key);
  }

  /**
   * Deletes an image belonging to an issue report. This operation removes both the metadata entry
   * and the underlying stored file.
   * <p>
   * The method performs the following steps:
   * <ol>
   *   <li>Validates that the current user has permission to delete the image.</li>
   *   <li>Retrieves the associated {@link ReportImage} entity.</li>
   *   <li>Extracts the storage key from the image locator URI.</li>
   *   <li>Deletes the file from storage.</li>
   *   <li>Deletes the metadata entry from the database.</li>
   * </ol>
   *
   * @param externalKey The external ID (UUID) of the issue report.
   * @param imageId     The external ID (UUID) of the image to delete.
   * @throws IOException If an error occurs while deleting the stored file.
   */
  @Override
  @Transactional
  public void deleteImage(UUID externalKey, UUID imageId) throws IOException {
    UserProfile currentUser = userService.getCurrentUser();
    IssueReport report = getReportOrThrow(externalKey);

    // Access control
    if (!report.getUserProfile().getId().equals(currentUser.getId()) && !currentUser.isManager()) {
      throw new AccessDeniedException("You do not have permission to delete this image");
    }

    ReportImage image = reportImageRepository
        .findByIssueReportAndExternalId(report, imageId)
        .orElseThrow(() -> new ResourceNotFoundException("Image not found"));

    // Extract the storage key from the URI
    String key = image.getImageLocator().getSchemeSpecificPart();

    // Delete the file from storage
    storageService.delete(key);

    // Delete metadata
    reportImageRepository.delete(image);
  }

  /**
   * Retrieves an {@link IssueReport} by its external ID or throws a
   * {@link ResourceNotFoundException} if it does not exist.
   *
   * @param externalKey The external ID (UUID) of the issue report.
   * @return The corresponding {@link IssueReport}.
   * @throws ResourceNotFoundException If no report exists with the given ID.
   */
  private IssueReport getReportOrThrow(UUID externalKey) {
    return issueReportRepository.findByExternalId(externalKey)
        .orElseThrow(() -> new ResourceNotFoundException("Issue report not found"));
  }
}
