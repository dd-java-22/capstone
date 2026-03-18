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

[No AI tools were used during this sprint.]: #

## Sprint 2

### Document generation

**Tool used:** Perplexity AI
**Task:** Drafting the outline and structure for this `use-of-ai.md` document.  
**Prompt used:**
> "We missed the Use of AI item in Milestone 1. We need to add it in Milestone 2 as a markdown file. Help me draft an outline that will allow us to easily add cases where AI was utilized in the future. Include the use of this prompt to generate the document itself."

**Output produced:** Initial structure and template for this document, including front matter, section headers, field conventions, and placeholder guidance for future sprints.  
**Human review/modification:** [Moved this entry to Sprint 2 as it was not completed in Sprint 1]

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

**Tool used:** Claude Agent
**Task:** Creating test classes for the user controller and user service interface and implementation class.
**Prompt used:**
> "Can you please help me create test classes for the user controller and user service interface and implementation class"

**Output produced:** Test classes for the user controller and user service interface and implementation class.

**Human review/modification:** Methods within the class were changed, added, or  removed as needed.

**Tool used:** Claude Agent
**Task:** Updating test classes for the user controller and user service interface and implementation class after a significant changes to the current build.
**Prompt used:**
> "Could you please edit the existing test classes in this project to fit the current build and ensure that all of our code is working as intended?"

**Output produced:** Test classes for the user controller and user service interface were updated to match the current build.

**Human review/modification:** Test classes were reviewed by team members to ensure that they are still passing.

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
