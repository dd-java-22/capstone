package edu.cnm.deepdive.seesomethingabq.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.cnm.deepdive.seesomethingabq.exception.AccessDeniedException;
import edu.cnm.deepdive.seesomethingabq.exception.ResourceNotFoundException;
import edu.cnm.deepdive.seesomethingabq.model.entity.AcceptedState;
import edu.cnm.deepdive.seesomethingabq.model.entity.IssueReport;
import edu.cnm.deepdive.seesomethingabq.model.entity.ReportImage;
import edu.cnm.deepdive.seesomethingabq.model.entity.UserProfile;
import edu.cnm.deepdive.seesomethingabq.service.repository.IssueReportRepository;
import edu.cnm.deepdive.seesomethingabq.service.repository.ReportImageRepository;
import edu.cnm.deepdive.seesomethingabq.service.storage.StorageService;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class ReportImageServiceImplTest {

  private static final UUID REPORT_EXTERNAL_ID =
      UUID.fromString("11111111-1111-1111-1111-111111111111");
  private static final UUID IMAGE_EXTERNAL_ID =
      UUID.fromString("22222222-2222-2222-2222-222222222222");
  private static final String STORAGE_KEY = "abc123.jpg";

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

  @BeforeEach
  void setUp() {
    owner = userFixture(10L, false);
    manager = userFixture(11L, true);

    report = new IssueReport();
    setField(report, "externalId", REPORT_EXTERNAL_ID);
    report.setUserProfile(owner);
    report.setTextDescription("Broken street light");
    report.setAcceptedState(acceptedStateFixture("PENDING"));

    image = new ReportImage();
    setField(image, "externalId", IMAGE_EXTERNAL_ID);
    image.setIssueReport(report);
    image.setImageLocator(URI.create("stored:" + STORAGE_KEY));
    image.setFilename("photo.jpg");
    image.setMimeType("image/jpeg");
    image.setAlbumOrder(0);
  }

  @Test
  void getImage_ownerAllowed() {
    when(userService.getCurrentUser()).thenReturn(owner);
    when(issueReportRepository.findByExternalId(REPORT_EXTERNAL_ID)).thenReturn(Optional.of(report));
    when(reportImageRepository.findByIssueReportAndExternalId(report, IMAGE_EXTERNAL_ID))
        .thenReturn(Optional.of(image));

    ReportImage result = service.getImage(REPORT_EXTERNAL_ID, IMAGE_EXTERNAL_ID);

    assertEquals(image, result);
    verify(reportImageRepository).findByIssueReportAndExternalId(report, IMAGE_EXTERNAL_ID);
  }

  @Test
  void getImage_managerAllowed() {
    when(userService.getCurrentUser()).thenReturn(manager);
    when(issueReportRepository.findByExternalId(REPORT_EXTERNAL_ID)).thenReturn(Optional.of(report));
    when(reportImageRepository.findByIssueReportAndExternalId(report, IMAGE_EXTERNAL_ID))
        .thenReturn(Optional.of(image));

    ReportImage result = service.getImage(REPORT_EXTERNAL_ID, IMAGE_EXTERNAL_ID);

    assertEquals(image, result);
  }

  @Test
  void getImage_unauthorizedUser_throwsAccessDenied() {
    UserProfile stranger = userFixture(99L, false);
    when(userService.getCurrentUser()).thenReturn(stranger);
    when(issueReportRepository.findByExternalId(REPORT_EXTERNAL_ID)).thenReturn(Optional.of(report));

    assertThrows(AccessDeniedException.class,
        () -> service.getImage(REPORT_EXTERNAL_ID, IMAGE_EXTERNAL_ID));
  }

  @Test
  void getImage_reportNotFound_throws() {
    when(userService.getCurrentUser()).thenReturn(owner);
    when(issueReportRepository.findByExternalId(REPORT_EXTERNAL_ID)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class,
        () -> service.getImage(REPORT_EXTERNAL_ID, IMAGE_EXTERNAL_ID));
  }

  @Test
  void getImage_imageNotFound_throws() {
    when(userService.getCurrentUser()).thenReturn(owner);
    when(issueReportRepository.findByExternalId(REPORT_EXTERNAL_ID)).thenReturn(Optional.of(report));
    when(reportImageRepository.findByIssueReportAndExternalId(report, IMAGE_EXTERNAL_ID))
        .thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class,
        () -> service.getImage(REPORT_EXTERNAL_ID, IMAGE_EXTERNAL_ID));
  }

  @Test
  void uploadImage_success() throws IOException, HttpMediaTypeException {
    MultipartFile file = org.mockito.Mockito.mock(MultipartFile.class);
    when(file.getOriginalFilename()).thenReturn("photo.jpg");
    when(file.getContentType()).thenReturn("image/jpeg");
    when(file.isEmpty()).thenReturn(false);

    when(userService.getCurrentUser()).thenReturn(owner);
    when(issueReportRepository.findByExternalId(REPORT_EXTERNAL_ID)).thenReturn(Optional.of(report));
    when(storageService.store(file)).thenReturn(STORAGE_KEY);
    when(reportImageRepository.save(any(ReportImage.class))).thenAnswer(invocation -> invocation.getArgument(0));

    ReportImage result = service.uploadImage(REPORT_EXTERNAL_ID, file);

    assertNotNull(result);
    assertEquals(URI.create("stored:" + STORAGE_KEY), result.getImageLocator());
    assertEquals("photo.jpg", result.getFilename());
    assertEquals("image/jpeg", result.getMimeType());
    assertEquals(0, result.getAlbumOrder());
    assertEquals(report, result.getIssueReport());
    verify(storageService).store(file);
    verify(reportImageRepository).save(any(ReportImage.class));
  }

  @Test
  void uploadImage_notOwner_throwsAccessDenied() throws Exception {
    MultipartFile file = org.mockito.Mockito.mock(MultipartFile.class);
    when(file.isEmpty()).thenReturn(false);
    when(userService.getCurrentUser()).thenReturn(manager);
    when(issueReportRepository.findByExternalId(REPORT_EXTERNAL_ID)).thenReturn(Optional.of(report));

    assertThrows(AccessDeniedException.class, () -> service.uploadImage(REPORT_EXTERNAL_ID, file));
    verify(storageService, never()).store(any());
    verify(reportImageRepository, never()).save(any());
  }

  @Test
  void uploadImage_reportNotFound_throws() {
    MultipartFile file = org.mockito.Mockito.mock(MultipartFile.class);
    when(file.isEmpty()).thenReturn(false);
    when(userService.getCurrentUser()).thenReturn(owner);
    when(issueReportRepository.findByExternalId(REPORT_EXTERNAL_ID)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> service.uploadImage(REPORT_EXTERNAL_ID, file));
  }

  @Test
  void uploadImage_emptyFile_throwsBadRequest() throws Exception {
    MultipartFile file = org.mockito.Mockito.mock(MultipartFile.class);
    when(file.isEmpty()).thenReturn(true);

    assertThrows(IllegalArgumentException.class, () -> service.uploadImage(REPORT_EXTERNAL_ID, file));
    verify(storageService, never()).store(any());
    verify(reportImageRepository, never()).save(any());
  }

  @Test
  void getImageFile_success() throws IOException {
    Resource resource = org.mockito.Mockito.mock(Resource.class);
    when(storageService.retrieve(STORAGE_KEY)).thenReturn(resource);

    Resource result = service.getImageFile(STORAGE_KEY);

    assertEquals(resource, result);
    verify(storageService).retrieve(STORAGE_KEY);
  }

  @Test
  void getImageFile_missingBackingFile_throws() throws IOException {
    when(storageService.retrieve(STORAGE_KEY)).thenThrow(new IOException("missing"));

    assertThrows(IOException.class, () -> service.getImageFile(STORAGE_KEY));
  }

  @Test
  void deleteImage_ownerAllowed_deletesFileAndMetadata() throws IOException {
    when(userService.getCurrentUser()).thenReturn(owner);
    when(issueReportRepository.findByExternalId(REPORT_EXTERNAL_ID)).thenReturn(Optional.of(report));
    when(reportImageRepository.findByIssueReportAndExternalId(report, IMAGE_EXTERNAL_ID))
        .thenReturn(Optional.of(image));

    service.deleteImage(REPORT_EXTERNAL_ID, IMAGE_EXTERNAL_ID);

    verify(storageService).delete(STORAGE_KEY);
    verify(reportImageRepository).delete(image);
  }

  @Test
  void deleteImage_managerAllowed_deletesFileAndMetadata() throws IOException {
    when(userService.getCurrentUser()).thenReturn(manager);
    when(issueReportRepository.findByExternalId(REPORT_EXTERNAL_ID)).thenReturn(Optional.of(report));
    when(reportImageRepository.findByIssueReportAndExternalId(report, IMAGE_EXTERNAL_ID))
        .thenReturn(Optional.of(image));

    service.deleteImage(REPORT_EXTERNAL_ID, IMAGE_EXTERNAL_ID);

    verify(storageService).delete(STORAGE_KEY);
    verify(reportImageRepository).delete(image);
  }

  @Test
  void deleteImage_wrongReportImageCombination_throwsNotFound() throws Exception {
    when(userService.getCurrentUser()).thenReturn(owner);
    when(issueReportRepository.findByExternalId(REPORT_EXTERNAL_ID)).thenReturn(Optional.of(report));
    when(reportImageRepository.findByIssueReportAndExternalId(report, IMAGE_EXTERNAL_ID))
        .thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class,
        () -> service.deleteImage(REPORT_EXTERNAL_ID, IMAGE_EXTERNAL_ID));
    verify(storageService, never()).delete(anyString());
    verify(reportImageRepository, never()).delete(any());
  }

  @Test
  void deleteImage_unauthorized_throwsAccessDenied() throws Exception {
    UserProfile stranger = userFixture(99L, false);
    when(userService.getCurrentUser()).thenReturn(stranger);
    when(issueReportRepository.findByExternalId(REPORT_EXTERNAL_ID)).thenReturn(Optional.of(report));

    assertThrows(AccessDeniedException.class,
        () -> service.deleteImage(REPORT_EXTERNAL_ID, IMAGE_EXTERNAL_ID));
    verify(storageService, never()).delete(anyString());
    verify(reportImageRepository, never()).delete(any());
  }

  private static AcceptedState acceptedStateFixture(String statusTag) {
    AcceptedState state = new AcceptedState();
    state.setStatusTag(statusTag);
    state.setStatusTagDescription(statusTag);
    return state;
  }

  private static UserProfile userFixture(long id, boolean isManager) {
    UserProfile user = new UserProfile();
    setField(user, "id", id);
    setField(user, "externalId", UUID.nameUUIDFromBytes(("user:" + id).getBytes()));
    user.setIsManager(isManager);
    user.setUserEnabled(true);
    user.setOauthKey("oauth:" + id);
    user.setEmail("user" + id + "@example.com");
    user.setDisplayName("User " + id);
    return user;
  }

  private static void setField(Object target, String fieldName, Object value) {
    try {
      var field = target.getClass().getDeclaredField(fieldName);
      field.setAccessible(true);
      field.set(target, value);
    } catch (ReflectiveOperationException e) {
      throw new AssertionError(e);
    }
  }

}
