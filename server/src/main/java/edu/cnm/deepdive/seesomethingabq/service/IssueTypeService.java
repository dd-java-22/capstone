package edu.cnm.deepdive.seesomethingabq.service;

import edu.cnm.deepdive.seesomethingabq.model.entity.IssueType;
import java.util.List;

public interface IssueTypeService {

  List<IssueType> getAll();

  IssueType updateIssueTypeDescription(String issueTypeTag, String newIssueTypeDescription);

  void deleteUnusedIssueType(String issueTypeTag);

  String createNewIssueType(IssueType newIssueType);
}
