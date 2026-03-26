package edu.cnm.deepdive.seesomethingabq.service;

import edu.cnm.deepdive.seesomethingabq.model.entity.IssueReport;
import edu.cnm.deepdive.seesomethingabq.model.entity.IssueType;
import edu.cnm.deepdive.seesomethingabq.service.repository.IssueReportRepository;
import edu.cnm.deepdive.seesomethingabq.service.repository.IssueTypeRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

@Service
public class IssueTypeServiceImpl implements IssueTypeService {

  private final IssueTypeRepository tagRepository;

  @Autowired
  public IssueTypeServiceImpl(IssueTypeRepository tagRepository) {
    this.tagRepository = tagRepository;
  }

  @Override
  public List<IssueType> getAll() {
    return tagRepository.findAll(Sort.by(Direction.ASC, "issueTypeTag"));
  }

  @Override
  public IssueType updateIssueTypeDescription(String issueTypeTag, String newIssueTypeDescription) {
    IssueType tagToChange = tagRepository.findByIssueTypeTag(issueTypeTag);
    if (tagToChange == null) {
      throw new IllegalArgumentException("Issue type tag not found: " + issueTypeTag);
    }
    tagToChange.setIssueTypeDescription(newIssueTypeDescription);
    return tagRepository.save(tagToChange);
  }

  @Override
  public void deleteUnusedIssueType(String issueTypeTag) {
    IssueType doomedTag = tagRepository.findByIssueTypeTag(issueTypeTag);
    // TODO: 3/26/2026 if throw.
    if (doomedTag.getIssueReports().isEmpty()) {
      tagRepository.delete(doomedTag);
    } else {
      throw new IllegalStateException("Cannot delete issue type with active reports");
    }
  }
}
