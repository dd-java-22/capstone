package edu.cnm.deepdive.seesomethingabq.service;

import edu.cnm.deepdive.seesomethingabq.model.entity.IssueType;
import java.util.List;

/**
 * Service providing CRUD-style operations for {@link IssueType} entities.
 */
public interface IssueTypeService {

  /**
   * Returns all issue types.
   *
   * @return list of issue types.
   */
  List<IssueType> getAll();

  /**
   * Returns an issue type by tag.
   *
   * @param issueTypeTag issue type tag.
   * @return issue type.
   */
  IssueType getByIssueTypeTag(String issueTypeTag);

  /**
   * Updates the description for an issue type.
   *
   * @param issueTypeTag issue type tag.
   * @param newIssueTypeDescription new description text.
   * @return updated entity.
   */
  IssueType updateIssueTypeDescription(String issueTypeTag, String newIssueTypeDescription);

  /**
   * Deletes an issue type if it is not currently referenced by any reports.
   *
   * @param issueTypeTag issue type tag.
   */
  void deleteUnusedIssueType(String issueTypeTag);

  /**
   * Creates a new issue type.
   *
   * @param newIssueType issue type to create.
   * @return created entity.
   */
  IssueType createNewIssueType(IssueType newIssueType);
}
