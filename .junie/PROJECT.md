## Project-Specific Rules

- **Base Package Name:** Read from the `basePackageName` property in `gradle.properties`.
- **Database Schema:** Read the Room schema location from the `room.schemaLocation` argument in `app/build.gradle.kts`.
- **Project Structure:** This is a multi-module project with `app` (Android client) and `server` (Spring Boot) modules.
