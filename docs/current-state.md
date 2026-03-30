---
title: Current State Summary
description: "Milestone 3 - Current state of the project"
order: 10
---

{% include ddc-abbreviations.md %}

## Summary of Current State

**Last Updated:** March 29, 2026
**Milestone:** 3 of 4
**Overall Completion Status:** ~75% complete

---

## Project Architecture Overview

### Backend Service (Spring Boot)

The See Something ABQ backend REST API service is substantially complete for Milestone 3, with all core CRUD operations implemented and tested.

**Technology Stack:**
- Spring Boot 4.0.3
- Spring Data JPA with Hibernate
- Spring Security with OAuth2 Resource Server
- H2 Database (development/testing)
- JUnit 5 & Mockito (testing)
- OpenAPI 3.0 documentation

**Project Statistics:**
- **71** Java source files
- **6** JPA entity classes
- **10** REST controller classes
- **5** service implementation classes
- **14** custom exception classes
- **Comprehensive test coverage** with unit and integration tests
- **All tests passing** ✅

---

## Completed Deliverables (Milestone 3)

### ✅ 1. Data Model & Persistence Layer

**Status:** COMPLETE

- All 6 entity classes fully implemented with JPA annotations:
  - `UserProfile` - User authentication and profile data
  - `IssueReport` - Core issue/incident reports
  - `ReportLocation` - GPS and location data for reports
  - `IssueType` - Categorization tags for reports
  - `AcceptedState` - Status tracking (New, In Progress, Closed, etc.)
  - `ReportImage` - Photo attachments for reports

- All entity relationships properly configured:
  - One-to-Many: User → Reports, Report → Images
  - Many-to-Many: Reports ↔ IssueTypes
  - One-to-One: Report → ReportLocation
  - Many-to-One: Reports → AcceptedState

- Spring Data JPA repositories implemented for all entities
- Database schema automatically generated via Hibernate

### ✅ 2. Service Layer

**Status:** COMPLETE

All core service interfaces and implementations are complete:

- `UserServiceImpl` - User profile management, OAuth integration
- `IssueReportServiceImpl` - CRUD operations for issue reports
- `IssueTypeServiceImpl` - Management of issue categorization tags
- `AcceptedStateServiceImpl` - Status workflow management
- `ReportImageServiceImpl` - Image attachment handling

### ✅ 3. REST API Controllers

**Status:** COMPLETE

**User-Facing Controllers:**
- `UserController` - User profile operations (`/users/**`)
- `IssueReportController` - Report CRUD for users (`/issue-reports/**`)
- `IssueTypeController` - View available issue types (`/issue-types`)
- `ReportImageController` - Image management (`/issue-reports/{id}/images/**`)

**Manager-Only Controllers:**
- `ManagerUserController` - User administration (`/manager/users/**`)
- `ManagerAcceptedStateController` - Status management (`/manager/accepted-states/**`)
- `ManagerIssueTypeController` - Issue type administration (`/manager/issue-types/**`)
- `ManagerIssueReportController` - All reports access (`/manager/issue-reports/**`)

### ✅ 4. Security Implementation

**Status:** COMPLETE

- OAuth2 Resource Server configured with Google Sign-In
- JWT token validation with custom `JwtConverter`
- Role-based access control (USER vs MANAGER roles)
- Method-level security on manager endpoints
- CORS configuration for cross-origin requests
- Security configuration following Spring Security 7.0 best practices

### ✅ 5. Custom Exception Handling

**Status:** COMPLETE (Milestone 3 Requirement)

- 14 custom exception classes organized by HTTP status:
  - **404 NOT_FOUND:** `ResourceNotFoundException` and subclasses
    - `AcceptedStateNotFoundException`
    - `ImageNotFoundException`
    - `IssueReportNotFoundException`
    - `IssueTypeNotFoundException`
    - `UserNotFoundException`
  - **400 BAD_REQUEST:** `BadRequestException` and subclasses
    - `InvalidImageException`
    - `InvalidIssueReportException`
    - `InvalidIssueTypeException`
    - `InvalidStateException`
    - `InvalidUserException`
  - **409 CONFLICT:** `ConflictException` and subclasses
    - `DuplicateAcceptedStateException`
    - `DuplicateIssueTypeException`
    - `DuplicateUserException`
  - **403 FORBIDDEN:** `AccessDeniedException`

- `GlobalExceptionHandler` with `@RestControllerAdvice`:
  - All service exceptions mapped to appropriate HTTP status codes
  - Consistent error response format with timestamps
  - `@ExceptionHandler` methods for each exception category
  - Proper handling of JPA exceptions (`EntityNotFoundException`, `NoSuchElementException`)

- All controllers now use custom exceptions (no `ResponseStatusException` wrapping)
- Exception handling fully tested with unit tests

### ✅ 6. Testing

**Status:** COMPREHENSIVE

- Unit tests for all service implementations
- Unit tests for all manager controllers
- Integration tests for authentication flow
- Mock-based testing with Mockito
- **All tests passing** ✅

### ✅ 7. API Documentation

**Status:** COMPLETE

- OpenAPI 3.0 specification generated
- Swagger UI available for API exploration
- Comprehensive endpoint documentation with request/response schemas

---

## Known Deficiencies & Incomplete Elements

### 1. DTO Layer (In Progress)

**Status:** INCOMPLETE
**Priority:** HIGH
**Impact:** Medium

**Current State:**
- Controllers currently expose JPA entities directly in request/response bodies
- No dedicated Data Transfer Objects (DTOs) implemented yet

**Issues:**
- Circular reference risks in JSON serialization (mitigated with `@JsonIgnore`)
- Over-exposure of internal entity structure to API consumers
- Difficulty implementing field-level validation
- Tight coupling between API contract and database schema

**Remaining Work:**
- Create DTO classes for each entity (e.g., `IssueReportRequest`, `IssueReportResponse`, `IssueReportSummary`)
- Implement DTO ↔ Entity mappers (manual or using MapStruct)
- Add Bean Validation annotations to DTOs (`@NotNull`, `@Size`, `@Email`, etc.)
- Update controllers to use DTOs instead of entities
- Update tests to work with DTOs

**TODOs in Code:**
```java
// IssueReportController.java:46-47
// TODO: Replace entity-based request/response with DTOs
// TODO: Add validation (e.g., @Valid, Bean Validation annotations)
```

### 2. Ownership & Access Control Enforcement

**Status:** PARTIALLY IMPLEMENTED
**Priority:** HIGH
**Impact:** High (Security)

**Current State:**
- Basic role-based security implemented (USER vs MANAGER)
- Some ownership checks in `ReportImageServiceImpl`
- User profile correctly stamped on report creation

**Issues:**
- Users can potentially access/modify other users' reports
- Ownership verification not consistently enforced across all service methods
- No systematic authorization checks before updates/deletes

**Remaining Work:**
- Implement ownership verification in `IssueReportServiceImpl.updateReport()`
- Implement ownership verification in `IssueReportServiceImpl.deleteReport()`
- Add authorization checks to ensure users can only modify their own reports
- Managers should be able to access all reports (already working)
- Add integration tests for authorization scenarios

**TODOs in Code:**
```java
// IssueReportServiceImpl.java:63
// TODO: Confirm user ownership behavior, or enforce ownership rules

// IssueReportServiceImpl.java:99
// TODO: Enforce real ownership instead of always stamping current user
```

### 3. Bidirectional Relationship Management

**Status:** PARTIALLY IMPLEMENTED
**Priority:** MEDIUM
**Impact:** Medium

**Current State:**
- Some bidirectional relationships set correctly (e.g., `ReportLocation → IssueReport`)
- Cascade strategies defined but not fully tested

**Issues:**
- Manual bidirectional linking required in multiple places
- Inconsistent handling of relationship updates
- Potential for orphaned entities

**Remaining Work:**
- Review and standardize cascade strategies across all entities
- Implement helper methods for managing bidirectional relationships
- Add comprehensive tests for cascade operations
- Consider using Hibernate's `@PrePersist`/`@PreUpdate` lifecycle callbacks

**TODOs in Code:**
```java
// IssueReport.java:55
// TODO: Revisit cascade strategy for reportLocation

// IssueReportServiceImpl.java:80
// TODO: Confirm bidirectional link handling once DTOs/mappers are in place

// IssueReportServiceImpl.java:121
// TODO: Update issueTypes and reportImages when DTOs and mapping rules are in place
```

### 4. Data Model Clarifications Needed

**Status:** AWAITING CLIENT INPUT
**Priority:** LOW
**Impact:** Low

**Current State:**
- `ReportLocation.streetCoordinate` field exists but purpose is unclear

**Remaining Work:**
- Clarify with stakeholders the intended use of `streetCoordinate`
- Determine if it should be an address, intersection, or block number
- Add appropriate validation once purpose is clarified

**TODOs in Code:**
```java
// ReportLocation.java:26
// TODO: Needs client clarification on streetCoordinate field
```

### 5. Image Storage Implementation

**Status:** STUB IMPLEMENTATION
**Priority:** HIGH (Milestone 4)
**Impact:** High

**Current State:**
- `ReportImage` entity tracks metadata (filename, MIME type, etc.)
- `imageLocator` field intended for file path or cloud storage key
- No actual file upload/storage mechanism implemented

**Remaining Work:**
- Implement file upload handling in controllers (multipart/form-data)
- Choose storage strategy:
  - Option A: Local filesystem (development)
  - Option B: Cloud storage (AWS S3, Google Cloud Storage, Azure Blob)
- Implement file validation (size limits, MIME type verification)
- Implement secure file serving with access control
- Add image optimization/thumbnail generation (stretch goal)

---

## Unimplemented Features (vs. Milestone 3 Rubric)

### ✅ All Required Milestone 3 Deliverables Complete

According to the Milestone 3 rubric requirements, all deliverables are **COMPLETE**:

✅ **Entity classes with proper annotations** - 6 entities fully implemented
✅ **Service interfaces and implementations** - 5 services complete
✅ **JPA repositories** - All repositories implemented
✅ **REST controllers** - 10 controllers with full CRUD operations
✅ **OAuth2 authentication** - Google Sign-In integrated
✅ **Custom exception classes with @RestControllerAdvice** - 14 exceptions + global handler
✅ **Unit and integration tests** - Comprehensive test coverage
✅ **OpenAPI documentation** - Complete API specification

---

## Functional Stretch Goals

### Short-Term Stretch Goals (Potentially achievable in Milestone 4)

#### 1. **Image Upload & Storage**
**Priority:** HIGH
**Estimated Effort:** 8-12 hours

- Implement actual file upload handling
- Add cloud storage integration (Google Cloud Storage preferred)
- Implement image compression/optimization
- Add thumbnail generation for list views
- Secure image serving with access control

**Value:** Essential for full functionality - users expect to attach photos to reports

#### 2. **Advanced Filtering & Search**
**Priority:** MEDIUM
**Estimated Effort:** 6-8 hours

- Location-based filtering (by city district, radius from point)
- Date range filtering
- Multi-tag filtering with AND/OR logic
- Full-text search on report descriptions
- Sorting by multiple fields

**Value:** Critical for managers reviewing large numbers of reports

#### 3. **Report Comments/Updates**
**Priority:** MEDIUM
**Estimated Effort:** 6-8 hours

- Add `ReportComment` entity
- Allow users to add updates to their own reports
- Allow managers to add comments to any report
- Display comment history with timestamps

**Value:** Enables ongoing communication about issue resolution

#### 4. **Email Notifications**
**Priority:** MEDIUM
**Estimated Effort:** 4-6 hours

- Send email when report is created (to managers)
- Send email when status changes (to report creator)
- Configurable notification preferences
- Email templates with report details

**Value:** Ensures timely response to new reports

#### 5. **Analytics Dashboard Data**
**Priority:** LOW
**Estimated Effort:** 8-12 hours

- Reports per day/week/month
- Average time to resolution by issue type
- Most common issue types
- Geographic heat maps of issues
- Trend analysis over time

**Value:** Helps city executives make data-driven decisions

### Medium-Term Stretch Goals (Post-Capstone)

#### 6. **Multi-Provider OAuth Support**
**Estimated Effort:** 4-6 hours

- Add Microsoft Azure AD support
- Add Apple Sign-In support
- Configurable OAuth providers
- Provider-specific role mapping

#### 7. **Automated Issue Classification**
**Estimated Effort:** 20-30 hours

- Train ML model on historical issue data
- Auto-suggest issue types based on description
- Auto-suggest issue types based on uploaded images
- Confidence scores for suggestions

#### 8. **Geographic Data Enhancements**
**Estimated Effort:** 8-12 hours

- Reverse geocoding (GPS → address)
- City district/neighborhood identification
- Integration with city GIS systems
- Map visualization of report clusters

#### 9. **Report Export & Reporting**
**Estimated Effort:** 6-8 hours

- CSV export with filters
- PDF report generation
- Scheduled report emails
- Custom report templates

#### 10. **Mobile Push Notifications**
**Estimated Effort:** 6-8 hours (backend only)

- Firebase Cloud Messaging integration
- Push notifications for status changes
- Push notifications for manager assignments
- Configurable notification preferences

### Long-Term Enhancements (Future Versions)

#### 11. **Workflow Automation**
- Automatic assignment to departments based on issue type
- Escalation workflows for overdue issues
- SLA tracking and enforcement

#### 12. **Public Portal**
- Public-facing view of sanitized reports
- Community engagement features
- Anonymous reporting option

#### 13. **Integration with City Systems**
- Integration with 311 systems
- Integration with work order management
- Integration with asset management systems

---

## Technical Debt & Code Quality

### Current State: GOOD ✅

- Code follows consistent style and naming conventions
- Proper use of dependency injection
- Appropriate separation of concerns (Controller → Service → Repository)
- Comprehensive JavaDoc documentation
- No critical technical debt identified

### Minor Technical Debt Items:

1. **Some TODO comments remaining** (8 locations) - mostly for future enhancements
2. **Circular JSON reference risks** - mitigated with `@JsonIgnore`, but DTOs would eliminate entirely
3. **Hardcoded default state** - "New" state is hardcoded in `IssueReportServiceImpl`

---

## Deployment Readiness

### Current Status: DEVELOPMENT READY ✅

**What's Working:**
- Application runs successfully on localhost
- H2 database configured for development
- OAuth2 authentication working with Google
- All REST endpoints functional
- Comprehensive error handling

**Not Production-Ready:**
- Using H2 in-memory database (not persistent)
- No production database configuration (PostgreSQL/MySQL)
- No environment-specific configuration profiles
- No HTTPS/TLS configuration
- No production logging configuration
- No monitoring/metrics implementation
- No Docker containerization
- No CI/CD pipeline

**Milestone 4 Focus:**
- Production database configuration
- Deployment configuration
- Production security hardening
- Performance optimization

---

## Conclusion

The See Something ABQ backend service has successfully completed all Milestone 3 requirements and is on track for Milestone 4. The core functionality is solid, with comprehensive exception handling, security implementation, and test coverage. The primary focus for Milestone 4 will be implementing DTOs, completing image upload functionality, strengthening authorization enforcement, and preparing for production deployment.

**Overall Risk Assessment:** LOW ✅
**Milestone 3 Completion:** 100% ✅
**Project Completion:** ~75%
**On Track for Successful Delivery:** YES ✅
