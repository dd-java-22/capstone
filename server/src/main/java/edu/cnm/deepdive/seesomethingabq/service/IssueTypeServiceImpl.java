package edu.cnm.deepdive.seesomethingabq.service;

import edu.cnm.deepdive.seesomethingabq.model.entity.IssueType;
import edu.cnm.deepdive.seesomethingabq.service.repository.IssueTypeRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

@Service
public class IssueTypeServiceImpl implements IssueTypeService {

  private final IssueTypeRepository repository;

  @Autowired
  public IssueTypeServiceImpl(IssueTypeRepository repository) {
    this.repository = repository;
  }

  @Override
  public List<IssueType> getAll() {
    return repository.findAll(Sort.by(Direction.ASC, "issueTypeTag"));
  }

  @Override
  public boolean deleteUnusedIssueType(String issueTypeTag) {
    throw new UnsupportedOperationException("Not yet implemented");
    int numDeleted = repository.deleteByIssueTypeTag(issueTypeTag);
    IssueType doomedTag =  repository.findByIssueTypeTag(issueTypeTag);
  }
}
