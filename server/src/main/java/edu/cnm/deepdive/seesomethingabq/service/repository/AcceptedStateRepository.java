package edu.cnm.deepdive.seesomethingabq.service.repository;

import edu.cnm.deepdive.seesomethingabq.model.entity.AcceptedState;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AcceptedStateRepository extends JpaRepository<AcceptedState, Long> {

  AcceptedState findByStatusTag(String statusTag);

  boolean existsByStatusTag(String statusTag);

}
