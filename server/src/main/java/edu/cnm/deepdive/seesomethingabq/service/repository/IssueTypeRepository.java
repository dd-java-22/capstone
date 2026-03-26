package edu.cnm.deepdive.seesomethingabq.service.repository;

import edu.cnm.deepdive.seesomethingabq.model.entity.IssueType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssueTypeRepository extends JpaRepository<IssueType, Long> {

  IssueType findByIssueTypeTag(String issueTypeTag);

  void deleteIssueTypeByIssueTypeTag(String issueTypeTag);

  boolean existsByIssueTypeTag(String issueTypeTag);
}
