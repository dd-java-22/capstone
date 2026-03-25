package edu.cnm.deepdive.seesomethingabq.service.dao;

import edu.cnm.deepdive.seesomethingabq.model.entity.IssueReport;
import edu.cnm.deepdive.seesomethingabq.model.entity.UserProfile;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssueReportRepository extends JpaRepository<IssueReport, Long> {

  Optional<IssueReport> findByExternalKey(UUID externalKey);

  List<IssueReport> findByUser(UserProfile user);

  List<IssueReport> findByUserOrderByTimeLastModifiedDesc(UserProfile user);

  List<IssueReport> findByUserOrderByTimeFirstReportedDesc(UserProfile user);
}
