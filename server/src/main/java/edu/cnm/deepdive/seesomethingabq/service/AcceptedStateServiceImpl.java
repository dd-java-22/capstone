package edu.cnm.deepdive.seesomethingabq.service;

import edu.cnm.deepdive.seesomethingabq.model.entity.AcceptedState;
import edu.cnm.deepdive.seesomethingabq.service.repository.AcceptedStateRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

@Service
public class AcceptedStateServiceImpl implements AcceptedStateService {

  private final AcceptedStateRepository repository;

  @Autowired
  public AcceptedStateServiceImpl(AcceptedStateRepository repository) {
    this.repository = repository;
  }

  @Override
  public List<AcceptedState> getAll() {
    return repository.findAll(Sort.by(Direction.ASC, "statusTag"));
  }

  @Override
  public AcceptedState createNewAcceptedState(AcceptedState newAcceptedState) {
    if (repository.existsByStatusTag(newAcceptedState.getStatusTag())) {
      throw new IllegalArgumentException("Accepted state status tag already exists: "
          + newAcceptedState.getStatusTag());
    }
    return repository.save(newAcceptedState);
  }

  @Override
  public AcceptedState updateAcceptedStateDescription(String statusTag, String newDescription) {
    AcceptedState stateToChange = repository.findByStatusTag(statusTag);
    if (stateToChange == null) {
      throw new IllegalArgumentException("Accepted state status tag not found: " + statusTag);
    }
    stateToChange.setStatusTagDescription(newDescription);
    return repository.save(stateToChange);
  }

  @Override
  public void deleteUnusedAcceptedState(String statusTag) {
    AcceptedState doomedState = repository.findByStatusTag(statusTag);
    if (doomedState == null) {
      throw new IllegalArgumentException("Accepted state status tag not found: " + statusTag);
    }
    if (doomedState.getIssueReports().isEmpty()) {
      repository.delete(doomedState);
    } else {
      throw new IllegalStateException("Cannot delete accepted state with active reports");
    }
  }

}

