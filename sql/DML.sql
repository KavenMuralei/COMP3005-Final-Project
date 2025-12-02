--members
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

--trainer
WITH new_user AS (
  INSERT INTO "User"(first_name, last_name, email, user_password, user_type)
  VALUES ('Ansh','Sharma','anshsharma@cmail.carleton.ca', 'password', 1)
  RETURNING user_id
)
INSERT INTO Trainer(trainer_id)
SELECT user_id FROM new_user;

--admin

WITH new_user AS (
  INSERT INTO "User"(first_name, last_name, email, user_password, user_type)
  VALUES ('Kaven','Muraleitharan','kavenmuraleitharan@cmail.carleton.ca', 'password', 2)
  RETURNING user_id
)
INSERT INTO Admin(admin_id)
SELECT user_id FROM new_user;

-- rooms
INSERT INTO Room (room_name, capacity, location_details)
VALUES ('UNASSIGNED', 15, 'N/A');

INSERT INTO Room (room_name, capacity, location_details)
VALUES ('Room 201', 30, 'First floor near reception');

INSERT INTO Room (room_name, capacity, location_details)
VALUES ('Room 307', 20, 'Second floor yoga room');

INSERT INTO Room (room_name, capacity, location_details)
VALUES ('Room 102', 25, 'Basement strength training room');

INSERT INTO Room (room_name, capacity, location_details)
VALUES ('Room 202', 40, 'First floor kick boxing room');

INSERT INTO Room (room_name, capacity, location_details)
VALUES ('Room 405', 15, 'Third floor wellness room');

-- equipment
INSERT INTO Equipment (name, status, location_details)
VALUES ('Dumbells', 'OK', 'Room 102 - Basement strength training room');

INSERT INTO Equipment (name, status, location_details)
VALUES ('Treadmill', 'OK', 'Room 201 - First floor near reception');

INSERT INTO Equipment (name, status, location_details)
VALUES ('Yoga balls', 'OK', 'Room 307 - Second floor yoga room');

INSERT INTO Equipment (name, status, location_details)
VALUES ('Punching Bag', 'OK', 'First floor kick boxing room');

INSERT INTO Equipment (name, status, location_details)
VALUES ('Massage Chair', 'OK', 'Third floor wellness room');

-- classes
INSERT INTO Class (name, description)
VALUES ('Yoga', 'Yoga class focusing on breathing, flexibility, and relaxation.');

INSERT INTO Class (name, description)
VALUES ('Cardio', 'High-intensity cardio workouts.');

INSERT INTO Class (name, description)
VALUES ('Strength Training', 'Weightlifting and resistance exercises to build some muscle.');

INSERT INTO Class (name, description)
VALUES ('Kickboxing', 'Kickboxing session which combines martial arts with some sparring.');

INSERT INTO Class (name, description)
VALUES ('Meditation', 'Meditation practices for stress relief and focussing.');