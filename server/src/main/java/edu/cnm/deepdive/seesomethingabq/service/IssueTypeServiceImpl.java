package edu.cnm.deepdive.seesomethingabq.service;

import edu.cnm.deepdive.seesomethingabq.exception.ConflictException;
import edu.cnm.deepdive.seesomethingabq.exception.DuplicateIssueTypeException;
import edu.cnm.deepdive.seesomethingabq.exception.IssueTypeNotFoundException;
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
  public IssueType getByIssueTypeTag(String issueTypeTag) {
    IssueType issueType = tagRepository.findByIssueTypeTag(issueTypeTag);
    if (issueType == null) {
      throw new IssueTypeNotFoundException("Issue type tag not found: " + issueTypeTag);
    }
    return issueType;
  }

  @Override
  public IssueType createNewIssueType(IssueType newIssueType) {
    if (tagRepository.existsByIssueTypeTag(newIssueType.getIssueTypeTag())) {
      throw new DuplicateIssueTypeException("Issue type tag already exists: " + newIssueType.getIssueTypeTag());
    }
    return tagRepository.save(newIssueType);
  }

  @Override
  public IssueType updateIssueTypeDescription(String issueTypeTag, String newIssueTypeDescription) {
    IssueType tagToChange = tagRepository.findByIssueTypeTag(issueTypeTag);
    if (tagToChange == null) {
      throw new IssueTypeNotFoundException("Issue type tag not found: " + issueTypeTag);
    }
    tagToChange.setIssueTypeDescription(newIssueTypeDescription);
    return tagRepository.save(tagToChange);
  }

  @Override
  public void deleteUnusedIssueType(String issueTypeTag) {
    IssueType doomedTag = tagRepository.findByIssueTypeTag(issueTypeTag);
    if (doomedTag == null) {
      throw new IssueTypeNotFoundException("Issue type tag not found: " + issueTypeTag);
    }    if (doomedTag.getIssueReports().isEmpty()) {
      tagRepository.delete(doomedTag);
    } else {
      throw new ConflictException("Cannot delete issue type with active reports");
    }
  }
}
