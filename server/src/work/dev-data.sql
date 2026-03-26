-- Dev seed data for local H2 environment.
-- This script assumes the schema already exists.
-- It is written to be easy to rerun in a local-only database.

SET REFERENTIAL_INTEGRITY FALSE;

DELETE FROM issue_report_issue_type;
DELETE FROM report_image;
DELETE FROM issue_report;
DELETE FROM accepted_state;
DELETE FROM issue_type;
DELETE FROM report_location;
DELETE FROM user_profile;

ALTER TABLE accepted_state ALTER COLUMN accepted_state_id RESTART WITH 1;
ALTER TABLE issue_type ALTER COLUMN issue_type_id RESTART WITH 1;
ALTER TABLE report_location ALTER COLUMN report_location_id RESTART WITH 1;
ALTER TABLE user_profile ALTER COLUMN user_profile_id RESTART WITH 1;
ALTER TABLE issue_report ALTER COLUMN issue_report_id RESTART WITH 1;
ALTER TABLE report_image ALTER COLUMN report_image_id RESTART WITH 1;

SET REFERENTIAL_INTEGRITY TRUE;

-- ---------------------------------------------------------------------------
-- user_profile
-- ---------------------------------------------------------------------------

INSERT INTO user_profile (
  user_profile_id,
  user_profile_external_id,
  oauth_key,
  display_name,
  email,
  avatar,
  is_manager,
  time_created,
  user_enabled
) VALUES
  (
    1,
    RANDOM_UUID(),
    'dev-manager-oauth-key',
    'Morgan Manager',
    'manager@example.com',
    'https://example.com/avatars/manager.png',
    TRUE,
    CURRENT_TIMESTAMP,
    TRUE
  ),
  (
    2,
    RANDOM_UUID(),
    'dev-user-oauth-key',
    'Riley Reporter',
    'user@example.com',
    'https://example.com/avatars/user.png',
    FALSE,
    CURRENT_TIMESTAMP,
    TRUE
  );

-- ---------------------------------------------------------------------------
-- accepted_state
-- ---------------------------------------------------------------------------

INSERT INTO accepted_state (
  accepted_state_id,
  status_tag,
  status_tag_description
) VALUES
  (1, 'New', 'Issue has been reported and is awaiting review.'),
  (2, 'In progress', 'Issue has been acknowledged and work is underway.'),
  (3, 'Closed', 'Issue has been resolved and closed out.');

-- ---------------------------------------------------------------------------
-- issue_type
-- ---------------------------------------------------------------------------

INSERT INTO issue_type (
  issue_type_id,
  issue_type_tag,
  issue_type_description
) VALUES
  (1, 'Trash', 'Garbage, illegal dumping, or overflowing trash concerns.'),
  (2, 'Maintenance', 'General infrastructure or property maintenance issue.'),
  (3, 'Pothole', 'Road surface damage or pothole hazard.');

-- ---------------------------------------------------------------------------
-- report_location
-- ---------------------------------------------------------------------------

INSERT INTO report_location (
  report_location_id,
  latitude,
  longitude,
  street_coordinate,
  location_description
) VALUES
  (
    1,
    35.084400,
    -106.650400,
    'Central Ave NE & Broadway Blvd NE',
    'Overflowing public trash can near the bus stop.'
  ),
  (
    2,
    35.087100,
    -106.648600,
    '7th St NW & Tijeras Ave NW',
    'Streetlight flickering near the courthouse block.'
  ),
  (
    3,
    35.079800,
    -106.605900,
    'Lomas Blvd NE & University Blvd NE',
    'Large pothole in the right lane near campus.'
  ),
  (
    4,
    35.104200,
    -106.629500,
    'Carlisle Blvd NE & Indian School Rd NE',
    'Dumped bags of trash beside the sidewalk.'
  ),
  (
    5,
    35.131300,
    -106.586000,
    'Wyoming Blvd NE & Montgomery Blvd NE',
    'Recurring roadway damage near the intersection.'
  );

-- ---------------------------------------------------------------------------
-- issue_report
-- ---------------------------------------------------------------------------

INSERT INTO issue_report (
  issue_report_id,
  issue_report_external_id,
  user_profile_id,
  report_location_id,
  accepted_state_id,
  time_first_reported,
  time_last_modified,
  text_description
) VALUES
  (
    1,
    RANDOM_UUID(),
    2,
    1,
    1,
    DATEADD('DAY', -10, CURRENT_TIMESTAMP),
    DATEADD('DAY', -10, CURRENT_TIMESTAMP),
    'Trash can is overflowing onto the sidewalk and attracting birds.'
  ),
  (
    2,
    RANDOM_UUID(),
    1,
    2,
    2,
    DATEADD('DAY', -7, CURRENT_TIMESTAMP),
    DATEADD('DAY', -2, CURRENT_TIMESTAMP),
    'Streetlight has been flickering for several nights and leaves the corner poorly lit.'
  ),
  (
    3,
    RANDOM_UUID(),
    2,
    3,
    2,
    DATEADD('DAY', -5, CURRENT_TIMESTAMP),
    DATEADD('DAY', -1, CURRENT_TIMESTAMP),
    'Deep pothole causes cars to swerve suddenly when approaching the intersection.'
  ),
  (
    4,
    RANDOM_UUID(),
    2,
    4,
    3,
    DATEADD('DAY', -14, CURRENT_TIMESTAMP),
    DATEADD('DAY', -3, CURRENT_TIMESTAMP),
    'Several trash bags were dumped beside the sidewalk and have now torn open.'
  ),
  (
    5,
    RANDOM_UUID(),
    1,
    5,
    1,
    DATEADD('DAY', -1, CURRENT_TIMESTAMP),
    DATEADD('HOUR', -6, CURRENT_TIMESTAMP),
    'Road surface is breaking apart and may soon become a larger pothole.'
  );

-- ---------------------------------------------------------------------------
-- issue_report_issue_type
-- ---------------------------------------------------------------------------

INSERT INTO issue_report_issue_type (
  issue_report_id,
  issue_type_id
) VALUES
  (1, 1),
  (1, 2),
  (2, 2),
  (2, 3),
  (3, 3),
  (4, 1),
  (4, 2),
  (5, 3);

-- ---------------------------------------------------------------------------
-- report_image
-- NOTE: image_locator is VARBINARY in the generated H2 schema because the
-- entity field is a java.net.URI. STRINGTOUTF8(...) stores a binary
-- representation that H2 accepts for this column.
-- ---------------------------------------------------------------------------

INSERT INTO report_image (
  report_image_id,
  issue_report_id,
  image_locator,
  filename,
  mime_type,
  album_order
) VALUES
  (
    1,
    1,
    STRINGTOUTF8('https://example.com/dev-images/trash-can-overflow-1.jpg'),
    'trash-can-overflow-1.jpg',
    'image/jpeg',
    1
  ),
  (
    2,
    2,
    STRINGTOUTF8('https://example.com/dev-images/streetlight-flicker-1.jpg'),
    'streetlight-flicker-1.jpg',
    'image/jpeg',
    1
  ),
  (
    3,
    3,
    STRINGTOUTF8('https://example.com/dev-images/pothole-campus-1.jpg'),
    'pothole-campus-1.jpg',
    'image/jpeg',
    1
  ),
  (
    4,
    4,
    STRINGTOUTF8('https://example.com/dev-images/dumped-trash-1.jpg'),
    'dumped-trash-1.jpg',
    'image/jpeg',
    1
  ),
  (
    5,
    4,
    STRINGTOUTF8('https://example.com/dev-images/dumped-trash-2.jpg'),
    'dumped-trash-2.jpg',
    'image/jpeg',
    2
  );

-- Optional verification queries:
-- SELECT * FROM user_profile;
-- SELECT * FROM accepted_state;
-- SELECT * FROM issue_type;
-- SELECT * FROM report_location;
-- SELECT * FROM issue_report;
-- SELECT * FROM issue_report_issue_type;
-- SELECT * FROM report_image;
