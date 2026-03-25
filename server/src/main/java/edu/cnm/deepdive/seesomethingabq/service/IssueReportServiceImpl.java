package edu.cnm.deepdive.seesomethingabq.service;

import edu.cnm.deepdive.seesomethingabq.model.entity.IssueReport;
import edu.cnm.deepdive.seesomethingabq.service.dao.IssueReportRepository;
import edu.cnm.deepdive.seesomethingabq.service.repository.ReportLocationRepository;
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
  private final ReportLocationRepository reportLocationRepository;

  @Autowired
  public IssueReportServiceImpl(
      IssueReportRepository issueReportRepository,
      UserService userService,
      ReportLocationRepository reportLocationRepository) {
    this.issueReportRepository = issueReportRepository;
    this.userService = userService;
    this.reportLocationRepository = reportLocationRepository;
  }

  @Override
  public List<IssueReport> getReportsForCurrentUser(String sortParam) {
    return issueReportRepository.findAll();
  }

  @Override
  public IssueReport createReport(IssueReport report) {
    return issueReportRepository.save(report);
  }

  @Override
  public IssueReport getReportByExternalKey(UUID externalKey) {
    return issueReportRepository.findByExternalKey(externalKey)
        .orElseThrow(() -> new RuntimeException(externalKey + " not found"));
  }

  @Override
  public IssueReport updateReport(UUID externalKey, IssueReport report) {
    return issueReportRepository.save(report);
  }

  @Override
  public void deleteReport(UUID externalKey) {
    issueReportRepository.delete(getReportByExternalKey(externalKey));
  }
}
