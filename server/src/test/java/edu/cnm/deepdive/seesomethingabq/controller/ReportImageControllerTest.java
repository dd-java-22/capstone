package edu.cnm.deepdive.seesomethingabq.controller;

import static org.hamcrest.Matchers.endsWith;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import edu.cnm.deepdive.seesomethingabq.TestStorageConfig;
import edu.cnm.deepdive.seesomethingabq.model.dto.AddImageRequest;
import edu.cnm.deepdive.seesomethingabq.model.entity.ReportImage;
import edu.cnm.deepdive.seesomethingabq.service.ReportImageService;
import java.nio.charset.StandardCharsets;
import java.net.URI;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

@SpringBootTest
@ActiveProfiles("service")
@TestPropertySource(properties = {
    "spring.main.allow-bean-definition-overriding=true",
    "spring.security.oauth2.resourceserver.jwt.issuer-uri=https://example.com/issuer",
    "spring.security.oauth2.resourceserver.jwt.audiences=test-client-id"
})
@ContextConfiguration(classes = {ReportImageControllerTest.TestConfig.class, TestStorageConfig.class})
class ReportImageControllerTest {

  @Autowired
  private WebApplicationContext context;

  private MockMvc mockMvc;

  @Autowired
  private ReportImageService reportImageService;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders
        .webAppContextSetup(context)
        .apply(springSecurity())
        .build();
  }

  @Test
  @WithMockUser(roles = "USER")
  void getImage_returnsBinaryContent() throws Exception {
    UUID reportId = UUID.randomUUID();
    UUID imageId = UUID.randomUUID();

    ReportImage image = new ReportImage();
    image.setMimeType("image/jpeg");
    image.setImageLocator(URI.create("stored:test.jpg"));

    when(reportImageService.getImage(reportId, imageId)).thenReturn(image);
    Resource resource = new ByteArrayResource("hello".getBytes(StandardCharsets.UTF_8));
    when(reportImageService.getImageFile("test.jpg")).thenReturn(resource);

    mockMvc.perform(
            get("/issue-reports/{reportId}/images/{imageId}", reportId, imageId)
        )
        .andExpect(status().isOk())
        .andExpect(content().contentType("image/jpeg"))
        .andExpect(content().bytes("hello".getBytes(StandardCharsets.UTF_8)));
  }

  @Test
  @WithMockUser(roles = "USER")
  void uploadImage_returnsCreatedAndLocation() throws Exception {
    UUID reportId = UUID.randomUUID();
    UUID imageId = UUID.randomUUID();

    ReportImage created = new ReportImage();
    ReflectionTestUtils.setField(created, "externalId", imageId);
    created.setFilename("photo.jpg");
    created.setMimeType("image/jpeg");
    created.setImageLocator(URI.create("stored:abc123.jpg"));

    when(reportImageService.uploadImage(
        org.mockito.ArgumentMatchers.eq(reportId),
        org.mockito.ArgumentMatchers.any()
    )).thenReturn(created);

    MockMultipartFile file = new MockMultipartFile(
        "file",
        "photo.jpg",
        "image/jpeg",
        "data".getBytes(StandardCharsets.UTF_8)
    );

    mockMvc.perform(
            multipart("/issue-reports/{reportId}/images/upload", reportId)
                .file(file)
                .with(csrf())
        )
        .andExpect(status().isCreated())
        .andExpect(header().string("Location",
            endsWith("/issue-reports/" + reportId + "/images/" + imageId)));

    verify(reportImageService).uploadImage(
        org.mockito.ArgumentMatchers.eq(reportId),
        org.mockito.ArgumentMatchers.any()
    );
  }

  @Test
  @WithMockUser(roles = "USER")
  void uploadImage_emptyFile_returnsBadRequest() throws Exception {
    UUID reportId = UUID.randomUUID();

    when(reportImageService.uploadImage(
        org.mockito.ArgumentMatchers.eq(reportId),
        org.mockito.ArgumentMatchers.any()
    )).thenThrow(new IllegalArgumentException("Upload file must not be empty."));

    MockMultipartFile file = new MockMultipartFile(
        "file",
        "empty.jpg",
        "image/jpeg",
        new byte[0]
    );

    mockMvc.perform(
            multipart("/issue-reports/{reportId}/images/upload", reportId)
                .file(file)
                .with(csrf())
        )
        .andExpect(status().isBadRequest());

    verify(reportImageService).uploadImage(
        org.mockito.ArgumentMatchers.eq(reportId),
        org.mockito.ArgumentMatchers.any()
    );
  }

  @Test
  @WithMockUser(roles = "USER")
  void addImage_createsImage() throws Exception {
    UUID reportId = UUID.randomUUID();
    UUID imageId = UUID.randomUUID();

    ReportImage created = new ReportImage();
    created.setFilename("new.jpg");
    ReflectionTestUtils.setField(created, "externalId", imageId);

    when(reportImageService.addImage(
        org.mockito.ArgumentMatchers.eq(reportId),
        org.mockito.ArgumentMatchers.any(AddImageRequest.class)
    )).thenReturn(created);

    mockMvc.perform(
            post("/issue-reports/{reportId}/images", reportId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "filename": "new.jpg",
                      "mimeType": "image/jpeg",
                      "imageLocator": "stored:new.jpg",
                      "albumOrder": 1
                    }
                    """)
        )
        .andExpect(status().isCreated())
        .andExpect(header().string("Location",
            endsWith("/issue-reports/" + reportId + "/images/" + imageId)))
        .andExpect(jsonPath("$.filename").value("new.jpg"));

    ArgumentCaptor<AddImageRequest> captor = ArgumentCaptor.forClass(AddImageRequest.class);
    verify(reportImageService).addImage(org.mockito.ArgumentMatchers.eq(reportId), captor.capture());
  }

  @Test
  @WithMockUser(roles = "USER")
  void deleteImage_returnsNoContent() throws Exception {
    UUID reportId = UUID.randomUUID();
    UUID imageId = UUID.randomUUID();

    mockMvc.perform(
            delete("/issue-reports/{reportId}/images/{imageId}", reportId, imageId)
                .with(csrf())
        )
        .andExpect(status().isNoContent());

    verify(reportImageService).deleteImage(reportId, imageId);
  }

  @TestConfiguration
  static class TestConfig {

    @Bean
    @Primary
    public ReportImageService reportImageService() {
      return org.mockito.Mockito.mock(ReportImageService.class);
    }

    @Bean(name = "provideDecoder")
    public JwtDecoder provideDecoder() {
      return org.mockito.Mockito.mock(JwtDecoder.class);
    }
  }
}
