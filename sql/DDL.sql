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
    member_id INT PRIMARY KEY REFERENCES "User"(user_id),
    phone_number VARCHAR(20),
    date_of_birth DATE,
    gender VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS Trainer (
    trainer_id INT PRIMARY KEY REFERENCES "User"(user_id)
);

CREATE TABLE IF NOT EXISTS Admin (
    admin_id INT PRIMARY KEY REFERENCES "User"(user_id)
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
    status VARCHAR(255) NOT NULL DEFAULT 'OK'
);

CREATE TABLE IF NOT EXISTS Class (
    class_id SERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL,
    description TEXT
);

CREATE TABLE IF NOT EXISTS ClassSession (
    session_id SERIAL PRIMARY KEY,
    class_type_id INT REFERENCES Class(class_id) NOT NULL,
    trainer_id INT REFERENCES Trainer(trainer_id) NOT NULL,
    room_id INT REFERENCES Room(room_id) NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    max_capacity INT,
    UNIQUE (room_id, start_time)
);

CREATE TABLE IF NOT EXISTS ClassGroup(
    group_id SERIAL PRIMARY KEY,
    member_id INT REFERENCES Member(member_id) NOT NULL,
    session_id INT REFERENCES ClassSession(session_id) NOT NULL,
    enrollment_date TIMESTAMP NOT NULL,
    UNIQUE (member_id, session_id)
);

CREATE TABLE IF NOT EXISTS PTSession (
    pt_session_id SERIAL PRIMARY KEY,
    member_id INT REFERENCES Member(member_id) NOT NULL,
    trainer_id INT REFERENCES Trainer(trainer_id) NOT NULL,
    room_id INT REFERENCES Room(room_id), 
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    status VARCHAR(255) NOT NULL DEFAULT 'scheduled',
    UNIQUE (trainer_id, start_time)
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
    availability_id SERIAL PRIMARY KEY,
    trainer_id INT REFERENCES Trainer(trainer_id) NOT NULL,
    start_day DATE NOT NULL,
    end_day DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL
);

CREATE TABLE IF NOT EXISTS Maintenance (
    maintenance_id SERIAL PRIMARY KEY,
    equipment_id INT REFERENCES Equipment(equipment_id) NOT NULL,
    room_id INT REFERENCES Room(room_id),
    report_date TIMESTAMP NOT NULL,
    issue_description TEXT,
    status VARCHAR(255) NOT NULL DEFAULT 'reported',
    issue_resolved_date TIMESTAMP
);