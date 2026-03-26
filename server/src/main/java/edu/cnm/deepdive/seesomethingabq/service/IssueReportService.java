package edu.cnm.deepdive.seesomethingabq.service;

import edu.cnm.deepdive.seesomethingabq.model.entity.IssueReport;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IssueReportService {

  Page<IssueReport> getAll(Pageable pageable);

  Optional<IssueReport> getByExternalId(UUID externalId);

  IssueReport setAcceptedState(UUID externalId, String statusTag);

  IssueReport replaceIssueTypes(UUID externalId, Iterable<String> issueTypeTags);

}

