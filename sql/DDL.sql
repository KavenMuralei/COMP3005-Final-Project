DROP TABLE IF EXISTS "User" CASCADE;
DROP TABLE IF EXISTS Member CASCADE;
DROP TABLE IF EXISTS Trainer CASCADE;
DROP TABLE IF EXISTS Admin CASCADE;
DROP TABLE IF EXISTS Room CASCADE;
DROP TABLE IF EXISTS Equipment CASCADE;
DROP TABLE IF EXISTS Bookings CASCADE;
DROP TABLE IF EXISTS Class CASCADE;
DROP TABLE IF EXISTS ClassSession CASCADE;
DROP TABLE IF EXISTS ClassGroup CASCADE;
DROP TABLE IF EXISTS Join_Group CASCADE;
DROP TABLE IF EXISTS PTSession CASCADE;
DROP TABLE IF EXISTS HealthMetric CASCADE;
DROP TABLE IF EXISTS FitnessGoal CASCADE;
DROP TABLE IF EXISTS TrainerAvailability CASCADE;
DROP TABLE IF EXISTS Maintenance CASCADE;

CREATE TABLE IF NOT EXISTS "User" (
    user_id SERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    user_password VARCHAR(255) NOT NULL,
    user_type INT CHECK (user_type BETWEEN 0 AND 2)
);

CREATE TABLE IF NOT EXISTS Member (
    member_id INT PRIMARY KEY REFERENCES "User"(user_id) ON DELETE CASCADE,
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
    status VARCHAR(255) NOT NULL DEFAULT 'OK',
    location_details VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS Bookings (
    booking_id SERIAL PRIMARY KEY,
    trainer_id INT REFERENCES Trainer(trainer_id) NOT NULL,
    room_id INT REFERENCES Room(room_id) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    day DATE NOT NULL,
    UNIQUE (room_id, day, start_time),
    UNIQUE (trainer_id, day, start_time)
);

CREATE TABLE IF NOT EXISTS Class (
    class_id SERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL,
    description TEXT
);

CREATE TABLE IF NOT EXISTS ClassGroup (
    group_id SERIAL PRIMARY KEY,
    class_id INT NOT NULL REFERENCES Class(class_id),
    max_capacity INT
);

CREATE TABLE IF NOT EXISTS ClassSession (
    session_id SERIAL PRIMARY KEY,
    class_id INT REFERENCES Class(class_id) NOT NULL,
    group_id INT REFERENCES ClassGroup(group_id) NOT NULL,
    booking_id INT REFERENCES Bookings(booking_id) NOT NULL
);

CREATE TABLE IF NOT EXISTS Join_Group (
    group_id INT REFERENCES ClassGroup(group_id) NOT NULL,
    member_id INT REFERENCES Member(member_id) NOT NULL,
    enrollment_date DATE NOT NULL,
    PRIMARY KEY (group_id, member_id)
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

CREATE TABLE IF NOT EXISTS FitnessGoal (
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
    day DATE NOT NULL,
    shift_start TIME NOT NULL,
    shift_end TIME NOT NULL,
	UNIQUE(trainer_id, day)
);

CREATE TABLE IF NOT EXISTS Maintenance (
    record_id SERIAL PRIMARY KEY,
    equipment_id INT REFERENCES Equipment(equipment_id) NOT NULL,
    room_id INT REFERENCES Room(room_id),
    report_date TIMESTAMP NOT NULL,
    issue_description TEXT,
    status VARCHAR(255) NOT NULL DEFAULT 'reported'
);

CREATE INDEX IF NOT EXISTS name_index ON "User" (
    lower(first_name), lower(last_name)
);

-- member dashboard operation (view)
CREATE OR REPLACE VIEW member_dashboard_view AS
SELECT 
    m.member_id,
    u.first_name,
    u.last_name,

    hm.weight AS latest_weight,
    hm.height_cm AS latest_height,
    hm.bodyfat_percent AS latest_bodyfat,
    hm.bpm AS latest_bpm,
    hm.time AS last_metric_time,

    fg.goal_type,
    fg.target,
    fg.status AS goal_status,

    (
        SELECT COUNT(*)
        FROM Join_Group jg
        WHERE jg.member_id = m.member_id
    ) AS total_classes_joined

FROM Member m
JOIN "User" u ON u.user_id = m.member_id
LEFT JOIN LATERAL (
    SELECT * FROM HealthMetric
    WHERE member_id = m.member_id
    ORDER BY time DESC
    LIMIT 1
) hm ON TRUE
LEFT JOIN FitnessGoal fg ON fg.member_id = m.member_id;


-- trigger for health metric to fitness goal completion
CREATE OR REPLACE FUNCTION update_goal_on_metric()
RETURNS TRIGGER AS $$
DECLARE
    goal_target NUMERIC;
    goal_name VARCHAR(255);
BEGIN
    SELECT target, goal_type
    INTO goal_target, goal_name
    FROM FitnessGoal
    WHERE member_id = NEW.member_id
      AND status = 'ongoing'
    LIMIT 1;

    IF NOT FOUND THEN
        RETURN NEW; -- no goal
    END IF;

    -- weight check
    IF goal_name = 'weight' AND NEW.weight IS NOT NULL THEN
        IF NEW.weight <= goal_target THEN
            UPDATE FitnessGoal
            SET status = 'completed',
                end_of_goal = CURRENT_DATE
            WHERE member_id = NEW.member_id
              AND status = 'ongoing';
        END IF;
    END IF;

    -- body fat check
    IF goal_name = 'bodyfat' AND NEW.bodyfat_percent IS NOT NULL THEN
        IF NEW.bodyfat_percent <= goal_target THEN
            UPDATE FitnessGoal
            SET status = 'completed',
                end_of_goal = CURRENT_DATE
            WHERE member_id = NEW.member_id
              AND status = 'ongoing';
        END IF;
    END IF;

    -- height check
    IF goal_name = 'height' AND NEW.height_cm IS NOT NULL THEN
        IF NEW.height_cm >= goal_target THEN
            UPDATE FitnessGoal
            SET status = 'completed',
                end_of_goal = CURRENT_DATE
            WHERE member_id = NEW.member_id
              AND status = 'ongoing';
        END IF;
    END IF;

    -- bpm check
    IF goal_name = 'bpm' AND NEW.bpm IS NOT NULL THEN
        IF NEW.bpm <= goal_target THEN
            UPDATE FitnessGoal
            SET status = 'completed',
                end_of_goal = CURRENT_DATE
            WHERE member_id = NEW.member_id
              AND status = 'ongoing';
        END IF;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
CREATE TRIGGER check_goal_completion
AFTER INSERT ON HealthMetric
FOR EACH ROW
EXECUTE FUNCTION update_goal_on_metric();
