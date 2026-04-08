/*
 *  Copyright 2026 CNM Ingenuity, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package edu.cnm.deepdive.seesomethingabq.controller;

import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReportStatusUpdateRequest;
import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReportSummary;
import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReportTypesUpdateRequest;
import edu.cnm.deepdive.seesomethingabq.model.dto.ManagerIssueReportResponse;
import edu.cnm.deepdive.seesomethingabq.model.entity.IssueReport;
import edu.cnm.deepdive.seesomethingabq.service.IssueReportService;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for manager-only issue report administration operations.
 */
@RestController
@RequestMapping("/manager/issue-reports")
public class ManagerIssueReportController {

  private static final int DEFAULT_PAGE_SIZE = 20;
  private static final int DEFAULT_PAGE_NUMBER = 0;

  private final IssueReportService service;

  /**
   * Creates a controller exposing manager-only issue report administration operations.
   *
   * @param service issue report service.
   */
  @Autowired
  public ManagerIssueReportController(IssueReportService service) {
    this.service = service;
  }

  /**
   * Returns a page of issue report summaries.
   *
   * @param pageSize page size.
   * @param pageNumber zero-based page index.
   * @return page of report summaries.
   */
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @Transactional(readOnly = true)
  public Page<IssueReportSummary> getAll(
      @RequestParam(defaultValue = "" + DEFAULT_PAGE_SIZE) int pageSize,
      @RequestParam(defaultValue = "" + DEFAULT_PAGE_NUMBER) int pageNumber
  ) {
    PageRequest pageable = PageRequest.of(
        pageNumber,
        pageSize,
        Sort.by(Direction.DESC, "timeLastModified")
    );
    return service.getAll(pageable).map(IssueReportSummary::fromIssueReport);
  }

  /**
   * Updates the accepted-state/status of an issue report.
   *
   * @param externalId report external ID.
   * @param request request payload containing the new status tag.
   * @return updated issue report.
   */
  @PutMapping(
      value = "/{externalId}/status",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public IssueReport updateStatus(
      @PathVariable UUID externalId,
      @RequestBody IssueReportStatusUpdateRequest request
  ) {
    return service.setAcceptedState(externalId, request.getStatusTag());
  }

  /**
   * Replaces the issue types associated with an issue report.
   *
   * @param externalId report external ID.
   * @param request request payload containing the replacement issue type tags.
   * @return updated issue report.
   */
  @PutMapping(
      value = "/{externalId}/issue-types",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public IssueReport updateIssueTypes(
      @PathVariable UUID externalId,
      @RequestBody IssueReportTypesUpdateRequest request
  ) {
    return service.replaceIssueTypes(externalId, request.getIssueTypeTags());
  }

}
