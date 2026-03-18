package edu.cnm.deepdive.seesomethingabq.service.repository;

import edu.cnm.deepdive.seesomethingabq.model.entity.UserProfile;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for {@link UserProfile} entity persistence operations.
 */
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

  /**
   * Finds a user profile by OAuth2 key.
   *
   * @param oauthKey OAuth2 key to search for.
   * @return {@link Optional} containing the user profile if found, empty otherwise.
   */
  Optional<UserProfile> findByOauthKey(String oauthKey);

  Optional<UserProfile> findByExternalId(UUID externalId);

  List<UserProfile> findUserProfileByDisplayNameContaining(String displayName, Sort sort, Limit limit);

}
