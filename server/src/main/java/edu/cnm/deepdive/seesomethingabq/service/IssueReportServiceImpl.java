package edu.cnm.deepdive.seesomethingabq.service;

import edu.cnm.deepdive.seesomethingabq.model.entity.IssueReport;
import edu.cnm.deepdive.seesomethingabq.model.entity.ReportLocation;
import edu.cnm.deepdive.seesomethingabq.model.entity.UserProfile;
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

  @Autowired
  public IssueReportServiceImpl(
      IssueReportRepository issueReportRepository,
      UserService userService) {
    this.issueReportRepository = issueReportRepository;
    this.userService = userService;
  }


  @Override
  public List<IssueReport> getReportsForCurrentUser(String sortParam) {
    UserProfile user = userService.getCurrentUser();
    return issueReportRepository.getIssueReportsByUserProfileOrderByTimeFirstReportedDesc(user);
  }

  @Override
  public IssueReport createReport(IssueReport report) {
    // TODO: 2026-03-26 Confirm this is the correct user ownership behavior, or enforce ownership rules here
    UserProfile currentUser = userService.getCurrentUser();
    report.setUserProfile(currentUser);

    // TODO: 2026-03-26 Set default AcceptedState (e.g., PENDING) instead of leaving null
    //  report.setAcceptedState(defaultState);

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
    // TODO: 2026-03-26 Enforce real ownership instead of always stamping current user
    UserProfile currentUser = userService.getCurrentUser();
    report.setUserProfile(currentUser);
    ReportLocation location = report.getReportLocation();
    if (location != null) {
      location.setIssueReport(report);
    }
    return issueReportRepository.save(report);
  }

  @Override
  public void deleteReport(UUID externalKey) {
    issueReportRepository.delete(requireReport(externalKey));
  }


  private IssueReport requireReport(UUID externalKey) {
    return issueReportRepository.findByExternalId(externalKey)
        .orElseThrow(() -> new RuntimeException(externalKey + " not found"));
    // TODO: 3/26/2026 change RuntimeException to appropriate @RestControllerAdvice
    //  custom exception when ticket #66 is complete.
  }
}
