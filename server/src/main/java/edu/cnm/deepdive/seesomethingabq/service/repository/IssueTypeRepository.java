package edu.cnm.deepdive.seesomethingabq.service.repository;

import edu.cnm.deepdive.seesomethingabq.model.entity.IssueType;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data repository for {@link IssueType} persistence operations.
 */
public interface IssueTypeRepository extends JpaRepository<IssueType, Long> {

  /**
   * Finds all issue types matching the provided tags.
   *
   * @param issueTypeTags issue type tags.
   * @return matching issue types.
   */
  List<IssueType> findAllByIssueTypeTagIn(Collection<String> issueTypeTags);

  /**
   * Finds an issue type by tag.
   *
   * @param issueTypeTag issue type tag.
   * @return issue type, or {@code null} if not found.
   */
  IssueType findByIssueTypeTag(String issueTypeTag);

  /**
   * Deletes an issue type by tag.
   *
   * @param issueTypeTag issue type tag.
   */
  void deleteIssueTypeByIssueTypeTag(String issueTypeTag);

  /**
   * Returns whether an issue type exists with the provided tag.
   *
   * @param issueTypeTag issue type tag.
   * @return {@code true} if a matching issue type exists; {@code false} otherwise.
   */
  boolean existsByIssueTypeTag(String issueTypeTag);
}
