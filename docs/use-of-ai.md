---
title: Use of AI
description: "Documentation of AI tool usage across all project sprints."
order: 30
---

{% include ddc-abbreviations.md %}

## Page contents
{:.no_toc}

- ToC
{:toc}

## Overview

This document tracks all significant uses of AI tools throughout the development of this project. It is maintained across sprints and updated as new AI-assisted work is completed. Per project requirements, detail is provided on the specific tasks completed and components produced with AI assistance.

## Sprint 1

No AI tools were used during this sprint.

## Sprint 2

### Document generation

**Tool used:** Perplexity AI


**Task:** Drafting the outline and structure for this `use-of-ai.md` document.


**Prompt used:**

> "We missed the Use of AI item in Milestone 1. We need to add it in Milestone 2 as a markdown file. Help me draft an outline that will allow us to easily add cases where AI was utilized in the future. Include the use of this prompt to generate the document itself."

**Output produced:** Initial structure and template for this document, including front matter, section headers, field conventions, and placeholder guidance for future sprints.


**Human review/modification:** [Moved this entry to Sprint 2 as it was not completed in Sprint 1]

---

### Rest controller

**Tool used:** Claude Agent


**Task:** Creating a REST Controller and implementing the application logic services for this project.


**Prompt used:**
> "Can you please help me create a user controller class that supports the GET /users/me endpoint, and returns the requested user's profile? The User controller class must be located in the controller subpackage of the main project package. The controller class must not perform any persistence operations directly, instead it should delegate these to business-logic interfaces. I also need help creating  a user service interface and implementation class. The user service interface must be specified as a parameter type in the constructor of the user controller class. The class must be annotated with the @Service annotation, and it must be located in the service subpackage. Examples of functionality that warrants the use of service classes include:
Identifying users based on their OAuth2.0 information and creating new user records as necessary.
Persistence of entity instances.
Relays of data sourced from external services.
Batch updates to the server datastore.
Finite state machine processing (e.g., for games)."

**Output produced:** A user controller class and user service interface and implementation class.


**Human review/modification:** Class names were changed to match the project naming convention, a class was moved to the correct package, and methods were changed, removed, or added as needed.

---

### Rest controller tests

**Tool used:** Claude Agent


**Task:** Creating test classes for the user controller and user service interface and implementation class.


**Prompt used:**
> "Can you please help me create test classes for the user controller and user service interface and implementation class"

**Output produced:** Test classes for the user controller and user service interface and implementation class.

**Human review/modification:** Methods within the class were changed, added, or  removed as needed.

---

**Tool used:** Claude Agent


**Task:** Updating test classes for the user controller and user service interface and implementation class after a significant changes to the current build.


**Prompt used:**
> "Could you please edit the existing test classes in this project to fit the current build and ensure that all of our code is working as intended?"

**Output produced:** Test classes for the user controller and user service interface were updated to match the current build.

**Human review/modification:** Test classes were reviewed by team members to ensure that they are still passing.

---

### OpenAPI spec generation

**Tool used:** ChatGPT  


**Task:** Generate draft of OpenAPI spec based on ERD and expected endpoints   


**Prompt used:**
> I had a conversation with ChatGPT where I provided a PDF of the milestone 2 requirements, our ERD.svg, and a zip of our entity classes.
> I asked it what else it needed in order to generate a spec, and provided all of the information it asked for.
> After that, it generated the open-api.yaml file.

**Output produced:** open-api.yaml  


**Human review/modification:** Review will happen in the PR, and I will update anything we find after we discuss as a team 

---

### Entity class generation

**Tool used:** ChatGPT + Claude  


**Task:** Create entity classes from ERD 


**Prompt used:**
> I gave the ERD diagram, milestone spec, and a zip of the entity classes I had made to ChatGPT.
> I asked it to check my work against the ERD and then generate a sequence of prompts to give
> to Claude to finish the entity classes. I also used ChatGPT to verify the classes after
> Claude had finished making them.

**Output produced:** All of the entity classes  


**Human review/modification:** We went through the entity classes together in class and added more annotations to them.

---

### mermaid-converter.bat

**Tool used:** Claude

**Task:** Automate mermaid publishing

**Prompt used:**
> i got the official dockerhub image of mermaid cli. How do I use it from docker desktop

then later...
> can you give me a script file to run that runs these mermaid conversions?

**Output produced:** convert-mermaid.bat which publishes everything in docs/mermaid to docs/img and 
docs/pdf

**Human review/modification:** Debugged some issues in the bat file (e.g. linux file endings workaround)

---

### Ticket 36 — Login on New Activities

**Tool used:** ChatGPT + Claude

**Prompt used:**  
Discussed refactoring approach to reuse existing login logic across new activities. Generated guidance and implementation approach, then used Claude to apply the changes.

**Output produced:**  
Updated activities to use existing login flow, removed unused `MainActivity`, and ensured consistent authentication handling across user and manager flows.

**Human review/modification:**  
PR review + manual testing

---

### Ticket 34 — Basic Navigation

**Tool used:** ChatGPT + Claude

**Prompt used:**  
Discussed navigation structure and provided requirements. Generated implementation approach for activities, fragments, and navigation flow, then used Claude to implement changes.

**Output produced:**  
Created user and manager activities, added placeholder fragments for all expected screens, and implemented initial navigation between fragments.

**Human review/modification:**  
PR review + manual testing

---
## Sprint 3 

### [User Endpoints and User Role Implementation]

**Tool used:** [Claude Agent]  
**Task:** [Implement the functionality of the UserController endpoints and service interface.]  
**Prompt used:**
> [Could you please help me with writing the code for the user controller class and service implementation of this project? Here are the additions I would like to make to the UserController class: "Controller — UserController (already exists, needs additions)
GET /users/me
PUT /users/me/display-name
PUT /users/me/email
PUT /users/me/avatar
Implement functions in the service interface + implementation and in the repository that are needed for the round trip.
Remove the getAll Mapping from the UserController but, leave the service and repository layers alone."
Do you need any additional information to implement the functions of the service interface or anything in the repository?
]

---
 
### Ticket 39 & 42 — OpenAPI Update + Security Consistency

**Tool used:** ChatGPT

**Prompt used:**  
Discussed current API state and provided project zip. Requested regeneration of the OpenAPI specification to reflect current endpoints and to resolve inconsistent use of the `security` property by standardizing on global JWT bearer authentication.

**Output produced:**  
Generated updated `open-api.yaml` aligned with current controllers and endpoints, removed redundant per-endpoint security definitions, and standardized authentication documentation.

**Human review/modification:**  
PR review + manual testing

---

### Ticket 58 — Manager Issue Reports Endpoints

**Tool used:** ChatGPT + Codex

**Prompt used:**  
Reviewed requirements and provided project zip. Generated a sequence of structured prompts for Codex to implement `/manager/issue-reports` endpoints, including controller, service, repository updates, DTOs, and tests.

**Output produced:**  
Implemented manager-specific issue report endpoints with corresponding controller, service layer logic, DTOs, and supporting repository methods.

**Human review/modification:**  
PR review + manual testing

---

### Ticket 59 — Manager Accepted States Endpoints

**Tool used:** ChatGPT + Codex

**Prompt used:**  
Reviewed ticket requirements and provided project zip. Generated step-by-step Codex prompts to implement `/manager/accepted-states` endpoints, including PATCH behavior and validation constraints.

**Output produced:**  
Added manager endpoints for accepted states, including controller, service methods, DTO handling, and repository integration.

**Human review/modification:**  
PR review + manual testing

---

### Ticket 57 — Manager Users Endpoints

**Tool used:** ChatGPT + Codex

**Prompt used:**  
Discussed requirements and provided project zip. Generated Codex prompt sequence to implement `/manager/users` endpoints, including account enable/disable and manager role updates.

**Output produced:**  
Implemented manager user administration endpoints with controller logic, service layer updates, and supporting DTOs.

**Human review/modification:**  
PR review + manual testing

---

### SQL Seed Data Generation

**Tool used:** ChatGPT

**Prompt used:**  
Provided database schema and requested generation of an idempotent SQL seed script covering all tables, including relationships and sample data.

**Output produced:**  
Generated SQL file with sample data for all tables, including relationship mappings and multiple example records.

**Human review/modification:**  
PR review + manual testing

---

### Issue Report endpoints (Ticket #54)

**Tool used:** Perplexity AI & Claude Sonnet 4.6


**Task:** Creation of controller, repository, and service classes associated with Issue Report endpoints.


**Prompt used:**
> Several prompts were used. Firt to generate the classes themselves, and then to assist in the debugging of any issues that arose.

**Output produced:** `IssueReportController`, `IssueReportService`, `IssueReportServiceImpl`, `IssueReportRepository` classes/interfaces.


**Human review/modification:** Reviewed each file and shaped the naming and styles to fit the standards consistent with the rest of the project. Subject to peer review in the PR as well.

---

### Sort param fix (Ticket #76)

**Tool used:** Perplexity AI & Claude Sonnet 4.6


**Task:** Implement flexible sorting and summary DTO for current-user issue reports.


**Prompt used:**
> Provided context and requested solution that incorporated a DTO.

**Output produced:**

Created `IssueReportSummary` DTO

Updated `IssueReportController`:
- GET /issue-reports/mine now returns `List<IssueReportSummary>`.
- Continues to accept sort query parameter (default "last_modified") and passes it to the service.

Updated `IssueReportService` / `IssueReportServiceImpl`:
- `getReportsForCurrentUser(String sortParam)` now returns List<IssueReportSummary>.
- Introduced `parseSort(String sortParam)` to convert values like last_modified,asc or first_reported,desc (and multi-clause strings) into a Spring Sort.
- Maps `IssueReport` entities to `IssueReportSummary` via a new `toSummary` helper.

Updated `IssueReportRepository`:
- Added `List<IssueReport> findByUserProfile(UserProfile userProfile, Sort sort)` to support flexible sorting for the current-user reports query.


**Human review/modification:** Reviewed the changes, and worked out some bugs as well as modifications to some of the naming. Tested all sorted endpoints to confirm desired functionality. PR was reviewed by others prior to merging.

---
### Report Image endpoints (Ticket #56)

**Tool used:** Claude Agent


**Task:** Creation of controller, repository, and service classes associated with Report Image endpoints.


**Prompt used:**
> Several prompts were used. Initially to generate the classes for the contoller, repository, and service interface, and then to assist in creating test classes to ensure the service layer is working as expected.

**Output produced:** `ReportImageController`, `ReportImageService`, `ReportImageServiceImpl`, `IssueReportRepository` classes/interfaces.


**Human review/modification:** Reviewed each file and shaped the naming and styles to fit the standards consistent with the rest of the project. Subject to peer review in the PR as well.

---
### Custom Exception Classes (Ticket #66)

**Tool used:** Claude Agent & MS Copilot.


**Task:** Creation of custom exception classes and the REST controller advice for handling of them in the application.


**Prompt used:**
> Several prompts were used. The first created the custim exception classes, and the next mapped them to appropriate HTTP status codes using a combination of @ExcdptionHandler and @ResponseStatus in the @RestControllerAdvice-annotated class.

**Output produced:** `IssueReportController`, `IssueReportService`, `IssueReportServiceImpl`, `IssueReportRepository` classes/interfaces.


**Human review/modification:** Reviewed each file and shaped the naming and styles to fit the standards consistent with the rest of the project. Subject to peer review in the PR as well.

---
### Summary of Current State (Ticket #40)

**Tool used:** Claude Agent


**Task:** Creation of the current-state.md file in the docs directory. 


**Prompt used:**
> "Please summarize the current state of the project based on the current state if readiness for the project.

**Output produced:** `current-state.md`.


**Human review/modification:** Reviewed each file and shaped the naming and styles to fit the standards consistent with the rest of the project. Subject to peer review in the PR as well.

---

## Sprint 4

### AI Usage Summary - Kevin - (Milestone 4)

During this milestone, I used AI tools (ChatGPT and Codex) extensively to assist with development, debugging, and documentation.

AI was primarily used for:

- **Feature implementation:**  
  Designing and implementing the user settings/profile page, avatar upload and display functionality, and UI improvements for report detail screens. ChatGPT was used to plan changes and generate structured prompts, and Codex was used to apply those changes to the codebase.

- **Backend development:**  
  Implementing avatar upload and retrieval endpoints, improving API consistency (e.g., ensuring PATCH `/users/me` returns the same DTO as GET), and adding proper exception handling (such as returning HTTP 415 for unsupported media types).

- **Debugging and issue resolution:**  
  Diagnosing and fixing several issues, including:
    - avatar upload failures due to incorrect MIME types
    - image loading failures caused by authentication (401 errors with Glide)
    - incorrect report count values being reset after profile updates
    - UI inconsistencies and redundant elements

- **Refactoring and cleanup:**  
  Removing redundant UI elements (duplicate headers, unused fields), improving data consistency between frontend and backend, and refining overall structure without changing functionality.

- **Documentation:**  
  Generating Javadoc and KDoc comments across the codebase to meet milestone requirements. AI was used to identify missing documentation and generate appropriate comments for public classes and members.

- **Workflow support:**  
  ChatGPT was used to analyze problems, suggest architectural approaches, and generate precise prompts for Codex. Codex was then used in agent mode to implement changes efficiently.

Overall, AI tools were used as a development assistant to accelerate implementation, help diagnose issues, and ensure code quality and documentation compliance.

All AI-generated code and suggestions were reviewed and tested manually before being accepted.

---

<!--
Use the following template for each AI-assisted task:

### [Short task description]

**Tool used:** [e.g., Perplexity AI, GitHub Copilot, ChatGPT, Junie]  


**Task:** [What were you trying to accomplish?]  


**Prompt used:**
> [Paste the exact prompt or a close paraphrase]

**Output produced:** [Describe the artifact — e.g., a method, a class, a test, a SQL schema] 


**Human review/modification:** [Describe what was changed, corrected, or validated by a team member]

If no AI was used, replace the above with:
> No AI tools were used during this sprint.
-->
