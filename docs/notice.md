---
title: Notice
subtitle: "Notice"
order: 110
---

# License and Copyright Notice

## Project License

Copyright (c) 2026 by Kevin Dilts, Skylar Scott, Michael Sylvester, and Mark Vega

This project is licensed under the Apache License, Version 2.0.
You may obtain a copy of the License at:

http://www.apache.org/licenses/LICENSE-2.0

Unless otherwise noted, all original code and documentation in this repository are distributed under this license.

---

## Project Components

This repository contains the following components:

* **app** — Android client application
* **server** — Spring Boot backend service
* **docs** — project documentation (GitHub Pages)

---

## Third-Party Components

This project uses a number of third-party libraries, frameworks, tools, and services.

---

### Server (Spring Boot Backend)

* Spring Boot
  License: Apache License 2.0
  Role: backend framework

* Spring MVC (Spring Web)
  License: Apache License 2.0
  Role: REST API and web framework

* Spring Data JPA
  License: Apache License 2.0
  Role: database persistence abstraction

* Hibernate ORM
  License: GNU LGPL 2.1
  Role: object-relational mapping (JPA implementation)

* HikariCP
  License: Apache License 2.0
  Role: database connection pooling

* Spring Security
  License: Apache License 2.0
  Role: authentication and authorization

* Spring OAuth2 Resource Server
  License: Apache License 2.0
  Role: JWT-based authentication

* Nimbus JOSE + JWT
  License: Apache License 2.0
  Role: JWT parsing and validation

* Spring HATEOAS
  License: Apache License 2.0
  Role: hypermedia-driven API responses

* Jackson (Databind, Core, Annotations)
  License: Apache License 2.0
  Role: JSON serialization/deserialization

* Thymeleaf
  License: Apache License 2.0
  Role: server-side HTML templating

* Thymeleaf Extras (Spring Security)
  License: Apache License 2.0
  Role: security integration in templates

* Embedded Apache Tomcat
  License: Apache License 2.0
  Role: embedded web server

* Hibernate Validator
  License: Apache License 2.0
  Role: bean validation

* SLF4J / Logback
  License: MIT / EPL
  Role: logging framework

---

### Android Client

* Kotlin Standard Library
  License: Apache License 2.0
  Role: Kotlin language runtime

* Kotlin Coroutines
  License: Apache License 2.0
  Role: asynchronous programming

* AndroidX AppCompat
  License: Apache License 2.0
  Role: backward-compatible UI support

* AndroidX Activity / Fragment
  License: Apache License 2.0
  Role: lifecycle and UI components

* AndroidX ConstraintLayout
  License: Apache License 2.0
  Role: layout system

* AndroidX RecyclerView
  License: Apache License 2.0
  Role: list/grid UI display

* AndroidX Navigation
  License: Apache License 2.0
  Role: in-app navigation

* AndroidX Lifecycle (ViewModel / LiveData)
  License: Apache License 2.0
  Role: lifecycle-aware state management

* AndroidX Paging
  License: Apache License 2.0
  Role: paginated data loading

* AndroidX Preference
  License: Apache License 2.0
  Role: settings UI support

* Material Components for Android
  License: Apache License 2.0
  Role: Material Design UI

* Room Database
  License: Apache License 2.0
  Role: local persistence layer

* Retrofit
  License: Apache License 2.0
  Role: REST API client

* OkHttp Logging Interceptor
  License: Apache License 2.0
  Role: HTTP logging

* Gson
  License: Apache License 2.0
  Role: JSON serialization

* Hilt / Dagger
  License: Apache License 2.0
  Role: dependency injection

* Glide
  License: BSD/MIT/Apache (various components)
  Role: image loading and thumbnails

* Android Credential Manager
  License: Apache License 2.0
  Role: credential handling

---

### External Services

* Google Identity Services / Google Sign-In
  License: subject to Google Terms of Service
  Role: authentication

* Google Places API / Places SDK for Android
  License: subject to Google Maps Platform Terms of Service
  Role: location search and place details

---

### Build and Tooling

* Gradle
  License: Apache License 2.0
  Role: build system

* Deep Dive Version Catalog (`edu.cnm.deepdive:catalog-jdk21:22.0.6`)
  Role: dependency and plugin catalog

* Kotlin Symbol Processing (KSP)
  License: Apache License 2.0
  Role: code generation

* Dokka
  License: Apache License 2.0
  Role: documentation generation

* Checkstyle
  License: LGPL 2.1
  Role: static analysis

---

### Testing Libraries

* JUnit 5
  License: EPL 2.0
  Role: unit testing

* AndroidX Test / Espresso
  License: Apache License 2.0
  Role: UI testing

* Hilt Android Testing
  License: Apache License 2.0
  Role: dependency injection testing

---

### Supporting / Transitive Libraries

The project also depends on a number of transitive libraries pulled in by primary dependencies, including:

* Kotlin Serialization (Apache 2.0)
* Google Guava (Apache 2.0)
* Javax / Jakarta Inject APIs
* Protobuf (BSD-style)
* SQLite JDBC (Apache 2.0)
* JSR-305 / FindBugs annotations (BSD-style)
* Apache Commons libraries
* ANTLR (BSD-style)

These libraries are included indirectly via frameworks such as Spring Boot, Room, and Hilt.

---

## Notes

This notice document identifies the primary third-party components used by the project and their associated licenses.

Some dependencies are included transitively through frameworks and may not be explicitly declared in project build files.

This document is intended to satisfy project licensing requirements for Milestone 4 and should be updated if dependencies change.

All third-party components are used in accordance with their respective licenses.

No modifications have been made that would violate licensing requirements.
