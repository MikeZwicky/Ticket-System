-- /*****************************************************************
-- * Table Users
-- *****************************************************************/
-- Insert Users (4 Users, 2 Supports)
-- Admin Password: admin123 (hased: $2a$10$ATlfn3IJViknV5.6AheqtOgIyp3Pa396Nf3NNEw3iecbH1Z4EhxQa)
-- User Password: pass123 (hased: $2a$10$/vlKzJ7P5GEepFOL9GXuCOkm6mbg4Ix030PbnlP9QstsOHO.kbBvW)
-- Support Password: support123 (hased: $2a$10$ew4kitRu8iaejIUWu.hsN.mT7hPzmVvJIlCst8mlnl8m4nC9/IV2m)
INSERT INTO users (username, email, password, role, created_At) VALUES
('admin1', 'admin1@example.com', '$2a$10$ATlfn3IJViknV5.6AheqtOgIyp3Pa396Nf3NNEw3iecbH1Z4EhxQa', 'Admin', TIMESTAMP '2024-01-01 08:00:00'),
('user1', 'user1@example.com', '$2a$10$/vlKzJ7P5GEepFOL9GXuCOkm6mbg4Ix030PbnlP9QstsOHO.kbBvW', 'User', TIMESTAMP '2024-05-01 10:00:00'),
('user2', 'user2@example.com', '$2a$10$/vlKzJ7P5GEepFOL9GXuCOkm6mbg4Ix030PbnlP9QstsOHO.kbBvW', 'User', TIMESTAMP '2024-05-05 12:30:00'),
('user3', 'user3@example.com', '$2a$10$/vlKzJ7P5GEepFOL9GXuCOkm6mbg4Ix030PbnlP9QstsOHO.kbBvW', 'User', TIMESTAMP '2024-05-10 09:15:00'),
('user4', 'user4@example.com', '$2a$10$/vlKzJ7P5GEepFOL9GXuCOkm6mbg4Ix030PbnlP9QstsOHO.kbBvW', 'User', TIMESTAMP '2024-05-15 14:45:00'),
('support1', 'support1@example.com', '$2a$10$ew4kitRu8iaejIUWu.hsN.mT7hPzmVvJIlCst8mlnl8m4nC9/IV2m', 'Support', TIMESTAMP '2024-05-02 11:20:00'),
('support2', 'support2@example.com', '$2a$10$ew4kitRu8iaejIUWu.hsN.mT7hPzmVvJIlCst8mlnl8m4nC9/IV2m', 'Support', TIMESTAMP '2024-05-12 16:10:00');


-- Insert Tickets
INSERT INTO ticket (title, description, created_at, priority, created_by, assigned_to)
VALUES
('Login Issue', 'Some users are unable to log in to their accounts using valid credentials.', '2024-05-03 10:27:24', 2, 1, 5),
('Broken Dashboard Link', 'The dashboard displays a "Page not found" error when accessed.', '2024-05-05 08:13:10', 3, 2, 7),
('Delayed Email Notifications', 'System emails are arriving with a significant delay, affecting user communication.', '2024-05-05 11:17:09', 1, 3, 5),
('Profile Page Error 500', 'Users receive a server error (500) when trying to view or edit their profile.', '2024-05-07 09:48:53', 2, 4, 6),
('DB Connection Timeout', 'Frequent database connection timeouts are affecting the application''s performance.', '2024-05-08 12:15:15', 3, 1, 5),
('Mobile UI Misalignment', 'UI elements are misaligned when viewed on mobile devices, especially in portrait mode.', '2024-05-08 14:18:15', 1, 2, 6),
('Search Inaccuracy', 'The search feature is returning results unrelated to the query terms.', '2024-05-08 15:55:09', 2, 3, 5),
('Short Session Expiry', 'User sessions are timing out too quickly, causing inconvenience.', '2024-05-10 13:05:44', 3, 4, 7),
('Incorrect Password Reset', 'The password reset emails contain incorrect reset links or missing data.', '2024-05-11 07:48:01', 1, 1, 5),
('Missing Settings Styles', 'The settings page lacks proper styling, making it hard to navigate.', '2024-05-12 11:09:31', 2, 2, 6),
('Ticket Sorting Bug', 'Sorting options on the ticket list do not work as expected.', '2024-05-14 16:21:38', 3, 3, 5),
('Support Dashboard Crash', 'The support dashboard crashes intermittently while navigating through tabs.', '2024-05-15 08:27:36', 1, 4, 6),
('Slow Login Page', 'The login page is taking unusually long to load for all users.', '2024-05-16 11:44:53', 2, 1, 5),
('Unresponsive Dropdown', 'Dropdown menus are not responding to user clicks in some browsers.', '2024-05-17 14:52:21', 3, 2, 6),
('Captcha Loading Issue', 'Captcha fails to load, preventing users from completing forms.', '2024-05-18 10:01:15', 1, 3, 5),
('Submit Requires Double Click', 'Users must click the submit button twice for it to respond.', '2024-05-19 13:16:57', 2, 4, 6),
('Dark Mode Settings', 'Dark mode preferences are not saved after logging out.', '2024-05-20 15:55:57', 3, 1, 5),
('Broken Help Link', 'The help link in the footer redirects to a 404 page.', '2024-05-22 14:24:40', 1, 2, 6),
('Missing Homepage Footer', 'The homepage footer is missing on all screen resolutions.', '2024-05-23 09:20:39', 2, 3, 5),
('Feedback Form Error', 'Submitting the feedback form results in a server error.', '2024-05-23 11:36:37', 3, 4, 7);

-- Insert TicketStatusChanges
INSERT INTO ticket_status_change (changed_at, ticket_id, status_for_assigned, status_for_creator) VALUES
('2024-05-03 10:27:24', 1, 'Open', 'Pending'),
('2024-05-05 08:13:10', 2, 'Open', 'Pending'),
('2024-05-05 11:17:09', 3, 'Open', 'Pending'),
('2024-05-07 09:48:53', 4, 'Open', 'Pending'),
('2024-05-08 12:15:15', 5, 'Open', 'Pending'),
('2024-05-08 14:18:15', 6, 'Open', 'Pending'),
('2024-05-08 15:55:09', 7, 'Open', 'Pending'),
('2024-05-10 13:05:44', 8, 'Open', 'Pending'),
('2024-05-11 07:48:01', 9, 'Open', 'Pending'),
('2024-05-12 11:09:31', 10, 'Open', 'Pending'),
('2024-05-14 16:21:38', 11, 'Open', 'Pending'),
('2024-05-15 08:27:36', 12, 'Open', 'Pending'),
('2024-05-16 11:44:53', 13, 'Open', 'Pending'),
('2024-05-17 14:52:21', 14, 'Open', 'Pending'),
('2024-05-18 10:01:15', 15, 'Open', 'Pending'),
('2024-05-19 13:16:57', 16, 'Open', 'Pending'),
('2024-05-20 15:55:57', 17, 'Open', 'Pending'),
('2024-05-22 14:24:40', 18, 'Open', 'Pending'),
('2024-05-23 09:20:39', 19, 'Open', 'Pending'),
('2024-05-23 11:36:37', 20, 'Open', 'Pending');

UPDATE ticket SET latest_status_change_id = 1 WHERE ticket_id = 1;
UPDATE ticket SET latest_status_change_id = 2 WHERE ticket_id = 2;
UPDATE ticket SET latest_status_change_id = 3 WHERE ticket_id = 3;
UPDATE ticket SET latest_status_change_id = 4 WHERE ticket_id = 4;
UPDATE ticket SET latest_status_change_id = 5 WHERE ticket_id = 5;
UPDATE ticket SET latest_status_change_id = 6 WHERE ticket_id = 6;
UPDATE ticket SET latest_status_change_id = 7 WHERE ticket_id = 7;
UPDATE ticket SET latest_status_change_id = 8 WHERE ticket_id = 8;
UPDATE ticket SET latest_status_change_id = 9 WHERE ticket_id = 9;
UPDATE ticket SET latest_status_change_id = 10 WHERE ticket_id = 10;
UPDATE ticket SET latest_status_change_id = 11 WHERE ticket_id = 11;
UPDATE ticket SET latest_status_change_id = 12 WHERE ticket_id = 12;
UPDATE ticket SET latest_status_change_id = 13 WHERE ticket_id = 13;
UPDATE ticket SET latest_status_change_id = 14 WHERE ticket_id = 14;
UPDATE ticket SET latest_status_change_id = 15 WHERE ticket_id = 15;
UPDATE ticket SET latest_status_change_id = 16 WHERE ticket_id = 16;
UPDATE ticket SET latest_status_change_id = 17 WHERE ticket_id = 17;
UPDATE ticket SET latest_status_change_id = 18 WHERE ticket_id = 18;
UPDATE ticket SET latest_status_change_id = 19 WHERE ticket_id = 19;
UPDATE ticket SET latest_status_change_id = 20 WHERE ticket_id = 20;



/*****************************************************************
* Table Messages
*****************************************************************/
-- Ticket 1: Login Issue
-- Comment: Support applied fix and needs user confirmation
INSERT INTO message (ticket_id, user_id, text, created_at) VALUES
(1, 5, 'We have applied a fix for the login issue. Can you try logging in again and let us know if it works?', TIMESTAMP '2024-05-03 10:32:00');

INSERT INTO ticket_status_change (changed_at, message_id, previous_change_id, rating_id, ticket_id, status_for_assigned, status_for_creator) VALUES
(TIMESTAMP '2024-05-03 10:32:00', 1, 1, NULL, 1, 'Pending', 'Open');
UPDATE ticket SET latest_status_change_id = 21 WHERE ticket_id = 1;

-- Ticket 3: Delayed Email Notifications
-- Comment: Ongoing exchange between user and support, ends with user confirmation
INSERT INTO message (ticket_id, user_id, text, created_at) VALUES
(3, 3, 'Could you please look into why emails are being delayed?', TIMESTAMP '2024-05-05 11:22:00'),
(3, 5, 'We are currently investigating the email delivery delays. Can you send an example?', TIMESTAMP '2024-05-05 11:35:00'),
(3, 3, 'Here is an example of a delayed email for reference.', TIMESTAMP '2024-05-05 12:00:00'),
(3, 5, 'We believe the email notifications should now be sent promptly. Can you confirm?', TIMESTAMP '2024-05-05 12:20:00');

INSERT INTO ticket_status_change (changed_at, message_id, previous_change_id, rating_id, ticket_id, status_for_assigned, status_for_creator) VALUES
(TIMESTAMP '2024-05-05 11:22:00', 2, 3, NULL, 3, 'Open', 'Pending'),
(TIMESTAMP '2024-05-05 11:35:00', 3, 22, NULL, 3, 'Pending', 'Open'),
(TIMESTAMP '2024-05-05 12:00:00', 4, 23, NULL, 3, 'Open', 'Pending'),
(TIMESTAMP '2024-05-05 12:20:00', 5, 24, NULL, 3, 'Pending', 'Open');

UPDATE ticket SET latest_status_change_id = 22 WHERE ticket_id = 3;
UPDATE ticket SET latest_status_change_id = 23 WHERE ticket_id = 3;
UPDATE ticket SET latest_status_change_id = 24 WHERE ticket_id = 3;
UPDATE ticket SET latest_status_change_id = 25 WHERE ticket_id = 3;

INSERT INTO rating (rating, created_at, created_by, rated_user_id, ticket_id, text) VALUES
(4, TIMESTAMP '2024-05-05 12:40:00', 3, 5, 3, 'The issue is resolved. Thanks for the quick fix!');

INSERT INTO ticket_status_change (changed_at, message_id, previous_change_id, rating_id, ticket_id, status_for_assigned, status_for_creator) VALUES
(TIMESTAMP '2024-05-05 12:40:00', NULL, 25, 1, 3, 'Closed', 'Closed');
UPDATE ticket SET latest_status_change_id = 26 WHERE ticket_id = 3;

-- Ticket 4: Profile Page Error 500
-- Comment: Fix delivered, awaiting user confirmation
INSERT INTO message (ticket_id, user_id, text, created_at) VALUES
(4, 4, 'Can you check why the profile page is giving a 500 error?', TIMESTAMP '2024-05-07 09:55:00'),
(4, 6, 'We are looking into the server error on the profile page. Can you send a screenshot of the error?', TIMESTAMP '2024-05-07 10:10:00'),
(4, 4, 'Here is a screenshot of the error message.', TIMESTAMP '2024-05-07 10:25:00'),
(4, 6, 'We have deployed a fix for the profile page error. Please verify if it works now.', TIMESTAMP '2024-05-07 10:45:00');

INSERT INTO ticket_status_change (changed_at, message_id, previous_change_id, rating_id, ticket_id, status_for_assigned, status_for_creator) VALUES
(TIMESTAMP '2024-05-07 09:55:00', 6, 4, NULL, 4, 'Open', 'Pending'),
(TIMESTAMP '2024-05-07 10:10:00', 7, 27, NULL, 4, 'Pending', 'Open'),
(TIMESTAMP '2024-05-07 10:25:00', 8, 28, NULL, 4, 'Open', 'Pending'),
(TIMESTAMP '2024-05-07 10:45:00', 9, 29, NULL, 4, 'Pending', 'Open');

UPDATE ticket SET latest_status_change_id = 27 WHERE ticket_id = 4;
UPDATE ticket SET latest_status_change_id = 28 WHERE ticket_id = 4;
UPDATE ticket SET latest_status_change_id = 29 WHERE ticket_id = 4;
UPDATE ticket SET latest_status_change_id = 30 WHERE ticket_id = 4;

-- Ticket 6: Mobile UI Misalignment
-- Comment: Complete exchange and confirmation
INSERT INTO message (ticket_id, user_id, text, created_at) VALUES
(6, 6, 'We are reviewing the mobile UI layout issues.', TIMESTAMP '2024-05-08 14:25:00'),
(6, 6, 'Can you send a screenshot of the misaligned elements?', TIMESTAMP '2024-05-08 14:40:00'),
(6, 2, 'Here is a screenshot showing the misaligned elements.', TIMESTAMP '2024-05-08 15:00:00'),
(6, 6, 'We have fixed the mobile UI alignment. Can you confirm it looks correct now?', TIMESTAMP '2024-05-08 15:20:00');

INSERT INTO ticket_status_change (changed_at, message_id, previous_change_id, rating_id, ticket_id, status_for_assigned, status_for_creator) VALUES
(TIMESTAMP '2024-05-08 14:25:00', 10, 6, NULL, 6, 'Open', 'Pending'),
(TIMESTAMP '2024-05-08 14:40:00', 11, 31, NULL, 6, 'Pending', 'Open'),
(TIMESTAMP '2024-05-08 15:00:00', 12, 32, NULL, 6, 'Open', 'Pending'),
(TIMESTAMP '2024-05-08 15:20:00', 13, 33, NULL, 6, 'Pending', 'Open');

UPDATE ticket SET latest_status_change_id = 31 WHERE ticket_id = 6;
UPDATE ticket SET latest_status_change_id = 32 WHERE ticket_id = 6;
UPDATE ticket SET latest_status_change_id = 33 WHERE ticket_id = 6;
UPDATE ticket SET latest_status_change_id = 34 WHERE ticket_id = 6;

INSERT INTO rating (rating, created_at, created_by, rated_user_id, ticket_id, text) VALUES
(5, TIMESTAMP '2024-05-08 15:40:00', 2, 6, 6, 'The UI looks good now on mobile. Thank you!');

INSERT INTO ticket_status_change (changed_at, message_id, previous_change_id, rating_id, ticket_id, status_for_assigned, status_for_creator) VALUES
(TIMESTAMP '2024-05-08 15:40:00', NULL, 34, 2, 6, 'Closed', 'Closed');

UPDATE ticket SET latest_status_change_id = 35 WHERE ticket_id = 6;

-- Ticket 7: Search Inaccuracy
-- One support message with no further action yet
INSERT INTO message (ticket_id, user_id, text, created_at) VALUES
(7, 5, 'We have updated the search logic. Please try searching again and let us know if the results are more accurate.', TIMESTAMP '2024-05-08 16:00:00');

INSERT INTO ticket_status_change (changed_at, message_id, previous_change_id, rating_id, ticket_id, status_for_assigned, status_for_creator) VALUES
(TIMESTAMP '2024-05-08 16:00:00', 14, 7, NULL, 7, 'Pending', 'Open');

UPDATE ticket SET latest_status_change_id = 36 WHERE ticket_id = 7;

-- Ticket 9: Incorrect Password Reset
-- Full exchange ending with user confirmation
INSERT INTO message (ticket_id, user_id, text, created_at) VALUES
(9, 1, 'Can you resolve the issue today?', TIMESTAMP '2024-05-11 07:55:00'),
(9, 5, 'We are investigating the password reset email issue.', TIMESTAMP '2024-05-12 08:15:00'),
(9, 1, 'Here is a screenshot of the incorrect reset link. Maybe it helps.', TIMESTAMP '2024-05-15 08:45:00'),
(9, 5, 'We have corrected the reset link in the emails. Please test it again.', TIMESTAMP '2024-05-16 09:10:00');

INSERT INTO ticket_status_change (changed_at, message_id, previous_change_id, rating_id, ticket_id, status_for_assigned, status_for_creator) VALUES
(TIMESTAMP '2024-05-11 07:55:00', 15, 9, NULL, 9, 'Open', 'Pending'),
(TIMESTAMP '2024-05-15 08:15:00', 16, 37, NULL, 9, 'Open', 'Pending'),
(TIMESTAMP '2024-05-15 08:45:00', 17, 38, NULL, 9, 'Open', 'Pending'),
(TIMESTAMP '2024-05-16 09:10:00', 18, 39, NULL, 9, 'Pending', 'Open');

UPDATE ticket SET latest_status_change_id = 37 WHERE ticket_id = 9;
UPDATE ticket SET latest_status_change_id = 38 WHERE ticket_id = 9;
UPDATE ticket SET latest_status_change_id = 39 WHERE ticket_id = 9;
UPDATE ticket SET latest_status_change_id = 40 WHERE ticket_id = 9;

INSERT INTO rating (rating, created_at, created_by, rated_user_id, ticket_id, text) VALUES
(2, TIMESTAMP '2024-05-16 15:15:00', 1, 5, 9, 'The reset link works now, but it took a while to resolve.');

INSERT INTO ticket_status_change (changed_at, message_id, previous_change_id, rating_id, ticket_id, status_for_assigned, status_for_creator) VALUES
(TIMESTAMP '2024-05-16 15:15:00', NULL, 40, 3, 9, 'Closed', 'Closed');

UPDATE ticket SET latest_status_change_id = 41 WHERE ticket_id = 9;

-- Ticket 10: Missing Settings Styles
-- Support delivered fix, no user reply yet
INSERT INTO message (ticket_id, user_id, text, created_at) VALUES
(10, 6, 'We have added the missing styles to the settings page. Can you check if it looks better now?', TIMESTAMP '2024-05-12 11:15:00'),
(10, 6, 'Please refresh the settings page and let us know if the navigation is clearer.', TIMESTAMP '2024-05-12 11:25:00');

INSERT INTO ticket_status_change (changed_at, message_id, previous_change_id, rating_id, ticket_id, status_for_assigned, status_for_creator) VALUES
(TIMESTAMP '2024-05-12 11:15:00', 19, 10, NULL, 10, 'Pending', 'Open'),
(TIMESTAMP '2024-05-12 11:25:00', 20, 42, NULL, 10, 'Pending', 'Open');

UPDATE ticket SET latest_status_change_id = 42 WHERE ticket_id = 10;
UPDATE ticket SET latest_status_change_id = 43 WHERE ticket_id = 10;

-- Ticket 12: Support Dashboard Crash
-- Full exchange with user confirmation
INSERT INTO message (ticket_id, user_id, text, created_at) VALUES
(12, 4, 'We are investigating the intermittent crashes on the support dashboard.', TIMESTAMP '2024-05-15 08:30:00'),
(12, 6, 'When did the crash happen?', TIMESTAMP '2024-05-15 08:40:00'),
(12, 4, 'The crash happened yesterday around 18:00.', TIMESTAMP '2024-05-15 08:50:00'),
(12, 6, 'We have applied a fix for the dashboard crash. Please test it again.', TIMESTAMP '2024-05-15 09:00:00');

INSERT INTO ticket_status_change (changed_at, message_id, previous_change_id, rating_id, ticket_id, status_for_assigned, status_for_creator) VALUES
(TIMESTAMP '2024-05-15 08:30:00', 21, 12, NULL, 12, 'Open', 'Pending'),
(TIMESTAMP '2024-05-15 08:40:00', 22, 44, NULL, 12, 'Pending', 'Open'),
(TIMESTAMP '2024-05-15 08:50:00', 23, 45, NULL, 12, 'Open', 'Pending'),
(TIMESTAMP '2024-05-15 09:00:00', 24, 46, NULL, 12, 'Pending', 'Open');

UPDATE ticket SET latest_status_change_id = 44 WHERE ticket_id = 12;
UPDATE ticket SET latest_status_change_id = 45 WHERE ticket_id = 12;
UPDATE ticket SET latest_status_change_id = 46 WHERE ticket_id = 12;
UPDATE ticket SET latest_status_change_id = 47 WHERE ticket_id = 12;

INSERT INTO rating (rating, created_at, created_by, rated_user_id, ticket_id, text) VALUES
(5, TIMESTAMP '2024-05-15 09:15:00', 4, 6, 12, 'The dashboard is stable now. Thanks for resolving it!');

INSERT INTO ticket_status_change (changed_at, message_id, previous_change_id, rating_id, ticket_id, status_for_assigned, status_for_creator) VALUES
(TIMESTAMP '2024-05-15 09:15:00', NULL, 47, 4, 12, 'Closed', 'Closed');

UPDATE ticket SET latest_status_change_id = 48 WHERE ticket_id = 12;

-- Ticket 13 (Slow Login Page)
INSERT INTO message (ticket_id, user_id, text, created_at) VALUES
(13, 5, 'We have optimized the login page and are testing it now.', TIMESTAMP '2024-05-17 11:50:00'),
(13, 5, 'It should be fixed, let us know if you still experience slow loading times.', TIMESTAMP '2024-05-17 12:05:00');

INSERT INTO ticket_status_change (changed_at, message_id, previous_change_id, rating_id, ticket_id, status_for_assigned, status_for_creator) VALUES
(TIMESTAMP '2024-05-17 11:50:00', 25, 13, NULL, 13, 'Open', 'Pending'),
(TIMESTAMP '2024-05-17 12:05:00', 26, 49, NULL, 13, 'Pending', 'Open');

UPDATE ticket SET latest_status_change_id = 49 WHERE ticket_id = 13;
UPDATE ticket SET latest_status_change_id = 50 WHERE ticket_id = 13;

-- Ticket 14 (Unresponsive Dropdown)
INSERT INTO message (ticket_id, user_id, text, created_at) VALUES
(14, 2, 'Just to clarify, the dropdowns stop working after logging in on certain browsers.', TIMESTAMP '2024-05-17 15:00:00');

INSERT INTO ticket_status_change (changed_at, message_id, previous_change_id, rating_id, ticket_id, status_for_assigned, status_for_creator) VALUES
(TIMESTAMP '2024-05-17 15:00:00', 27, 14, NULL, 14, 'Open', 'Pending');

UPDATE ticket SET latest_status_change_id = 51 WHERE ticket_id = 14;

-- Ticket 15 (Captcha Loading Issue)
INSERT INTO message (ticket_id, user_id, text, created_at) VALUES
(15, 5, 'It works for me. Maybe you did it wrong!', TIMESTAMP '2024-05-18 10:10:00'),
(15, 3, 'No! It still does not work. I now how it works!', TIMESTAMP '2024-05-18 10:25:00'),
(15, 3, 'Here is a screenshot of the form without captcha. Maybe it can help solving the problem.', TIMESTAMP '2024-05-19 10:40:00'),
(15, 5, 'We have fixed the captcha issue. Please try submitting the form again.', TIMESTAMP '2024-05-19 10:55:00');

INSERT INTO ticket_status_change (changed_at, message_id, previous_change_id, rating_id, ticket_id, status_for_assigned, status_for_creator) VALUES
(TIMESTAMP '2024-05-18 10:10:00', 28, 15, NULL, 15, 'Pending', 'Open'),
(TIMESTAMP '2024-05-18 10:25:00', 29, 52, NULL, 15, 'Open', 'Pending'),
(TIMESTAMP '2024-05-19 10:40:00', 30, 53, NULL, 15, 'Open', 'Pending'),
(TIMESTAMP '2024-05-19 10:55:00', 31, 54, NULL, 15, 'Pending', 'Open');

UPDATE ticket SET latest_status_change_id = 52 WHERE ticket_id = 15;
UPDATE ticket SET latest_status_change_id = 53 WHERE ticket_id = 15;
UPDATE ticket SET latest_status_change_id = 54 WHERE ticket_id = 15;
UPDATE ticket SET latest_status_change_id = 55 WHERE ticket_id = 15;

INSERT INTO rating (rating, created_at, created_by, rated_user_id, ticket_id, text) VALUES
(1, TIMESTAMP '2024-05-19 11:10:00', 3, 5, 15, 'Captcha issue resolved but he first did not took me seriously!');

INSERT INTO ticket_status_change (changed_at, message_id, previous_change_id, rating_id, ticket_id, status_for_assigned, status_for_creator) VALUES
(TIMESTAMP '2024-05-19 11:10:00', NULL, 55, 5, 15, 'Closed', 'Closed');

UPDATE ticket SET latest_status_change_id = 56 WHERE ticket_id = 15;

-- Ticket 16 (Submit Requires Double Click)
INSERT INTO message (ticket_id, user_id, text, created_at) VALUES
(16, 4, 'Can you check why the submit button needs to be clicked twice?', TIMESTAMP '2024-05-19 13:30:00'),
(16, 6, 'We are looking into the double-click issue on the submit button.', TIMESTAMP '2024-05-19 13:45:00'),
(16, 4, 'Thank you!', TIMESTAMP '2024-05-19 14:00:00'),
(16, 6, 'We have fixed the submit button. Please test if it works with a single click now.', TIMESTAMP '2024-05-19 14:15:00');

INSERT INTO ticket_status_change (changed_at, message_id, previous_change_id, rating_id, ticket_id, status_for_assigned, status_for_creator) VALUES
(TIMESTAMP '2024-05-19 13:30:00', 32, 16, NULL, 16, 'Open', 'Pending'),
(TIMESTAMP '2024-05-19 13:45:00', 33, 57, NULL, 16, 'Open', 'Pending'),
(TIMESTAMP '2024-05-19 14:00:00', 34, 58, NULL, 16, 'Open', 'Pending'),
(TIMESTAMP '2024-05-19 14:15:00', 35, 59, NULL, 16, 'Pending', 'Open');

UPDATE ticket SET latest_status_change_id = 57 WHERE ticket_id = 16;
UPDATE ticket SET latest_status_change_id = 58 WHERE ticket_id = 16;
UPDATE ticket SET latest_status_change_id = 59 WHERE ticket_id = 16;
UPDATE ticket SET latest_status_change_id = 60 WHERE ticket_id = 16;

-- Ticket 18 (Broken Help Link)
INSERT INTO message (ticket_id, user_id, text, created_at) VALUES
(18, 2, 'Can you check why the help link in the footer leads to a 404 page?', TIMESTAMP '2024-05-22 14:30:00'),
(18, 6, 'We are investigating the broken help link issue. Can you send a screenshot?', TIMESTAMP '2024-05-22 14:45:00'),
(18, 2, 'Here is a screenshot of the 404 error.', TIMESTAMP '2024-05-22 15:00:00'),
(18, 6, 'The help link should now direct to the correct page. Please verify.', TIMESTAMP '2024-05-22 15:15:00');

INSERT INTO ticket_status_change (changed_at, message_id, previous_change_id, rating_id, ticket_id, status_for_assigned, status_for_creator) VALUES
(TIMESTAMP '2024-05-22 14:30:00', 36, 18, NULL, 18, 'Open', 'Pending'),
(TIMESTAMP '2024-05-22 14:45:00', 37, 61, NULL, 18, 'Pending', 'Open'),
(TIMESTAMP '2024-05-22 15:00:00', 38, 62, NULL, 18, 'Open', 'Pending'),
(TIMESTAMP '2024-05-22 15:15:00', 39, 63, NULL, 18, 'Pending', 'Open');

UPDATE ticket SET latest_status_change_id = 61 WHERE ticket_id = 18;
UPDATE ticket SET latest_status_change_id = 62 WHERE ticket_id = 18;
UPDATE ticket SET latest_status_change_id = 63 WHERE ticket_id = 18;
UPDATE ticket SET latest_status_change_id = 64 WHERE ticket_id = 18;

INSERT INTO rating (rating, created_at, created_by, rated_user_id, ticket_id, text) VALUES
(5, TIMESTAMP '2024-05-22 15:30:00', 2, 6, 18, 'Help link issue fixed. Appreciate it!');

INSERT INTO ticket_status_change (changed_at, message_id, previous_change_id, rating_id, ticket_id, status_for_assigned, status_for_creator) VALUES
(TIMESTAMP '2024-05-22 15:30:00', NULL, 64, 6, 18, 'Closed', 'Closed');

UPDATE ticket SET latest_status_change_id = 65 WHERE ticket_id = 18;

-- Ticket 19 (Missing Homepage Footer)
INSERT INTO message (ticket_id, user_id, text, created_at) VALUES
(19, 5, 'We have restored the homepage footer. Please check if it appears on all screen sizes.', TIMESTAMP '2024-05-23 09:30:00');

INSERT INTO ticket_status_change (changed_at, message_id, previous_change_id, rating_id, ticket_id, status_for_assigned, status_for_creator) VALUES
(TIMESTAMP '2024-05-23 09:30:00', 40, 19, NULL, 19, 'Open', 'Pending');

UPDATE ticket SET latest_status_change_id = 66 WHERE ticket_id = 19;

-- Ticket 20 (Feedback Form Error)
INSERT INTO message (ticket_id, user_id, text, created_at) VALUES
(20, 7, 'This error may be related to recent updates. We are looking into it.', TIMESTAMP '2024-05-23 11:40:00'),
(20, 7, 'Could you provide more details or a screenshot of the feedback form error?', TIMESTAMP '2024-05-23 11:50:00'),
(20, 4, 'I still get a server error when submitting the feedback form. Here is a screenshot.', TIMESTAMP '2024-05-23 12:00:00');

INSERT INTO ticket_status_change (changed_at, message_id, previous_change_id, rating_id, ticket_id, status_for_assigned, status_for_creator) VALUES
(TIMESTAMP '2024-05-23 11:40:00', 41, 20, NULL, 20, 'Open', 'Pending'),
(TIMESTAMP '2024-05-23 11:50:00', 42, 67, NULL, 20, 'Pending', 'Open'),
(TIMESTAMP '2024-05-23 12:00:00', 43, 68, NULL, 20, 'Open', 'Pending');

UPDATE ticket SET latest_status_change_id = 67 WHERE ticket_id = 20;
UPDATE ticket SET latest_status_change_id = 68 WHERE ticket_id = 20;
UPDATE ticket SET latest_status_change_id = 69 WHERE ticket_id = 20;
