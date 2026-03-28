package edu.cnm.deepdive.seesomethingabq.service;

import edu.cnm.deepdive.seesomethingabq.model.entity.AcceptedState;
import edu.cnm.deepdive.seesomethingabq.model.entity.IssueReport;
import edu.cnm.deepdive.seesomethingabq.model.entity.IssueType;
import edu.cnm.deepdive.seesomethingabq.model.entity.ReportLocation;
import edu.cnm.deepdive.seesomethingabq.model.entity.UserProfile;
import edu.cnm.deepdive.seesomethingabq.service.repository.AcceptedStateRepository;
import edu.cnm.deepdive.seesomethingabq.service.repository.IssueReportRepository;
import edu.cnm.deepdive.seesomethingabq.service.repository.IssueTypeRepository;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class IssueReportServiceImpl implements IssueReportService {

  private final IssueReportRepository issueReportRepository;
  private final UserService userService;
  private final AcceptedStateRepository acceptedStateRepository;
  private final IssueTypeRepository issueTypeRepository;

  @Autowired
  public IssueReportServiceImpl(
    IssueReportRepository issueReportRepository,
    UserService userService,
    AcceptedStateRepository acceptedStateRepository,
    IssueTypeRepository issueTypeRepository
  ) {
    this.issueReportRepository = issueReportRepository;
    this.userService = userService;
    this.acceptedStateRepository = acceptedStateRepository;
    this.issueTypeRepository = issueTypeRepository;
  }

  @Override
  public List<IssueReport> getReportsForCurrentUser(String sortParam) {
    UserProfile user = userService.getCurrentUser();
    return issueReportRepository
      .getIssueReportsByUserProfileOrderByTimeFirstReportedDesc(user);
  }

  @Override
  public IssueReport createReport(IssueReport report) {
    // TODO: 2026-03-26 Confirm this is the correct user ownership behavior, or enforce ownership rules here
    UserProfile currentUser = userService.getCurrentUser();
    report.setUserProfile(currentUser);

    AcceptedState defaultState = acceptedStateRepository
      .findByStatusTag("New");

    if (defaultState == null) {
      throw new IllegalStateException("Default accepted state 'New' not found.");
    }

    report.setAcceptedState(defaultState);

    ReportLocation location = report.getReportLocation();
    if (location != null) {
      // TODO: 2026-03-26 Confirm bidirectional link handling once DTOs/mappers are in place
      location.setIssueReport(report);
    }

    return issueReportRepository.save(report);
  }

  @Override
  public IssueReport getReportByExternalKey(UUID externalKey) {
    return requireReport(externalKey);
  }

  @Override
  public IssueReport updateReport(UUID externalKey, IssueReport report) {
    IssueReport existing = requireReport(externalKey);

    // Server-controlled fields stay on 'existing':
    // - id, externalId, userProfile, acceptedState, timestamps

    // TODO: 2026-03-26 Enforce real ownership instead of always stamping current user
    UserProfile currentUser = userService.getCurrentUser();
    existing.setUserProfile(currentUser);

    // Copy editable fields from incoming 'report' into 'existing'.
    existing.setTextDescription(report.getTextDescription());

    ReportLocation incomingLocation = report.getReportLocation();
    if (incomingLocation != null) {
      ReportLocation existingLocation = existing.getReportLocation();
      if (existingLocation == null) {
        existingLocation = incomingLocation;
        existingLocation.setIssueReport(existing);
        existing.setReportLocation(existingLocation);
      } else {
        existingLocation.setLatitude(incomingLocation.getLatitude());
        existingLocation.setLongitude(incomingLocation.getLongitude());
        existingLocation.setStreetCoordinate(incomingLocation.getStreetCoordinate());
        existingLocation.setLocationDescription(incomingLocation.getLocationDescription());
      }
    }

    // TODO: 2026-03-27 Update issueTypes and reportImages when DTOs and mapping rules are in place.

    return issueReportRepository.save(existing);
  }

  @Override
  public void deleteReport(UUID externalKey) {
    issueReportRepository.delete(requireReport(externalKey));
  }

  @Override
  public Page<IssueReport> getAll(Pageable pageable) {
    return issueReportRepository.findAll(pageable);
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

    List<IssueType> resolved = issueTypeRepository.findAllByIssueTypeTagIn(requested);
    if (resolved.size() != requested.size()) {
      throw new IllegalArgumentException("Invalid issueTypeTags set.");
    }
    report.getIssueTypes().clear();
    report.getIssueTypes().addAll(resolved);
    return issueReportRepository.save(report);
  }

  @Override
  @Transactional
  public IssueReport setAcceptedState(UUID externalId, String statusTag) {
    IssueReport report = issueReportRepository
      .findByExternalId(externalId)
      .orElseThrow(NoSuchElementException::new);

    AcceptedState acceptedState = acceptedStateRepository
      .findByStatusTag(statusTag);

    if (acceptedState != null) {
      report.setAcceptedState(acceptedState);
    } else {
      throw new NoSuchElementException();
    }

    return issueReportRepository.save(report);
  }

  private IssueReport requireReport(UUID externalKey) {
    return issueReportRepository.findByExternalId(externalKey)
      .orElseThrow(() -> new RuntimeException(externalKey + " not found"));
    // TODO: 3/26/2026 change RuntimeException to appropriate @RestControllerAdvice
    //  custom exception when ticket #66 is complete.
  }

}