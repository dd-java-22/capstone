/*
 *  Copyright 2026 CNM Ingenuity, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package edu.cnm.deepdive.seesomethingabq.service;

import edu.cnm.deepdive.seesomethingabq.exception.UserNotFoundException;
import edu.cnm.deepdive.seesomethingabq.model.dto.UserProfileResponse;
import edu.cnm.deepdive.seesomethingabq.model.entity.UserProfile;
import edu.cnm.deepdive.seesomethingabq.service.repository.IssueReportRepository;
import edu.cnm.deepdive.seesomethingabq.service.repository.UserProfileRepository;
import edu.cnm.deepdive.seesomethingabq.service.storage.StorageService;
import java.net.URL;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * Implementation of {@link UserService} providing business logic for user profile operations.
 * This service handles OAuth2-based user identification and creation, as well as profile updates.
 */
@Service
public class UserServiceImpl implements UserService {

  private static final String OAUTH_SUB_CLAIM = "sub";
  private static final String OAUTH_NAME_CLAIM = "name";

  private final UserProfileRepository repository;
  private final IssueReportRepository issueReportRepository;
  private final StorageService storageService;

  /**
   * Constructs an instance of {@code UserServiceImpl} with the specified repositories.
   *
   * @param repository user profile repository for persistence operations.
   * @param issueReportRepository issue report repository for report count lookups.
   */
  @Autowired
  public UserServiceImpl(UserProfileRepository repository,
      IssueReportRepository issueReportRepository, StorageService storageService) {
    this.repository = repository;
    this.issueReportRepository = issueReportRepository;
    this.storageService = storageService;
  }

  @Override
  public UserProfile getCurrentUser() {
    //noinspection DataFlowIssue
    return (UserProfile) SecurityContextHolder
        .getContext()
        .getAuthentication()
        .getPrincipal();
  }

  @Override
  public Optional<UserProfile> get(Long id) {
    return repository.findById(id);
  }

  @Override
  public UserProfile getOrCreate(String oauthKey, UserProfile userProfile) {
    return repository
        .findByOauthKey(oauthKey)
        .orElseGet(() -> {
          userProfile.setOauthKey(oauthKey);
          return repository.save(userProfile);
        });
  }

  @Override
  public UserProfile updateDisplayName(Long id, String displayName) {
    return repository
        .findById(id)
        .map(user -> {
          user.setDisplayName(displayName);
          return repository.save(user);
        })
        .orElseThrow(NoSuchElementException::new);
  }

  @Override
  public UserProfile updateEmail(Long id, String email) {
    return repository
        .findById(id)
        .map(user -> {
          user.setEmail(email);
          return repository.save(user);
        })
        .orElseThrow(NoSuchElementException::new);
  }

  @Override
  public UserProfile updateAvatar(Long id, URL avatar) {
    return repository
        .findById(id)
        .map(user -> {
          user.setAvatar(avatar);
          return repository.save(user);
        })
        .orElseThrow(NoSuchElementException::new);
  }

  @Override
  public UserProfile updateAvatarKey(Long id, String storageKey, String contentType) {
    return repository
        .findById(id)
        .map(user -> {
          // Best-effort cleanup of prior custom avatar (if present).
          String priorKey = user.getAvatarKey();
          if (priorKey != null && !priorKey.isBlank() && !priorKey.equals(storageKey)) {
            try {
              storageService.delete(priorKey);
            } catch (Exception e) {
              // TODO: Consider asynchronous cleanup / orphan reaper. Avoid breaking avatar updates.
            }
          }

          user.setAvatarKey(storageKey);
          user.setAvatarMimeType(contentType);

          try {
            ServletUriComponentsBuilder builder = ServletUriComponentsBuilder
                .fromCurrentContextPath();
            // When deployed behind Apache (TLS termination + reverse proxy), the inbound servlet
            // request context may reflect the internal connector (e.g., http + :8080). Prefer
            // X-Forwarded-* headers when present so generated URLs use the public endpoint.
            HttpServletRequest request = currentRequestOrNull();
            if (request != null) {
              applyForwardedHeaders(builder, request);
            }
            URL avatarUrl = builder
                .path("/users/{externalId}/avatar")
                .buildAndExpand(user.getExternalId())
                .toUri()
                .toURL();
            user.setAvatar(avatarUrl);
          } catch (Exception e) {
            throw new RuntimeException("Failed to create backend avatar URL", e);
          }

          return repository.save(user);
        })
        .orElseThrow(NoSuchElementException::new);
  }

  private static HttpServletRequest currentRequestOrNull() {
    if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
      return attributes.getRequest();
    }
    return null;
  }

  private static void applyForwardedHeaders(ServletUriComponentsBuilder builder, HttpServletRequest request) {
    String proto = firstForwardedValue(request.getHeader("X-Forwarded-Proto"));
    String host = firstForwardedValue(request.getHeader("X-Forwarded-Host"));
    String port = firstForwardedValue(request.getHeader("X-Forwarded-Port"));

    if (proto != null && !proto.isBlank()) {
      builder.scheme(proto.trim());
    }
    if (host != null && !host.isBlank()) {
      // X-Forwarded-Host can include port; ServletUriComponentsBuilder has a dedicated port setting.
      String trimmed = host.trim();
      int colon = trimmed.lastIndexOf(':');
      if (colon > 0 && colon < trimmed.length() - 1 && trimmed.indexOf(']') < 0) { // naive IPv6 avoidance
        String maybePort = trimmed.substring(colon + 1);
        if (maybePort.chars().allMatch(Character::isDigit)) {
          builder.host(trimmed.substring(0, colon));
          builder.port(Integer.parseInt(maybePort));
          return;
        }
      }
      builder.host(trimmed);
    }
    if (port != null && !port.isBlank()) {
      try {
        int parsed = Integer.parseInt(port.trim());
        // Avoid explicit :443 or :80 in generated URLs.
        String scheme = builder.build().getScheme();
        if (("https".equalsIgnoreCase(scheme) && parsed == 443) || ("http".equalsIgnoreCase(scheme) && parsed == 80)) {
          builder.port(-1);
        } else {
          builder.port(parsed);
        }
      } catch (NumberFormatException ignored) {
        // If the header is malformed, fall back to the servlet container context.
      }
    }
  }

  private static String firstForwardedValue(String header) {
    if (header == null) {
      return null;
    }
    int comma = header.indexOf(',');
    return (comma >= 0) ? header.substring(0, comma).trim() : header.trim();
  }

  @Override
  public List<UserProfile> getAll() {
    return repository.findAll();
  }

  @Override
  public Page<UserProfile> getAll(Pageable pageable) {
    return repository.findAll(pageable);
  }

  @Override
  public UserProfile getMe() {
    return getCurrentUser();
  }

  @Override
  public Optional<UserProfile> getByExternalId(UUID externalId) {
    return repository.findByExternalId(externalId);
  }

  @Override
  public UserProfile setManagerStatus(UUID externalId, boolean manager) {
    UserProfile user = repository
        .findByExternalId(externalId)
        .orElseThrow(() -> new UserNotFoundException("User not found: " + externalId));
    user.setIsManager(manager);
    return repository.save(user);
  }

  @Override
  public UserProfile setEnabled(UUID externalId, boolean enabled) {
    UserProfile user = repository
        .findByExternalId(externalId)
        .orElseThrow(() -> new UserNotFoundException("User not found: " + externalId));
    user.setUserEnabled(enabled);
    return repository.save(user);
  }

  /**
   * Maps a {@link UserProfile} entity to a {@link UserProfileResponse}, including total report count.
   *
   * @param userProfile source entity.
   * @return DTO enriched with report count.
   */
  @Override
  public UserProfileResponse getUserProfileResponse(UserProfile userProfile) {
    long reportCount = issueReportRepository.countByUserProfile(userProfile);
    return new UserProfileResponse(
        userProfile.getExternalId(),
        userProfile.getDisplayName(),
        userProfile.getEmail(),
        userProfile.getAvatar(),
        userProfile.isManager(),
        userProfile.getTimeCreated(),
        userProfile.getUserEnabled(),
        reportCount
    );
  }

}
