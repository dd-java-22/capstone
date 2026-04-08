package edu.cnm.deepdive.seesomethingabq.service.repository;

import edu.cnm.deepdive.seesomethingabq.model.entity.IssueReport;
import edu.cnm.deepdive.seesomethingabq.model.entity.ReportImage;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data repository for {@link ReportImage} persistence operations.
 */
public interface ReportImageRepository extends JpaRepository<ReportImage, Long> {

  /**
   * Finds a report image by parent report and image external identifier.
   *
   * @param issueReport parent report.
   * @param externalId image external ID.
   * @return optional containing the image if found.
   */
  Optional<ReportImage> findByIssueReportAndExternalId(IssueReport issueReport, UUID externalId);

  /**
   * Finds a report image by external identifier.
   *
   * @param externalId image external ID.
   * @return optional containing the image if found.
   */
  Optional<ReportImage> findByExternalId(UUID externalId);

}
