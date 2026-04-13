package edu.cnm.deepdive.seesomethingabq.service.repository;

import edu.cnm.deepdive.seesomethingabq.model.entity.AcceptedState;
import edu.cnm.deepdive.seesomethingabq.model.entity.IssueReport;
import edu.cnm.deepdive.seesomethingabq.model.entity.IssueType;
import edu.cnm.deepdive.seesomethingabq.model.entity.UserProfile;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data repository for {@link IssueReport} persistence operations.
 */
public interface IssueReportRepository extends JpaRepository<IssueReport, Long> {

  /**
   * Finds an issue report by external identifier.
   *
   * @param externalId report external ID.
   * @return optional containing the report if found.
   */
  Optional<IssueReport> findByExternalId(UUID externalId);

  /**
   * Finds issue reports for a user profile with the specified pageable definition. order.
   *
   * @param userProfile report owner.
   * @param pageable sort order.
   * @return matching issue reports.
   */
  Page<IssueReport> findByUserProfile(UserProfile userProfile, Pageable pageable);

  /**
   * Counts the number of issue reports owned by the specified user profile.
   *
   * @param userProfile report owner.
   * @return total number of matching reports.
   */
  long countByUserProfile(UserProfile userProfile);

  /**
   * Returns a user's reports ordered by first-reported time descending.
   *
   * @param userProfile report owner.
   * @return matching issue reports.
   */
  List<IssueReport> getIssueReportsByUserProfileOrderByTimeFirstReportedDesc(
      UserProfile userProfile);

  /**
   * Returns reports in a specific accepted state ordered by first-reported time descending.
   *
   * @param acceptedState accepted state.
   * @return matching issue reports.
   */
  List<IssueReport> getIssueReportsByAcceptedStateOrderByTimeFirstReportedDesc(
      AcceptedState acceptedState);

  /**
   * Returns reports associated with the provided issue types ordered by first-reported time descending.
   *
   * @param issueTypes issue types.
   * @return matching issue reports.
   */
  List<IssueReport> getIssueReportsByIssueTypesOrderByTimeFirstReportedDesc(
      List<IssueType> issueTypes);

  /**
   * Returns reports whose last-modified time is within the provided range.
   *
   * @param timeLastModifiedAfter start of range (inclusive/exclusive depends on generated query).
   * @param timeLastModifiedBefore end of range.
   * @return matching issue reports.
   */
  List<IssueReport> getIssueReportsByTimeLastModifiedBetween(
      Instant timeLastModifiedAfter, Instant timeLastModifiedBefore);
}
