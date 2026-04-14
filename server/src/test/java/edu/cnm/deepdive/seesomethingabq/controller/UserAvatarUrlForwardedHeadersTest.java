package edu.cnm.deepdive.seesomethingabq.controller;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import edu.cnm.deepdive.seesomethingabq.TestStorageConfig;
import edu.cnm.deepdive.seesomethingabq.service.storage.LocalFileSystemStorageService;
import java.net.URL;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.mock.web.MockMultipartFile;

@SpringBootTest
@ActiveProfiles("service")
@TestPropertySource(properties = {
    "spring.main.allow-bean-definition-overriding=true",
    "spring.security.oauth2.resourceserver.jwt.issuer-uri=https://example.com/issuer"
})
@ContextConfiguration(classes = {UserMeEndpointTest.TestConfig.class, TestStorageConfig.class})
class UserAvatarUrlForwardedHeadersTest {

  @Autowired
  private WebApplicationContext context;

  @Autowired
  private JwtDecoder jwtDecoder;

  @Autowired
  private LocalFileSystemStorageService storageService;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders
        .webAppContextSetup(context)
        .apply(springSecurity())
        .build();
  }

  @Test
  void uploadAvatar_usesForwardedHeadersForGeneratedAvatarUrl() throws Exception {
    // Backend will see an internal connector (http, :8080), but should generate URLs using the
    // public reverse-proxy context (https, :443) provided via X-Forwarded-* headers.
    String subject = "sub-avatar-user";
    URL picture = new URL("https://example.com/avatar.png");
    Jwt jwt = Jwt.withTokenValue("token")
        .header("alg", "none")
        .subject(subject)
        .claim("name", "Avatar User")
        .claim("email", "avatar.user@example.com")
        .claim("picture", picture)
        .build();
    when(jwtDecoder.decode("token")).thenReturn(jwt);
    when(storageService.store(any())).thenReturn("stored-key");

    MockMultipartFile file = new MockMultipartFile(
        "avatar",
        "avatar.jpg",
        "image/jpeg",
        "data".getBytes()
    );

    mockMvc
        .perform(
            multipart("/users/me/avatar")
                .file(file)
                .header("Authorization", "Bearer token")
                .header("X-Forwarded-Proto", "https")
                .header("X-Forwarded-Host", "gilesvolmir.ddc-java.services")
                .header("X-Forwarded-Port", "443")
                // Simulate internal connector context.
                .with(request -> {
                  request.setScheme("http");
                  request.setServerName("gilesvolmir.ddc-java.services");
                  request.setServerPort(8080);
                  return request;
                })
                .with(csrf())
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.avatar",
            allOf(
                startsWith("https://gilesvolmir.ddc-java.services/"),
                containsString("/avatar"),
                not(containsString(":8080"))
            )));
  }

}
