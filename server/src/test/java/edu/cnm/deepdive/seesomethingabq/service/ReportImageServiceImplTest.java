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
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link ReportImageServiceImpl}. Uses Mockito to isolate the service
 * from repositories, storage, and user context.
 */
class ReportImageServiceImplTest {

  private ReportImageRepository reportImageRepository;
  private IssueReportRepository issueReportRepository;
  private UserService userService;
  private StorageService storageService;

  private ReportImageServiceImpl service;

  private UserProfile owner;
  private UserProfile manager;
  private IssueReport report;
  private ReportImage image;

  private final UUID reportExternalId = UUID.randomUUID();
  private final UUID imageExternalId = UUID.randomUUID();

  @BeforeEach
  void setUp() {
    reportImageRepository = mock(ReportImageRepository.class);
    issueReportRepository = mock(IssueReportRepository.class);
    userService = mock(UserService.class);
    storageService = mock(StorageService.class);

    service = new ReportImageServiceImpl(
        reportImageRepository,
        issueReportRepository,
        userService,
        storageService
    );

    owner = mock(UserProfile.class);
    when(owner.getId()).thenReturn(1L);
    when(owner.isManager()).thenReturn(false);

    manager = mock(UserProfile.class);
    when(manager.getId()).thenReturn(2L);
    when(manager.isManager()).thenReturn(true);

    report = mock(IssueReport.class);
    when(report.getUserProfile()).thenReturn(owner);
    when(report.getReportImages()).thenReturn(List.of());

    image = mock(ReportImage.class);
    when(image.getExternalId()).thenReturn(imageExternalId);
    when(image.getMimeType()).thenReturn("image/jpeg");
    when(image.getImageLocator()).thenReturn(URI.create("file:abc123.jpg"));
  }

  // ---------------------------------------------------------
  // getImage
  // ---------------------------------------------------------

  @Test
  void getImage_ownerAllowed() {
    when(userService.getCurrentUser()).thenReturn(owner);
    when(issueReportRepository.findByExternalId(reportExternalId))
        .thenReturn(Optional.of(report));
    when(reportImageRepository.findByIssueReportAndExternalId(report, imageExternalId))
        .thenReturn(Optional.of(image));

    ReportImage result = service.getImage(reportExternalId, imageExternalId);

    assertEquals(image, result);
  }

  @Test
  void getImage_managerAllowed() {
    when(userService.getCurrentUser()).thenReturn(manager);
    when(issueReportRepository.findByExternalId(reportExternalId))
        .thenReturn(Optional.of(report));
    when(reportImageRepository.findByIssueReportAndExternalId(report, imageExternalId))
        .thenReturn(Optional.of(image));

    ReportImage result = service.getImage(reportExternalId, imageExternalId);

    assertEquals(image, result);
  }

  @Test
  void getImage_unauthorizedUser_throws() {
    UserProfile stranger = mock(UserProfile.class);
    when(stranger.getId()).thenReturn(99L);
    when(stranger.isManager()).thenReturn(false);

    when(userService.getCurrentUser()).thenReturn(stranger);
    when(issueReportRepository.findByExternalId(reportExternalId))
        .thenReturn(Optional.of(report));

    assertThrows(AccessDeniedException.class,
        () -> service.getImage(reportExternalId, imageExternalId));
  }

  @Test
  void getImage_notFound_throws() {
    when(userService.getCurrentUser()).thenReturn(owner);
    when(issueReportRepository.findByExternalId(reportExternalId))
        .thenReturn(Optional.of(report));
    when(reportImageRepository.findByIssueReportAndExternalId(report, imageExternalId))
        .thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class,
        () -> service.getImage(reportExternalId, imageExternalId));
  }

  // ---------------------------------------------------------
  // addImage
  // ---------------------------------------------------------

  @Test
  void addImage_success() {
    AddImageRequest request = new AddImageRequest();
    request.setFilename("photo.jpg");
    request.setMimeType("image/jpeg");
    request.setAlbumOrder(0);
    request.setImageLocator(URI.create("file:abc123.jpg"));

    when(userService.getCurrentUser()).thenReturn(owner);
    when(issueReportRepository.findByExternalId(reportExternalId))
        .thenReturn(Optional.of(report));
    when(reportImageRepository.save(ArgumentMatchers.any())).thenReturn(image);

    ReportImage result = service.addImage(reportExternalId, request);

    assertNotNull(result);
  }

  // ---------------------------------------------------------
  // uploadImage
  // ---------------------------------------------------------

  @Test
  void uploadImage_success() throws IOException, HttpMediaTypeException {
    MultipartFile file = mock(MultipartFile.class);
    when(file.getOriginalFilename()).thenReturn("photo.jpg");
    when(file.getContentType()).thenReturn("image/jpeg");

    when(userService.getCurrentUser()).thenReturn(owner);
    when(issueReportRepository.findByExternalId(reportExternalId))
        .thenReturn(Optional.of(report));
    when(storageService.store(file)).thenReturn("abc123.jpg");
    when(reportImageRepository.save(ArgumentMatchers.any())).thenReturn(image);

    ReportImage result = service.uploadImage(reportExternalId, file);

    assertNotNull(result);
  }

  // ---------------------------------------------------------
  // getImageFile
  // ---------------------------------------------------------

  @Test
  void getImageFile_success() throws IOException {
    Resource resource = new ByteArrayResource("test".getBytes());
    when(storageService.retrieve("abc123.jpg")).thenReturn(resource);

    Resource result = service.getImageFile("abc123.jpg");

    assertEquals(resource, result);
  }

  // ---------------------------------------------------------
  // deleteImage
  // ---------------------------------------------------------

  @Test
  void deleteImage_success() throws IOException {
    when(userService.getCurrentUser()).thenReturn(owner);
    when(issueReportRepository.findByExternalId(reportExternalId))
        .thenReturn(Optional.of(report));
    when(reportImageRepository.findByIssueReportAndExternalId(report, imageExternalId))
        .thenReturn(Optional.of(image));

    service.deleteImage(reportExternalId, imageExternalId);

    verify(storageService).delete("abc123.jpg");
    verify(reportImageRepository).delete(image);
  }

  @Test
  void deleteImage_unauthorized_throws() {
    UserProfile stranger = mock(UserProfile.class);
    when(stranger.getId()).thenReturn(99L);
    when(stranger.isManager()).thenReturn(false);

    when(userService.getCurrentUser()).thenReturn(stranger);
    when(issueReportRepository.findByExternalId(reportExternalId))
        .thenReturn(Optional.of(report));

    assertThrows(AccessDeniedException.class,
        () -> service.deleteImage(reportExternalId, imageExternalId));
  }
}
