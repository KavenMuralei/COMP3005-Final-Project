DROP TABLE IF EXISTS "User" CASCADE;
DROP TABLE IF EXISTS Member CASCADE;
DROP TABLE IF EXISTS Trainer CASCADE;
DROP TABLE IF EXISTS Admin CASCADE;
DROP TABLE IF EXISTS Room CASCADE;
DROP TABLE IF EXISTS Equipment CASCADE;
DROP TABLE IF EXISTS Contains_Equipment CASCADE;
DROP TABLE IF EXISTS Bookings CASCADE;
DROP TABLE IF EXISTS Class CASCADE;
DROP TABLE IF EXISTS ClassSession CASCADE;
DROP TABLE IF EXISTS ClassGroup CASCADE;
DROP TABLE IF EXISTS Join_Group CASCADE;
DROP TABLE IF EXISTS PTSession CASCADE;
DROP TABLE IF EXISTS HealthMetric CASCADE;
DROP TABLE IF EXISTS TrainerAvailability CASCADE;
DROP TABLE IF EXISTS Maintenance CASCADE;

CREATE TABLE IF NOT EXISTS "User" (
    user_id SERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    user_password VARCHAR(255) NOT NULL,
    user_type INT,
    CHECK (user_type BETWEEN 0 AND 2)
);

CREATE TABLE IF NOT EXISTS Member (
    member_id SERIAL PRIMARY KEY,
    phone_number VARCHAR(20),
    date_of_birth DATE,
    gender VARCHAR(255),
    user_id INT REFERENCES "User"(user_id)
);

CREATE TABLE IF NOT EXISTS Trainer (
    trainer_id SERIAL PRIMARY KEY,
    user_id INT REFERENCES "User"(user_id)
);

CREATE TABLE IF NOT EXISTS Admin (
    admin_id SERIAL PRIMARY KEY,
    user_id INT REFERENCES "User"(user_id)
);

CREATE TABLE IF NOT EXISTS Room (
    room_id SERIAL PRIMARY KEY,
    room_name VARCHAR(255) UNIQUE NOT NULL,
    capacity INT CHECK (capacity > 0),
    location_details VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS Equipment (
    equipment_id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    status VARCHAR(255) NOT NULL DEFAULT 'OK',
    location_details VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS Contains_Equipment (
    room_id INT REFERENCES Room (room_id) NOT NULL,
    equipment_id INT REFERENCES Equipment (equipment_id) NOT NULL
);

CREATE TABLE IF NOT EXISTS Bookings (
    booking_id SERIAL PRIMARY KEY,
    trainer_id INT REFERENCES Trainer(trainer_id) NOT NULL,
    room_id INT REFERENCES Room(room_id) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    day DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS Class (
    class_id SERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL,
    description TEXT
);

CREATE TABLE IF NOT EXISTS ClassGroup(
    group_id SERIAL PRIMARY KEY,
    max_capacity INT
);

CREATE TABLE IF NOT EXISTS ClassSession (
    session_id SERIAL PRIMARY KEY,
    class_id INT REFERENCES Class(class_id) NOT NULL,
    group_id INT REFERENCES ClassGroup(group_id) NOT NULL,
    booking_id INT REFERENCES Bookings(booking_id) NOT NULL
);

CREATE TABLE IF NOT EXISTS Join_Group (
    group_id INT REFERENCES Room(room_id),
    member_id INT REFERENCES Member(member_id),
    enrollment_date DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS PTSession (
    pt_session_id SERIAL PRIMARY KEY,
    member_id INT REFERENCES Member(member_id) NOT NULL,
    booking_id INT REFERENCES Bookings(booking_id) NOT NULL,
    status VARCHAR(255) NOT NULL DEFAULT 'scheduled'
);

CREATE TABLE IF NOT EXISTS HealthMetric (
    metric_id SERIAL PRIMARY KEY,
    member_id INT REFERENCES Member(member_id) NOT NULL,
    time TIMESTAMP NOT NULL,
    weight NUMERIC(6,2),
    height_cm NUMERIC(5,2),
    bodyfat_percent NUMERIC(4,2),
    bpm INT
);

CREATE TABLE IF NOT EXISTS FitnessGoal(
    goal_id SERIAL PRIMARY KEY,
    member_id INT REFERENCES Member(member_id) NOT NULL,
    goal_type VARCHAR(255) NOT NULL,
    target NUMERIC(8,2),
    start_of_goal DATE,
    end_of_goal DATE,
    status VARCHAR(255) NOT NULL DEFAULT 'ongoing'
);

CREATE TABLE IF NOT EXISTS TrainerAvailability (
    day DATE PRIMARY KEY NOT NULL,
    trainer_id INT REFERENCES Trainer(trainer_id) NOT NULL,
    shift_start TIME NOT NULL,
    shift_end TIME NOT NULL
);

CREATE TABLE IF NOT EXISTS Maintenance (
    record_id SERIAL PRIMARY KEY,
    equipment_id INT REFERENCES Equipment(equipment_id) NOT NULL,
    report_date TIMESTAMP NOT NULL,
    issue_description TEXT,
    status VARCHAR(255) NOT NULL DEFAULT 'reported'
);