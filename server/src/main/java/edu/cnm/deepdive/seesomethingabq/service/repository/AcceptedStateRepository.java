package edu.cnm.deepdive.seesomethingabq.service.repository;

import edu.cnm.deepdive.seesomethingabq.model.entity.AcceptedState;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data repository for {@link AcceptedState} persistence operations.
 */
public interface AcceptedStateRepository extends JpaRepository<AcceptedState, Long> {

  /**
   * Finds an accepted state by status tag.
   *
   * @param statusTag status tag.
   * @return accepted state, or {@code null} if not found.
   */
  AcceptedState findByStatusTag(String statusTag);

  /**
   * Returns whether an accepted state exists with the provided status tag.
   *
   * @param statusTag status tag.
   * @return {@code true} if a matching state exists; {@code false} otherwise.
   */
  boolean existsByStatusTag(String statusTag);

}
