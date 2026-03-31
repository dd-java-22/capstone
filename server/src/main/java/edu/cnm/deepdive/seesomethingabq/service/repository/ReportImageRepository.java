package edu.cnm.deepdive.seesomethingabq.service.repository;

import edu.cnm.deepdive.seesomethingabq.model.entity.IssueReport;
import edu.cnm.deepdive.seesomethingabq.model.entity.ReportImage;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportImageRepository extends JpaRepository<ReportImage, Long> {

  Optional<ReportImage> findByIssueReportAndExternalId(IssueReport issueReport, UUID externalId);

}
