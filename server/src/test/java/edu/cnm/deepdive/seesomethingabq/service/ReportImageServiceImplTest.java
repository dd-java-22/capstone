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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link ReportImageServiceImpl}. Uses Mockito to isolate the service
 * from repositories, storage, and user context.
 */
class ReportImageServiceImplTest {

  @Mock
  private ReportImageRepository reportImageRepository;

  @Mock
  private IssueReportRepository issueReportRepository;

  @Mock
  private UserService userService;

  @Mock
  private StorageService storageService;

  @InjectMocks
  private ReportImageServiceImpl service;

  private UserProfile owner;
  private UserProfile manager;
  private IssueReport report;
  private ReportImage image;

  private final UUID reportExternalId = UUID.randomUUID();
  private final UUID imageExternalId = UUID.randomUUID();

  private final UUID reportId = UUID.randomUUID();
  private final UUID imageId = UUID.randomUUID();

  @BeforeEach
  void setup() throws Exception {
    MockitoAnnotations.openMocks(this);

    service = new ReportImageServiceImpl(
        reportImageRepository,
        issueReportRepository,
        userService,
        storageService
    );

    reportImageRepository = mock(ReportImageRepository.class);
    issueReportRepository = mock(IssueReportRepository.class);
    userService = mock(UserService.class);
    storageService = mock(StorageService.class);

    // Create owner
    owner = new UserProfile();
    setField(owner, "id", 1L);
    setField(owner, "externalId", UUID.randomUUID());
    owner.setIsManager(false);

    // Create manager
    manager = new UserProfile();
    setField(manager, "id", 2L);
    setField(manager, "externalId", UUID.randomUUID());
    manager.setIsManager(true);

    owner = mock(UserProfile.class);
    when(owner.getId()).thenReturn(1L);
    when(owner.isManager()).thenReturn(false);

    // Create report
    report = new IssueReport();
    setField(report, "externalId", reportId);
    setField(report, "userProfile", owner);

    manager = mock(UserProfile.class);
    when(manager.getId()).thenReturn(2L);
    when(manager.isManager()).thenReturn(true);

    // Create image
    image = new ReportImage();
    setField(image, "externalId", imageId);
    image.setIssueReport(report);
    image.setImageLocator(URI.create("file://test.jpg"));
    image.setFilename("test.jpg");
    image.setMimeType("image/jpeg");

    report = mock(IssueReport.class);
    when(report.getUserProfile()).thenReturn(owner);
    when(report.getReportImages()).thenReturn(List.of());

    image = mock(ReportImage.class);
    when(image.getExternalId()).thenReturn(imageExternalId);
    when(image.getMimeType()).thenReturn("image/jpeg");
    when(image.getImageLocator()).thenReturn(URI.create("file:abc123.jpg"));
  }

  // Helper to set private fields via reflection
  private void setField(Object target, String fieldName, Object value) throws Exception {
    var field = target.getClass().getDeclaredField(fieldName);
    field.setAccessible(true);
    field.set(target, value);
  }

//  @Test
//  void deleteImage_ownerCanDelete() {
//    when(reportImageRepository.findByExternalId(imageId)).thenReturn(Optional.of(image));
//    when(userService.getCurrentUser()).thenReturn(owner);
//
//    service.deleteImage(reportId, imageId);
//
//    verify(reportImageRepository).delete(image);
//    assertNull(image.getIssueReport());
//  }
//
//  @Test
//  void deleteImage_managerCanDelete() {
//    when(reportImageRepository.findByExternalId(imageId)).thenReturn(Optional.of(image));
//    when(userService.getCurrentUser()).thenReturn(manager);
//
//    service.deleteImage(reportId, imageId);
//
//    verify(reportImageRepository).delete(image);
//  }
//
//  @Test
//  void deleteImage_wrongReport_throwsNotFound() {
//    when(reportImageRepository.findByExternalId(imageId)).thenReturn(Optional.of(image));
//    UUID wrongReportId = UUID.randomUUID();
//
//    assertThrows(ResourceNotFoundException.class,
//        () -> service.deleteImage(wrongReportId, imageId));
//  }
//
//  @Test
//  void deleteImage_notOwner_throwsAccessDenied() {
//    UserProfile stranger = new UserProfile();
//    try {
//      setField(stranger, "id", 99L);
//      setField(stranger, "externalId", UUID.randomUUID());
//    } catch (Exception ignored) {}
//
//    when(reportImageRepository.findByExternalId(imageId)).thenReturn(Optional.of(image));
//    when(userService.getCurrentUser()).thenReturn(stranger);
//
//    assertThrows(AccessDeniedException.class,
//        () -> service.deleteImage(reportId, imageId));
//  }

  @Test
  void addImage_ownerCanAdd() {
    AddImageRequest request = new AddImageRequest();
    request.setFilename("new.jpg");
    request.setMimeType("image/jpeg");
    request.setImageLocator(URI.create("file://new.jpg"));
    request.setAlbumOrder(1);

    when(issueReportRepository.findByExternalId(reportId)).thenReturn(Optional.of(report));
    when(userService.getCurrentUser()).thenReturn(owner);
    when(reportImageRepository.save(any())).thenReturn(image);

    ReportImage result = service.addImage(reportId, request);

    assertEquals("test.jpg", result.getFilename());
  }

  @Test
  void addImage_notOwner_throwsAccessDenied() {
    UserProfile stranger = new UserProfile();
    try {
      setField(stranger, "id", 99L);
      setField(stranger, "externalId", UUID.randomUUID());
    } catch (Exception ignored) {}

    AddImageRequest request = new AddImageRequest();

    when(issueReportRepository.findByExternalId(reportId)).thenReturn(Optional.of(report));
    when(userService.getCurrentUser()).thenReturn(stranger);

    assertThrows(AccessDeniedException.class,
        () -> service.addImage(reportId, request));
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
