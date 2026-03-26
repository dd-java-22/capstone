package edu.cnm.deepdive.seesomethingabq.service;

import edu.cnm.deepdive.seesomethingabq.model.entity.AcceptedState;
import edu.cnm.deepdive.seesomethingabq.model.entity.IssueReport;
import edu.cnm.deepdive.seesomethingabq.model.entity.IssueType;
import edu.cnm.deepdive.seesomethingabq.service.repository.AcceptedStateRepository;
import edu.cnm.deepdive.seesomethingabq.service.repository.IssueReportRepository;
import edu.cnm.deepdive.seesomethingabq.service.repository.IssueTypeRepository;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IssueReportServiceImpl implements IssueReportService {

  private final IssueReportRepository issueReportRepository;
  private final AcceptedStateRepository acceptedStateRepository;
  private final IssueTypeRepository issueTypeRepository;

  @Autowired
  public IssueReportServiceImpl(
      IssueReportRepository issueReportRepository,
      AcceptedStateRepository acceptedStateRepository,
      IssueTypeRepository issueTypeRepository
  ) {
    this.issueReportRepository = issueReportRepository;
    this.acceptedStateRepository = acceptedStateRepository;
    this.issueTypeRepository = issueTypeRepository;
  }

  @Override
  public Page<IssueReport> getAll(Pageable pageable) {
    return issueReportRepository.findAll(pageable);
  }

  @Override
  public Optional<IssueReport> getByExternalId(UUID externalId) {
    return issueReportRepository.findByExternalId(externalId);
  }

  @Override
  @Transactional
  public IssueReport setAcceptedState(UUID externalId, String statusTag) {
    IssueReport report = issueReportRepository
        .findByExternalId(externalId)
        .orElseThrow(NoSuchElementException::new);
    AcceptedState acceptedState = acceptedStateRepository
        .findByStatusTag(statusTag)
        .orElseThrow(NoSuchElementException::new);
    report.setAcceptedState(acceptedState);
    return issueReportRepository.save(report);
  }

  @Override
  @Transactional
  public IssueReport replaceIssueTypes(UUID externalId, Iterable<String> issueTypeTags) {
    IssueReport report = issueReportRepository
        .findByExternalId(externalId)
        .orElseThrow(NoSuchElementException::new);

    Set<String> requested = new LinkedHashSet<>();
    if (issueTypeTags != null) {
      for (String tag : issueTypeTags) {
        if (tag != null && !tag.isBlank()) {
          requested.add(tag);
        }
      }
    }
    Collection<String> requestedTags = requested;
    List<IssueType> resolved = issueTypeRepository.findAllByIssueTypeTagIn(requestedTags);
    if (resolved.size() != requestedTags.size()) {
      throw new IllegalArgumentException("Invalid issueTypeTags set.");
    }
    report.getIssueTypes().clear();
    report.getIssueTypes().addAll(resolved);
    return issueReportRepository.save(report);
  }

}

