package edu.cnm.deepdive.seesomethingabq.service.repository;

import edu.cnm.deepdive.seesomethingabq.model.entity.IssueType;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssueTypeRepository extends JpaRepository<IssueType, Long> {

  List<IssueType> findAllByIssueTypeTagIn(Collection<String> issueTypeTags);

  IssueType findByIssueTypeTag(String issueTypeTag);

  void deleteIssueTypeByIssueTypeTag(String issueTypeTag);

  boolean existsByIssueTypeTag(String issueTypeTag);
}
