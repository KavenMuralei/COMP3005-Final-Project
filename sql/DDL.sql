
CREATE TABLE Member (
    member_id SERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    contact_email VARCHAR(255) UNIQUE NOT NULL,
    phone_number VARCHAR(20),
    date_of_birth DATE,
    gender VARCHAR(255)
);

CREATE TABLE Trainer (
    trainer_id SERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
);

CREATE TABLE Admin (
    admin_id SERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    contact_email VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE Room (
    room_id SERIAL PRIMARY KEY,
    room_name VARCHAR(255) UNIQUE NOT NULL,
    capacity INT CHECK (capacity > 0),
    location_details VARCHAR(255)
);

CREATE TABLE Equipment (
    equipment_id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'OK'
);

CREATE TABLE Class (
    class_id SERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
);

