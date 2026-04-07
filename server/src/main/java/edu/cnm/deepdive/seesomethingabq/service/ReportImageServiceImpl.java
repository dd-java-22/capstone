package edu.cnm.deepdive.seesomethingabq.service;

import edu.cnm.deepdive.seesomethingabq.exception.AccessDeniedException;
import edu.cnm.deepdive.seesomethingabq.exception.ResourceNotFoundException;
import edu.cnm.deepdive.seesomethingabq.model.dto.AddImageRequest;
import edu.cnm.deepdive.seesomethingabq.model.entity.IssueReport;
import edu.cnm.deepdive.seesomethingabq.model.entity.ReportImage;
import edu.cnm.deepdive.seesomethingabq.model.entity.UserProfile;
import edu.cnm.deepdive.seesomethingabq.service.repository.IssueReportRepository;
import edu.cnm.deepdive.seesomethingabq.service.repository.ReportImageRepository;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of {@link ReportImageService} for managing report images.
 */
@Service
public class ReportImageServiceImpl implements ReportImageService {

  private final ReportImageRepository reportImageRepository;
  private final IssueReportRepository issueReportRepository;
  private final UserService userService;

  @Autowired
  public ReportImageServiceImpl(ReportImageRepository reportImageRepository,
      IssueReportRepository issueReportRepository, UserService userService) {
    this.reportImageRepository = reportImageRepository;
    this.issueReportRepository = issueReportRepository;
    this.userService = userService;
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

  @Override
  @Transactional
  public void deleteImage(UUID externalId, UUID imageId) {

    // Load the image
    ReportImage image = reportImageRepository.findByExternalId(imageId)
        .orElseThrow(() -> new ResourceNotFoundException("Image not found"));

    IssueReport report = image.getIssueReport();

    // Validate that the image belongs to the specified report
    if (!report.getExternalId().equals(externalId)) {
      throw new ResourceNotFoundException("Image not found for this report");
    }

    UserProfile currentUser = userService.getCurrentUser();
    boolean isOwner = report.getUserProfile().getId().equals(currentUser.getId());
    boolean isManager = currentUser.isManager();

    // Authorization check
    if (!isOwner && !isManager) {
      throw new AccessDeniedException("You do not have permission to delete this image");
    }

    // Bidirectional unlink
    report.getReportImages().remove(image);
    image.setIssueReport(null);

    // Delete metadata
    reportImageRepository.delete(image);

    // TODO: Delete actual file from storage once implemented
  }
}
