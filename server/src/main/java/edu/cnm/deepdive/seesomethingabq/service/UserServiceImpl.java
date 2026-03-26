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

import edu.cnm.deepdive.seesomethingabq.model.entity.UserProfile;
import edu.cnm.deepdive.seesomethingabq.service.repository.UserProfileRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link UserService} providing business logic for user profile operations.
 * This service handles OAuth2-based user identification and creation, as well as profile updates.
 */
@Service
public class UserServiceImpl implements UserService {

  private static final String OAUTH_SUB_CLAIM = "sub";
  private static final String OAUTH_NAME_CLAIM = "name";

  private final UserProfileRepository repository;

  /**
   * Constructs an instance of {@code AbstractUserService} with the specified repository.
   *
   * @param repository User profile repository for persistence operations.
   */
  @Autowired
  public UserServiceImpl(UserProfileRepository repository) {
    this.repository = repository;
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
  public List<UserProfile> getAll() {
    return repository.findAll();
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
        .orElseThrow(IllegalArgumentException::new);
    user.setIsManager(manager);
    return repository.save(user);
  }

  @Override
  public UserProfile setEnabled(UUID externalId, boolean enabled) {
    UserProfile user = repository
        .findByExternalId(externalId)
        .orElseThrow(IllegalArgumentException::new);
    user.setUserEnabled(enabled);
    return repository.save(user);
  }
}
