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

### OpenAPI spec generation

**Tool used:** ChatGPT  
**Task:** Generate draft of OpenAPI spec based on ERD and expected endpoints   
**Prompt used:**
> I had a conversation with ChatGPT where I provided a PDF of the milestone 2 requirements, our ERD.svg, and a zip of our entity classes.
> I asked it what else it needed in order to generate a spec, and provided all of the information it asked for.
> After that, it generated the open-api.yaml file.

**Output produced:** open-api.yaml  
**Human review/modification:** Review will happen in the PR, and I will update anything we find after we discuss as a team 

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
