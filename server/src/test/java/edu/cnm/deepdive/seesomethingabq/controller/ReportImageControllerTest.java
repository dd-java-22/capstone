package edu.cnm.deepdive.seesomethingabq.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.endsWith;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import edu.cnm.deepdive.seesomethingabq.TestStorageConfig;
import edu.cnm.deepdive.seesomethingabq.model.dto.AddImageRequest;
import edu.cnm.deepdive.seesomethingabq.model.entity.ReportImage;
import edu.cnm.deepdive.seesomethingabq.service.ReportImageService;
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
  void getImage_returnsImage() throws Exception {
    UUID reportId = UUID.randomUUID();
    UUID imageId = UUID.randomUUID();

    ReportImage image = new ReportImage();
    image.setFilename("test.jpg");
    image.setMimeType("image/jpeg");
    image.setImageLocator(URI.create("file://test.jpg"));

    when(reportImageService.getImage(reportId, imageId)).thenReturn(image);

    mockMvc.perform(
            get("/issue-reports/{reportId}/images/{imageId}", reportId, imageId)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.filename").value("test.jpg"));
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
                      "imageLocator": "file://new.jpg",
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
