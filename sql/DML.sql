-- INSERT INTO Users (first_name, last_name, email, password, date_of_birth, gender, role)
-- VALUES
-- ('kaven', 'mura', 'kavenmura@gmail.com', 'pass', '2005-07-26', 'male', 'admin'),
-- ('test1', 'test11', 'test1@test.com', 'test1pass', '1900-01-01', 'female', 'trainer'),
-- ('test2', 'test22', 'test2@test.com', 'test2pass', '1900-01-01', 'other', 'member');


-- INSERT INTO Members (member_id) VALUES (1);
-- INSERT INTO Trainers (trainer_id) VALUES (2);
-- INSERT INTO Admins (admin_id) VALUES (3);

-- SELECT * FROM Users;

WITH new_user AS (
  INSERT INTO "User"(first_name, last_name, email, user_password, user_type)
  VALUES ('Dean','Sabbah','deansabbah@cmail.carleton.ca', 'password', 0)
  RETURNING user_id
)
INSERT INTO Member(member_id, phone_number, date_of_birth, gender)
SELECT user_id, '123-456-7890', '2002-10-07', 'male' FROM new_user;

WITH new_user AS (
  INSERT INTO "User"(first_name, last_name, email, user_password, user_type)
  VALUES ('Bill','Nye','billnye@science.guy', 'password', 0)
  RETURNING user_id
)
INSERT INTO Member(member_id, phone_number, date_of_birth, gender)
SELECT user_id, '123-456-7890', '1955-11-27', 'male' FROM new_user;

WITH new_user AS (
  INSERT INTO "User"(first_name, last_name, email, user_password, user_type)
  VALUES ('April','Suung','april.suung@email.com', 'password', 0)
  RETURNING user_id
)
INSERT INTO Member(member_id, phone_number, date_of_birth, gender)
SELECT user_id, '123-456-7890', '1998-02-16', 'female' FROM new_user;

WITH new_user AS (
  INSERT INTO "User"(first_name, last_name, email, user_password, user_type)
  VALUES ('Ansh','Sharma','anshsharma@cmail.carleton.ca', 'password', 1)
  RETURNING user_id
)
INSERT INTO Trainer(trainer_id)
SELECT user_id FROM new_user;

WITH new_user AS (
  INSERT INTO "User"(first_name, last_name, email, user_password, user_type)
  VALUES ('Kaven','Muraleitharan','kavenmuraleitharan@cmail.carleton.ca', 'password', 2)
  RETURNING user_id
)
INSERT INTO Admin(admin_id)
SELECT user_id FROM new_user;

-- rooms
INSERT INTO Room (room_name, capacity, location_details)
VALUES ('Room 201', 30, 'First floor near reception');

INSERT INTO Room (room_name, capacity, location_details)
VALUES ('Room 307', 20, 'Second floor yoga room');

INSERT INTO Room (room_name, capacity, location_details)
VALUES ('Room 102', 25, 'Basement strength training room');

INSERT INTO Room (room_name, capacity, location_details)
VALUES ('Strength Training Room', 40, 'First floor kick boxing room');

INSERT INTO Room (room_name, capacity, location_details)
VALUES ('Room 405', 15, 'Third floor wellness room');