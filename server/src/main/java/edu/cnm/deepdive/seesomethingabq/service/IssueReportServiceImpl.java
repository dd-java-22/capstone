package edu.cnm.deepdive.seesomethingabq.service;

import edu.cnm.deepdive.seesomethingabq.exception.AcceptedStateNotFoundException;
import edu.cnm.deepdive.seesomethingabq.exception.IssueReportNotFoundException;
import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReportRequest;
import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReportSummary;
import edu.cnm.deepdive.seesomethingabq.model.entity.AcceptedState;
import edu.cnm.deepdive.seesomethingabq.model.entity.IssueReport;
import edu.cnm.deepdive.seesomethingabq.model.entity.IssueType;
import edu.cnm.deepdive.seesomethingabq.model.entity.ReportLocation;
import edu.cnm.deepdive.seesomethingabq.model.entity.UserProfile;
import edu.cnm.deepdive.seesomethingabq.service.repository.AcceptedStateRepository;
import edu.cnm.deepdive.seesomethingabq.service.repository.IssueReportRepository;
import edu.cnm.deepdive.seesomethingabq.service.repository.IssueTypeRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class IssueReportServiceImpl implements IssueReportService {

  private final IssueReportRepository issueReportRepository;
  private final UserService userService;
  private final AcceptedStateRepository acceptedStateRepository;
  private final IssueTypeRepository issueTypeRepository;
  private final Validator validator;

  @Autowired
  public IssueReportServiceImpl(
      IssueReportRepository issueReportRepository,
      UserService userService,
      AcceptedStateRepository acceptedStateRepository,
      IssueTypeRepository issueTypeRepository,
      Validator validator
  ) {
    this.issueReportRepository = issueReportRepository;
    this.userService = userService;
    this.acceptedStateRepository = acceptedStateRepository;
    this.issueTypeRepository = issueTypeRepository;
    this.validator = validator;
  }

  @Override
  public List<IssueReportSummary> getReportsForCurrentUser(String sortParam) {
    UserProfile currentUser = userService.getCurrentUser();
    Sort sort = parseSort(sortParam);
    List<IssueReport> reports =
        issueReportRepository.findByUserProfile(currentUser, sort);
    return reports.stream()
        .map(this::toSummary)
        .toList();
  }

  @Override
  public IssueReport createReport(IssueReportRequest request) {
    // TODO: 2026-03-26 Confirm this is the correct user ownership behavior, or enforce ownership rules here
    UserProfile currentUser = userService.getCurrentUser();

    IssueReport report = new IssueReport();
    report.setUserProfile(currentUser);
    report.setTextDescription(request.getTextDescription());

    AcceptedState defaultState = acceptedStateRepository
        .findByStatusTag("New");

    if (defaultState != null) {
      report.setAcceptedState(defaultState);
    } else {
      throw new AcceptedStateNotFoundException("Default accepted state 'New' not found");
    }

    // reportLocation is required (IssueReport.reportLocation is optional=false).
    ReportLocation location = new ReportLocation();
    applyLocation(location, request);
    location.setIssueReport(report);
    report.setReportLocation(location);
    validate(location);

    // Resolve issueTypes from request tags (images are handled separately).
    report.getIssueTypes().clear();
    report.getIssueTypes().addAll(resolveIssueTypes(request.getIssueTypes()));

    return issueReportRepository.save(report);
  }

  @Override
  public IssueReport getReportByExternalId(UUID externalId) {
    return requireReport(externalId);
  }

  @Override
  public IssueReport updateReport(UUID externalId, IssueReportRequest request) {
    IssueReport existing = requireReport(externalId);

    // Server-controlled fields stay on 'existing':
    // - id, externalId, userProfile, acceptedState, timestamps

    existing.setTextDescription(request.getTextDescription());

    if (hasAnyLocationField(request)) {
      // Any location field present => treat as a location update; validate the resulting location.
      ReportLocation location = existing.getReportLocation();
      if (location == null) {
        location = new ReportLocation();
      }
      // Ensure both sides of the association are set consistently.
      location.setIssueReport(existing);
      existing.setReportLocation(location);
      applyLocation(location, request);
      validate(location);
    }

    if (request.getIssueTypes() != null) {
      // If a list is provided, fully replace issueTypes (do not keep stale associations).
      List<IssueType> resolved = resolveIssueTypes(request.getIssueTypes());
      existing.getIssueTypes().clear();
      existing.getIssueTypes().addAll(resolved);
    }

    return issueReportRepository.save(existing);
  }

  @Override
  public void deleteReport(UUID externalId) {
    issueReportRepository.delete(requireReport(externalId));
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

  private IssueReport requireReport(UUID externalId) {
    return issueReportRepository.findByExternalId(externalId)
        .orElseThrow(() -> new IssueReportNotFoundException("Issue report not found: " + externalId));
  }


  /**
   * Parses the {@code sortParam} string from the controller into a Spring Data {@link Sort}.
   * <p>
   * Supported formats:
   * <ul>
   *   <li>{@code "last_modified"} &rarr; sort by {@code timeLastModified} descending (default direction)</li>
   *   <li>{@code "last_modified,asc"} or {@code "last_modified,desc"}</li>
   *   <li>{@code "first_reported,asc"} or {@code "first_reported,desc"}</li>
   *   <li>Multiple clauses separated by {@code ';'}, e.g.
   *       {@code "last_modified,desc;first_reported,desc"}</li>
   * </ul>
   * Unknown field keys are ignored. If no valid clauses are found, the method
   * falls back to {@code timeLastModified} descending.
   */
  private Sort parseSort(String sortParam) {
    if (sortParam == null || sortParam.isBlank()) {
      return Sort.by(Sort.Order.desc("timeLastModified"));
    }
    Sort sort = Sort.unsorted();
    String[] clauses = sortParam.split(";");
    for (String clause : clauses) {
      String trimmed = clause.trim();
      if (trimmed.isEmpty()) {
        continue;
      }
      String[] parts = trimmed.split(",");
      String fieldKey = parts[0].trim();
      String direction = parts.length > 1 ? parts[1].trim().toLowerCase() : "desc";

      String property;
      switch (fieldKey) {
        case "last_modified":
          property = "timeLastModified";
          break;
        case "first_reported":
          property = "timeFirstReported";
          break;
        default:
          continue;
      }

      Sort.Order order = "asc".equals(direction)
          ? Sort.Order.asc(property)
          : Sort.Order.desc(property);

      sort = sort.and(Sort.by(order));
    }
    if (sort.isUnsorted()) {
      sort = Sort.by(Sort.Order.desc("timeLastModified"));
    }
    return sort;
  }

  private IssueReportSummary toSummary(IssueReport report) {
    IssueReportSummary dto = new IssueReportSummary();
    dto.setExternalId(report.getExternalId());
    dto.setDescription(report.getTextDescription());
    dto.setAcceptedState(report.getAcceptedState().getStatusTag());
    dto.setTimeFirstReported(report.getTimeFirstReported());
    dto.setTimeLastModified(report.getTimeLastModified());
    return dto;
  }

  private void applyLocation(ReportLocation location, IssueReportRequest request) {
    location.setLatitude(request.getLatitude());
    location.setLongitude(request.getLongitude());
    location.setStreetCoordinate(request.getStreetCoordinate());
    location.setLocationDescription(request.getLocationDescription());
  }

  private void validate(Object target) {
    var violations = validator.validate(target);
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException(violations);
    }
  }

  private boolean hasAnyLocationField(IssueReportRequest request) {
    return request.getLatitude() != null
        || request.getLongitude() != null
        || request.getStreetCoordinate() != null
        || request.getLocationDescription() != null;
  }

  private List<IssueType> resolveIssueTypes(Iterable<String> submittedTags) {
    LinkedHashSet<String> normalized = normalizeTags(submittedTags);
    if (normalized.isEmpty()) {
      return List.of();
    }

    List<IssueType> resolved = issueTypeRepository.findAllByIssueTypeTagIn(normalized);

    // Validate: reject any submitted nonblank tag that doesn't resolve to an IssueType.
    Set<String> resolvedTags = resolved.stream()
        .map(IssueType::getIssueTypeTag)
        .filter(Objects::nonNull)
        .collect(java.util.stream.Collectors.toSet());
    List<String> missing = normalized.stream()
        .filter(tag -> !resolvedTags.contains(tag))
        .toList();
    if (!missing.isEmpty()) {
      throw new IllegalArgumentException("Unrecognized issueTypes: " + missing);
    }

    // Preserve stable order of the request tags where practical.
    Map<String, IssueType> byTag = new HashMap<>();
    for (IssueType type : resolved) {
      if (type.getIssueTypeTag() != null) {
        byTag.put(type.getIssueTypeTag(), type);
      }
    }
    List<IssueType> ordered = new ArrayList<>(normalized.size());
    for (String tag : normalized) {
      IssueType type = byTag.get(tag);
      if (type != null) {
        ordered.add(type);
      }
    }
    return ordered;
  }

  private LinkedHashSet<String> normalizeTags(Iterable<String> submittedTags) {
    LinkedHashSet<String> normalized = new LinkedHashSet<>();
    if (submittedTags == null) {
      return normalized;
    }
    for (String tag : submittedTags) {
      if (tag == null) {
        continue;
      }
      String trimmed = tag.trim();
      if (!trimmed.isBlank()) {
        normalized.add(trimmed);
      }
    }
    return normalized;
  }
}
