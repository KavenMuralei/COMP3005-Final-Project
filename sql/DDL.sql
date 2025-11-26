CREATE TABLE Users (
	user_id SERIAL PRIMARY KEY,
	first_name TEXT NOT NULL,
	last_name TEXT NOT NULL,
	email TEXT NOT NULL UNIQUE,
	password TEXT NOT NULL,
	date_of_birth DATE NOT NULL,
	gender TEXT NOT NULL,
	role TEXT NOT NULL CHECK (role IN ('member', 'trainer', 'admin'))
);

CREATE TABLE Members (
    member_id INT PRIMARY KEY
        REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE Trainers (
    trainer_id INT PRIMARY KEY
        REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE Admins (
    admin_id INT PRIMARY KEY
        REFERENCES users(user_id) ON DELETE CASCADE
);