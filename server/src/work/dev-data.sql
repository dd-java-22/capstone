-- Idempotent dev seed data for local H2 environment.
-- Assumes schema already exists.
--
-- Design goals:
--   * No explicit numeric IDs
--   * Safe to rerun
--   * Only touches rows identified by stable seed keys
--
-- Notes:
--   * Uses MERGE ... KEY(...) so reruns update existing seed rows instead of
--     creating duplicates.
--   * Uses fixed external IDs / keys so dependent rows can be resolved through
--     subqueries.
--   * Does not wipe the whole database, so teammate-created local data can
--     coexist with the seed data.

-- ---------------------------------------------------------------------------
-- Stable seed keys
-- ---------------------------------------------------------------------------
-- user_profile.oauth_key
--   seed-manager-oauth-key
--   seed-user-oauth-key
--
-- issue_report.issue_report_external_id
--   11111111-1111-1111-1111-111111111001
--   11111111-1111-1111-1111-111111111002
--   11111111-1111-1111-1111-111111111003
--   11111111-1111-1111-1111-111111111004
--   11111111-1111-1111-1111-111111111005

-- ---------------------------------------------------------------------------
-- user_profile
-- ---------------------------------------------------------------------------

MERGE INTO user_profile (
  user_profile_external_id,
  oauth_key,
  display_name,
  email,
  avatar,
  is_manager,
  time_created,
  user_enabled
) KEY (oauth_key) VALUES
  (
    '00000000-0000-0000-0000-000000000101',
    'seed-manager-oauth-key',
    'Morgan Manager',
    'manager@example.com',
    'https://example.com/avatars/manager.png',
    TRUE,
    CURRENT_TIMESTAMP,
    TRUE
  ),
  (
    '00000000-0000-0000-0000-000000000102',
    'seed-user-oauth-key',
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

MERGE INTO accepted_state (
  status_tag,
  status_tag_description
) KEY (status_tag) VALUES
  ('New', 'Issue has been reported and is awaiting review.'),
  ('In progress', 'Issue has been acknowledged and work is underway.'),
  ('Closed', 'Issue has been resolved and closed out.');

-- ---------------------------------------------------------------------------
-- issue_type
-- ---------------------------------------------------------------------------

MERGE INTO issue_type (
  issue_type_tag,
  issue_type_description
) KEY (issue_type_tag) VALUES
  ('Trash', 'Garbage, illegal dumping, or overflowing trash concerns.'),
  ('Maintenance', 'General infrastructure or property maintenance issue.'),
  ('Pothole', 'Road surface damage or pothole hazard.');

-- ---------------------------------------------------------------------------
-- report_location
-- street_coordinate is used as the stable seed key here.
-- ---------------------------------------------------------------------------

MERGE INTO report_location (
  latitude,
  longitude,
  street_coordinate,
  location_description
) KEY (street_coordinate) VALUES
  (
    35.084400,
    -106.650400,
    'Central Ave NE & Broadway Blvd NE',
    'Overflowing public trash can near the bus stop.'
  ),
  (
    35.087100,
    -106.648600,
    '7th St NW & Tijeras Ave NW',
    'Streetlight flickering near the courthouse block.'
  ),
  (
    35.079800,
    -106.605900,
    'Lomas Blvd NE & University Blvd NE',
    'Large pothole in the right lane near campus.'
  ),
  (
    35.104200,
    -106.629500,
    'Carlisle Blvd NE & Indian School Rd NE',
    'Dumped bags of trash beside the sidewalk.'
  ),
  (
    35.131300,
    -106.586000,
    'Wyoming Blvd NE & Montgomery Blvd NE',
    'Recurring roadway damage near the intersection.'
  );

-- ---------------------------------------------------------------------------
-- issue_report
-- issue_report_external_id is the stable seed key.
-- ---------------------------------------------------------------------------

MERGE INTO issue_report (
  issue_report_external_id,
  user_profile_id,
  report_location_id,
  accepted_state_id,
  time_first_reported,
  time_last_modified,
  text_description
) KEY (issue_report_external_id) VALUES
  (
    '11111111-1111-1111-1111-111111111001',
    (SELECT user_profile_id
     FROM user_profile
     WHERE oauth_key = 'seed-user-oauth-key'),
    (SELECT report_location_id
     FROM report_location
     WHERE street_coordinate = 'Central Ave NE & Broadway Blvd NE'),
    (SELECT accepted_state_id
     FROM accepted_state
     WHERE status_tag = 'New'),
    DATEADD('DAY', -10, CURRENT_TIMESTAMP),
    DATEADD('DAY', -10, CURRENT_TIMESTAMP),
    'Trash can is overflowing onto the sidewalk and attracting birds.'
  ),
  (
    '11111111-1111-1111-1111-111111111002',
    (SELECT user_profile_id
     FROM user_profile
     WHERE oauth_key = 'seed-manager-oauth-key'),
    (SELECT report_location_id
     FROM report_location
     WHERE street_coordinate = '7th St NW & Tijeras Ave NW'),
    (SELECT accepted_state_id
     FROM accepted_state
     WHERE status_tag = 'In progress'),
    DATEADD('DAY', -7, CURRENT_TIMESTAMP),
    DATEADD('DAY', -2, CURRENT_TIMESTAMP),
    'Streetlight has been flickering for several nights and leaves the corner poorly lit.'
  ),
  (
    '11111111-1111-1111-1111-111111111003',
    (SELECT user_profile_id
     FROM user_profile
     WHERE oauth_key = 'seed-user-oauth-key'),
    (SELECT report_location_id
     FROM report_location
     WHERE street_coordinate = 'Lomas Blvd NE & University Blvd NE'),
    (SELECT accepted_state_id
     FROM accepted_state
     WHERE status_tag = 'In progress'),
    DATEADD('DAY', -5, CURRENT_TIMESTAMP),
    DATEADD('DAY', -1, CURRENT_TIMESTAMP),
    'Deep pothole causes cars to swerve suddenly when approaching the intersection.'
  ),
  (
    '11111111-1111-1111-1111-111111111004',
    (SELECT user_profile_id
     FROM user_profile
     WHERE oauth_key = 'seed-user-oauth-key'),
    (SELECT report_location_id
     FROM report_location
     WHERE street_coordinate = 'Carlisle Blvd NE & Indian School Rd NE'),
    (SELECT accepted_state_id
     FROM accepted_state
     WHERE status_tag = 'Closed'),
    DATEADD('DAY', -14, CURRENT_TIMESTAMP),
    DATEADD('DAY', -3, CURRENT_TIMESTAMP),
    'Several trash bags were dumped beside the sidewalk and have now torn open.'
  ),
  (
    '11111111-1111-1111-1111-111111111005',
    (SELECT user_profile_id
     FROM user_profile
     WHERE oauth_key = 'seed-manager-oauth-key'),
    (SELECT report_location_id
     FROM report_location
     WHERE street_coordinate = 'Wyoming Blvd NE & Montgomery Blvd NE'),
    (SELECT accepted_state_id
     FROM accepted_state
     WHERE status_tag = 'New'),
    DATEADD('DAY', -1, CURRENT_TIMESTAMP),
    DATEADD('HOUR', -6, CURRENT_TIMESTAMP),
    'Road surface is breaking apart and may soon become a larger pothole.'
  );

-- ---------------------------------------------------------------------------
-- issue_report_issue_type
-- Rebuild only the seed associations so reruns stay deterministic.
-- ---------------------------------------------------------------------------

DELETE FROM issue_report_issue_type
WHERE issue_report_id IN (
  SELECT issue_report_id
  FROM issue_report
  WHERE issue_report_external_id IN (
    '11111111-1111-1111-1111-111111111001',
    '11111111-1111-1111-1111-111111111002',
    '11111111-1111-1111-1111-111111111003',
    '11111111-1111-1111-1111-111111111004',
    '11111111-1111-1111-1111-111111111005'
  )
);

INSERT INTO issue_report_issue_type (
  issue_report_id,
  issue_type_id
) VALUES
  (
    (SELECT issue_report_id
     FROM issue_report
     WHERE issue_report_external_id = '11111111-1111-1111-1111-111111111001'),
    (SELECT issue_type_id
     FROM issue_type
     WHERE issue_type_tag = 'Trash')
  ),
  (
    (SELECT issue_report_id
     FROM issue_report
     WHERE issue_report_external_id = '11111111-1111-1111-1111-111111111001'),
    (SELECT issue_type_id
     FROM issue_type
     WHERE issue_type_tag = 'Maintenance')
  ),
  (
    (SELECT issue_report_id
     FROM issue_report
     WHERE issue_report_external_id = '11111111-1111-1111-1111-111111111002'),
    (SELECT issue_type_id
     FROM issue_type
     WHERE issue_type_tag = 'Maintenance')
  ),
  (
    (SELECT issue_report_id
     FROM issue_report
     WHERE issue_report_external_id = '11111111-1111-1111-1111-111111111002'),
    (SELECT issue_type_id
     FROM issue_type
     WHERE issue_type_tag = 'Pothole')
  ),
  (
    (SELECT issue_report_id
     FROM issue_report
     WHERE issue_report_external_id = '11111111-1111-1111-1111-111111111003'),
    (SELECT issue_type_id
     FROM issue_type
     WHERE issue_type_tag = 'Pothole')
  ),
  (
    (SELECT issue_report_id
     FROM issue_report
     WHERE issue_report_external_id = '11111111-1111-1111-1111-111111111004'),
    (SELECT issue_type_id
     FROM issue_type
     WHERE issue_type_tag = 'Trash')
  ),
  (
    (SELECT issue_report_id
     FROM issue_report
     WHERE issue_report_external_id = '11111111-1111-1111-1111-111111111004'),
    (SELECT issue_type_id
     FROM issue_type
     WHERE issue_type_tag = 'Maintenance')
  ),
  (
    (SELECT issue_report_id
     FROM issue_report
     WHERE issue_report_external_id = '11111111-1111-1111-1111-111111111005'),
    (SELECT issue_type_id
     FROM issue_type
     WHERE issue_type_tag = 'Pothole')
  );

-- ---------------------------------------------------------------------------
-- report_image
-- filename is used as the stable seed key here.
-- image_locator is VARBINARY in the current H2 schema because the entity field
-- is a java.net.URI. STRINGTOUTF8(...) stores binary data H2 accepts.
-- ---------------------------------------------------------------------------

MERGE INTO report_image (
  issue_report_id,
  image_locator,
  filename,
  mime_type,
  album_order
) KEY (filename) VALUES
  (
    (SELECT issue_report_id
     FROM issue_report
     WHERE issue_report_external_id = '11111111-1111-1111-1111-111111111001'),
    STRINGTOUTF8('https://example.com/dev-images/trash-can-overflow-1.jpg'),
    'trash-can-overflow-1.jpg',
    'image/jpeg',
    1
  ),
  (
    (SELECT issue_report_id
     FROM issue_report
     WHERE issue_report_external_id = '11111111-1111-1111-1111-111111111002'),
    STRINGTOUTF8('https://example.com/dev-images/streetlight-flicker-1.jpg'),
    'streetlight-flicker-1.jpg',
    'image/jpeg',
    1
  ),
  (
    (SELECT issue_report_id
     FROM issue_report
     WHERE issue_report_external_id = '11111111-1111-1111-1111-111111111003'),
    STRINGTOUTF8('https://example.com/dev-images/pothole-campus-1.jpg'),
    'pothole-campus-1.jpg',
    'image/jpeg',
    1
  ),
  (
    (SELECT issue_report_id
     FROM issue_report
     WHERE issue_report_external_id = '11111111-1111-1111-1111-111111111004'),
    STRINGTOUTF8('https://example.com/dev-images/dumped-trash-1.jpg'),
    'dumped-trash-1.jpg',
    'image/jpeg',
    1
  ),
  (
    (SELECT issue_report_id
     FROM issue_report
     WHERE issue_report_external_id = '11111111-1111-1111-1111-111111111004'),
    STRINGTOUTF8('https://example.com/dev-images/dumped-trash-2.jpg'),
    'dumped-trash-2.jpg',
    'image/jpeg',
    2
  );

-- Optional cleanup for stale seed images if you ever rename files:
-- DELETE FROM report_image
-- WHERE filename LIKE '%.jpg'
--   AND filename IN (
--     'trash-can-overflow-1.jpg',
--     'streetlight-flicker-1.jpg',
--     'pothole-campus-1.jpg',
--     'dumped-trash-1.jpg',
--     'dumped-trash-2.jpg'
--   )
--   AND filename NOT IN (
--     'trash-can-overflow-1.jpg',
--     'streetlight-flicker-1.jpg',
--     'pothole-campus-1.jpg',
--     'dumped-trash-1.jpg',
--     'dumped-trash-2.jpg'
--   );

-- Optional verification queries:
-- SELECT user_profile_id, oauth_key, display_name, is_manager FROM user_profile;
-- SELECT accepted_state_id, status_tag FROM accepted_state;
-- SELECT issue_type_id, issue_type_tag FROM issue_type;
-- SELECT report_location_id, street_coordinate FROM report_location;
-- SELECT issue_report_id, issue_report_external_id, text_description FROM issue_report;
-- SELECT * FROM issue_report_issue_type;
-- SELECT report_image_id, issue_report_id, filename, album_order FROM report_image;
