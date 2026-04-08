package edu.cnm.deepdive.seesomethingabq.service.repository;

import edu.cnm.deepdive.seesomethingabq.model.entity.UserProfile;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

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

  /**
   * Finds a user profile by external identifier.
   *
   * @param externalId user external ID.
   * @return optional containing the user profile if found.
   */
  Optional<UserProfile> findByExternalId(UUID externalId);

  /**
   * Searches for user profiles whose display name contains the provided substring.
   *
   * @param displayName substring to search for.
   * @param sort sort order.
   * @param limit maximum number of results.
   * @return matching user profiles.
   */
  List<UserProfile> findUserProfileByDisplayNameContaining(String displayName, Sort sort, Limit limit);

  /**
   * Disables a user profile by external identifier.
   *
   * @param externalId user external ID.
   * @return number of rows updated.
   */
  @Modifying
  @Query("UPDATE UserProfile SET userEnabled = FALSE WHERE externalId = :externalId")
  int disableUser(UUID externalId);

  /**
   * Enables a user profile by external identifier.
   *
   * @param externalId user external ID.
   * @return number of rows updated.
   */
  @Modifying
  @Query("UPDATE UserProfile SET userEnabled = TRUE WHERE externalId = :externalId")
  int enableUser(UUID externalId);

  /**
   * Sets manager privilege for a user profile by external identifier.
   *
   * @param externalId user external ID.
   * @param isManager {@code true} to grant manager privileges; {@code false} to revoke.
   * @return number of rows updated.
   */
  @Modifying
  @Query("UPDATE UserProfile SET isManager = :isManager WHERE externalId = :externalId")
  int setIsManager(UUID externalId, boolean isManager);



}
