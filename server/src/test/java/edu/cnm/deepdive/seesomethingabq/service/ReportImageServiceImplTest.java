package edu.cnm.deepdive.seesomethingabq.service;

import edu.cnm.deepdive.seesomethingabq.exception.AccessDeniedException;
import edu.cnm.deepdive.seesomethingabq.exception.ResourceNotFoundException;
import edu.cnm.deepdive.seesomethingabq.model.dto.AddImageRequest;
import edu.cnm.deepdive.seesomethingabq.model.entity.IssueReport;
import edu.cnm.deepdive.seesomethingabq.model.entity.ReportImage;
import edu.cnm.deepdive.seesomethingabq.model.entity.UserProfile;
import edu.cnm.deepdive.seesomethingabq.service.repository.IssueReportRepository;
import edu.cnm.deepdive.seesomethingabq.service.repository.ReportImageRepository;
import java.net.URI;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReportImageServiceImplTest {

  @Mock
  private ReportImageRepository reportImageRepository;

  @Mock
  private IssueReportRepository issueReportRepository;

  @Mock
  private UserService userService;

  @InjectMocks
  private ReportImageServiceImpl service;

  private UserProfile owner;
  private UserProfile manager;
  private IssueReport report;
  private ReportImage image;

  private final UUID reportId = UUID.randomUUID();
  private final UUID imageId = UUID.randomUUID();

  @BeforeEach
  void setup() throws Exception {
    MockitoAnnotations.openMocks(this);

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

    // Create report
    report = new IssueReport();
    setField(report, "externalId", reportId);
    setField(report, "userProfile", owner);

    // Create image
    image = new ReportImage();
    setField(image, "externalId", imageId);
    image.setIssueReport(report);
    image.setImageLocator(URI.create("file://test.jpg"));
    image.setFilename("test.jpg");
    image.setMimeType("image/jpeg");
  }

  // Helper to set private fields via reflection
  private void setField(Object target, String fieldName, Object value) throws Exception {
    var field = target.getClass().getDeclaredField(fieldName);
    field.setAccessible(true);
    field.set(target, value);
  }

  @Test
  void deleteImage_ownerCanDelete() {
    when(reportImageRepository.findByExternalId(imageId)).thenReturn(Optional.of(image));
    when(userService.getCurrentUser()).thenReturn(owner);

    service.deleteImage(reportId, imageId);

    verify(reportImageRepository).delete(image);
    assertNull(image.getIssueReport());
  }

  @Test
  void deleteImage_managerCanDelete() {
    when(reportImageRepository.findByExternalId(imageId)).thenReturn(Optional.of(image));
    when(userService.getCurrentUser()).thenReturn(manager);

    service.deleteImage(reportId, imageId);

    verify(reportImageRepository).delete(image);
  }

  @Test
  void deleteImage_wrongReport_throwsNotFound() {
    when(reportImageRepository.findByExternalId(imageId)).thenReturn(Optional.of(image));
    UUID wrongReportId = UUID.randomUUID();

    assertThrows(ResourceNotFoundException.class,
        () -> service.deleteImage(wrongReportId, imageId));
  }

  @Test
  void deleteImage_notOwner_throwsAccessDenied() {
    UserProfile stranger = new UserProfile();
    try {
      setField(stranger, "id", 99L);
      setField(stranger, "externalId", UUID.randomUUID());
    } catch (Exception ignored) {}

    when(reportImageRepository.findByExternalId(imageId)).thenReturn(Optional.of(image));
    when(userService.getCurrentUser()).thenReturn(stranger);

    assertThrows(AccessDeniedException.class,
        () -> service.deleteImage(reportId, imageId));
  }

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
}
