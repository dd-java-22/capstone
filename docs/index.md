---
title: Overview
description: "Summary of in-progress or completed project."
order: 0
---

{% include ddc-abbreviations.md %}

## Page contents
{:.no_toc}

- ToC
{:toc}

## Summary

See Something, Say Something, Albuquerque! is an issue/incident reporting system, intended for use by city employees and executives, to encourage all employees to report general (non-emergency) issues and incidents that should not be allowed to persist or escalate, and for designated executives to receive those reports in near-real-time and resolve them quickly.

## Intended users and user stories

- Commuting city employees

  > As a Albuquerque city employee who commutes to work by car pool, I often see unsightly graffiti or trash on the major traffic routes along the way. I use the reporting features of the app (along with my phone camera) to create issue reports, so they don’t get lost in the “cognitive shuffle” I experience when I get to the office.

- City employees committed to the long-term viability of Albuquerque’s parks and other public spaces

  > As an Albuquerque resident and employee who’s raising children here, I often see trash and broken equipment at city parks. I use the app (with its GPS and camera support) to create issue reports on the spot, and then use my list of open issues to track them; in this way, I feel I’m doing my part to keep the city’s public spaces safe and child-friendly.

- City executives dealing with fragmented, partial data

  > As an Albuquerque city executive who has, among my other responsibilities, input into the scheduling and and tasking of city maintenance and cleanup resources, I often feel frustrated by the lack of information available to me in that process. With city employees now encouraged to use the app to report issues, I can now use location- and tag-filtered queries of open issues to aid not only in immediate resolution of issues but also in short- and near-term planning and scheduling of city resources.

  

## Functionality


### Users

The system (available initially as an Android app) will present a very simple interface, allowing a user to:


- Create an issue report, with location information added manually or automatically (using the GPS services of the device), and optionally attaching one or more photographs

- Tag that report with predefined tags.

- See a list of issues previously reported by the given user, filtered (by default) by status, and (optionally) by tag and general location.

- Add comments (optionally with photos) to any issue previously reported by that user.

### Managers

In addition to the above, the system will enable users designated in the system as authorized managers to:


- Review lists of all issues, filtered (by default) by status, and (optionally) by tag and general location.

- Add comments (optionally with photos) to any issue.

- Update the status (open/reported, open/in-progress, closed) of any issue.

- Generate and export reports of issues filtered by status, tag, general location, and month, showing item-specific and aggregated data for time open, reports per month, reports by general location, etc.


### General

- The user-facing mobile app will send all issue reports (as they are created) to a Spring Boot-based web service, backed by a persistent store, so that reports can be accessed by issue creators and managers on any supported device.

- The web service will use a relational database management system (RDBMS) for the persistent store of issue reports (with related comments, photos, etc.), maintaining them for any duration required by law, regulation, or policy.



## Persistent data

- File store for photographs
- Relational database for issue reports and other data
- Storage for AI model (stretch goal)

## Device/external services


- Over the current planning horizon, the system will not manage user credentials internally (or via connection to a SSO—if any—in use by the city), but will use an external OpenID Connect/OAuth 2.0 authentication provider. Initially, Google will be supported as an authentication provider, but support for other providers are included in the short-term stretch goals.

- If the report initiator chooses to do so, device-level GPS services will be used to capture location information. Use of the app will not be limited to those users with GPS services available/enabled, and will not be otherwise restricted in any way if the user doesn’t grant location access permissions to the app.


- A report initiator may attach one or more photos captured by the mobile device to an issue report; a manager may attach additional photos during the process of resolution. Use of the app will not be otherwise restricted in any way if the user does not grant camera access permissions to the app.

- Device-level voice-to-text features will be supported for entering the text of an issue or related comment. Use of the app will not be otherwise restricted in any way if the user does not grant microphone access permissions to the app.

- Reports will be exportable in CSV format, enabling subsequent import into spreadsheets.

- Google Maps API for choosing a GPS location on a map.

## Stretch goals and possible enhancements 

[//]: # (TODO If you can identify functional elements of the software that you think might not be achievable in the scope of the project, but which would nonetheless add significant value if you were able to include them, list them here. For now, we recommend listing them in order of complexity/amount of work, from the least to the most.)
