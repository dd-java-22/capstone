package edu.cnm.deepdive.seesomethingabq.controller;

import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReportDto;
import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReportRequest;
import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReportSummary;
import edu.cnm.deepdive.seesomethingabq.model.entity.IssueReport;
import edu.cnm.deepdive.seesomethingabq.service.IssueReportService;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * REST controller exposing issue report operations for authenticated users.
 */
@RestController
@RequestMapping("/issue-reports")
public class IssueReportController {

  private static final int DEFAULT_PAGE_SIZE = 20;
  private static final int DEFAULT_PAGE_NUMBER = 0;

  private final IssueReportService issueReportService;

  /**
   * Creates a controller delegating issue report operations to the service layer.
   *
   * @param issueReportService service providing issue report operations.
   */
  public IssueReportController(IssueReportService issueReportService) {
    this.issueReportService = issueReportService;
  }

  /**
   * Returns summaries of issue reports owned by the currently authenticated user.
   *
   * @param sort sort key/direction parameter (passed through to the service).
   * @return list of report summaries.
   */
  @GetMapping(
      value = "/mine",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @Transactional(readOnly = true)
  public Page<IssueReportSummary> getMyReports(
      @RequestParam(defaultValue = "" + DEFAULT_PAGE_SIZE) int pageSize,
      @RequestParam(defaultValue = "" + DEFAULT_PAGE_NUMBER) int pageNumber
  ) {
    PageRequest pageable = PageRequest.of(
        pageNumber,
        pageSize,
        Sort.by(Direction.DESC, "timeLastModified")
    );
    return issueReportService.getReportsForCurrentUser(pageable).map(IssueReportSummary::fromIssueReport);
  }

  /**
   * Creates a new issue report for the currently authenticated user.
   *
   * @param request request payload describing the issue report.
   * @return response containing the created report and a {@code Location} header.
   */
  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<IssueReportDto> createReport(@RequestBody IssueReportRequest request) {
    IssueReport created = issueReportService.createReport(request);
    IssueReportDto dto = IssueReportDto.from(created);

    URI location = linkTo(IssueReportController.class)
        .slash(created.getExternalId())
        .toUri();

    return ResponseEntity.created(location).body(dto);

  }

  /**
   * Returns a single issue report by external identifier.
   *
   * @param externalId report external ID.
   * @return issue report entity.
   */
  @GetMapping("/{externalId}")
  public IssueReportDto getReport(@PathVariable UUID externalId) {
    IssueReport entity = issueReportService.getReportByExternalId(externalId);
    return IssueReportDto.from(entity);
  }


  /**
   * Updates an existing issue report.
   *
   * @param externalId report external ID.
   * @param request request payload describing updates to apply.
   * @return updated issue report entity.
   */
  @PutMapping("/{externalId}")
  public IssueReport updateReport(@PathVariable UUID externalId, @RequestBody IssueReportRequest request) {
    return issueReportService.updateReport(externalId, request);
  }

  /**
   * Deletes an issue report by external identifier.
   *
   * @param externalId report external ID.
   */
  @DeleteMapping("/{externalId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteReport(@PathVariable UUID externalId) {
    issueReportService.deleteReport(externalId);
  }


}
