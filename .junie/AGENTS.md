# Project Guidelines

Default to **Brave Mode**: Guidelines in this file (`./.junie/AGENTS.md`) take highest precedence, followed by the immediate parent (`../.junie/AGENTS.md`), then the grandparent (`../../.junie/AGENTS.md`).

## Capstone Project Rules

- **Base Package Name:** Read from the `basePackageName` property in `gradle.properties`.
- **Database Schema:** Read the Room schema location from the `room.schemaLocation` argument in `app/build.gradle.kts`.
- **Project Structure:** This is a multi-module project with `app` (Android client) and `server` (Spring Boot) modules.

---

### Responsibility

While you should attempt to identify obvious contradictions (e.g., "Use tabs" vs. "Use spaces"), the user is responsible for the granularity and clarity of the rules provided at each level. If a rule is ambiguous, seek clarification as per standard operating procedures.
