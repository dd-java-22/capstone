package edu.cnm.deepdive.seesomethingabq.service.repository;

import edu.cnm.deepdive.seesomethingabq.model.entity.ReportLocation;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data repository for {@link ReportLocation} persistence operations.
 */
public interface ReportLocationRepository extends JpaRepository<ReportLocation, Long> {



}
