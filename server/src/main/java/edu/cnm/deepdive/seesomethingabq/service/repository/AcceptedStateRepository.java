package edu.cnm.deepdive.seesomethingabq.service.repository;

import edu.cnm.deepdive.seesomethingabq.model.entity.AcceptedState;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AcceptedStateRepository extends JpaRepository<AcceptedState, Long> {

  // My side added Optional - is it correct?
  Optional<AcceptedState> findByStatusTag(String statusTag);

  boolean existsByStatusTag(String statusTag);

}
