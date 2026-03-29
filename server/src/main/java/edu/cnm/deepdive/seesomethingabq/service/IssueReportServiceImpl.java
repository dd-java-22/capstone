package edu.cnm.deepdive.seesomethingabq.service;

import edu.cnm.deepdive.seesomethingabq.exception.AcceptedStateNotFoundException;
import edu.cnm.deepdive.seesomethingabq.exception.IssueReportNotFoundException;
import edu.cnm.deepdive.seesomethingabq.model.entity.AcceptedState;
import edu.cnm.deepdive.seesomethingabq.model.entity.IssueReport;
import edu.cnm.deepdive.seesomethingabq.model.entity.ReportLocation;
import edu.cnm.deepdive.seesomethingabq.model.entity.UserProfile;
import edu.cnm.deepdive.seesomethingabq.service.repository.AcceptedStateRepository;
import edu.cnm.deepdive.seesomethingabq.service.repository.IssueReportRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class IssueReportServiceImpl implements IssueReportService {

  private final IssueReportRepository issueReportRepository;
  private final UserService userService;
  private final AcceptedStateRepository acceptedStateRepository;

  @Autowired
  public IssueReportServiceImpl(
      IssueReportRepository issueReportRepository,
      UserService userService,
      AcceptedStateRepository acceptedStateRepository
  ) {
    this.issueReportRepository = issueReportRepository;
    this.userService = userService;
    this.acceptedStateRepository = acceptedStateRepository;
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

    if (defaultState != null) {
      report.setAcceptedState(defaultState);
    } else {
      throw new AcceptedStateNotFoundException("Default accepted state 'New' not found");
    }

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

  private IssueReport requireReport(UUID externalKey) {
    return issueReportRepository.findByExternalId(externalKey)
        .orElseThrow(() -> new IssueReportNotFoundException("Issue report not found: " + externalKey));
  }

}