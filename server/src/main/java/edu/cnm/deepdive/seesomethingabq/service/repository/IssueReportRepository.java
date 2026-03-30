package edu.cnm.deepdive.seesomethingabq.service.repository;

import edu.cnm.deepdive.seesomethingabq.model.entity.AcceptedState;
import edu.cnm.deepdive.seesomethingabq.model.entity.IssueReport;
import edu.cnm.deepdive.seesomethingabq.model.entity.IssueType;
import edu.cnm.deepdive.seesomethingabq.model.entity.UserProfile;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssueReportRepository extends JpaRepository<IssueReport, Long> {

  Optional<IssueReport> findByExternalId(UUID externalId);

  List<IssueReport> findByUserProfile(UserProfile userProfile, Sort sort);

  List<IssueReport> getIssueReportsByUserProfileOrderByTimeFirstReportedDesc(
      UserProfile userProfile);

  List<IssueReport> getIssueReportsByAcceptedStateOrderByTimeFirstReportedDesc(
      AcceptedState acceptedState);

  List<IssueReport> getIssueReportsByIssueTypesOrderByTimeFirstReportedDesc(
      List<IssueType> issueTypes);

  List<IssueReport> getIssueReportsByTimeLastModifiedBetween(
      Instant timeLastModifiedAfter, Instant timeLastModifiedBefore);
}
