CREATE TABLE account (
    user_id SERIAL PRIMARY KEY,
    user_name VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email_id VARCHAR(255) UNIQUE NOT NULL,
    auth_token VARCHAR(255)
);

CREATE TABLE profile (
    user_id INTEGER PRIMARY KEY REFERENCES account(user_id),
    display_name VARCHAR(100),
    profile_picture VARCHAR(255)
);

CREATE TABLE status (
    status_id SERIAL PRIMARY KEY,
    status_name VARCHAR(50) UNIQUE NOT NULL
);

INSERT INTO status (status_name) VALUES
('not_started'),
('brainstorming'),
('development'),
('in_progress'),
('in_progress_risk'),
('in_progress_concerned'),
('under_review'),
('halted_temp'),
('halted_perm'),
('completed');

CREATE TABLE class (
    class_id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES account(user_id),
    class_name VARCHAR(100) NOT NULL,
    period VARCHAR(20),
    overall_grade NUMERIC(5,2)
);

CREATE TABLE goals (
    goal_id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES account(user_id),
    timedate TIMESTAMP,
    notes TEXT,
    ext_links VARCHAR(255),
    status_id INTEGER REFERENCES status(status_id),
    title VARCHAR(255)
);

CREATE TABLE assignment (
    assignment_id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES account(user_id),
    timedate TIMESTAMP,
    grade VARCHAR(10),
    user_score NUMERIC(6,2),
    total_points NUMERIC(6,2),
    notes TEXT,
    ext_links VARCHAR(255),
    status_id INTEGER REFERENCES status(status_id),
    title VARCHAR(255),
    class_id INTEGER REFERENCES class(class_id)
);

CREATE TABLE event (
    event_id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES account(user_id),
    timedate TIMESTAMP,
    notes TEXT,
    ext_links VARCHAR(255),
    location VARCHAR(255),
    estimated_hours NUMERIC(5,2),
    start_time TIMESTAMP,
    end_time_bool BOOLEAN DEFAULT FALSE,
    end_time TIMESTAMP,
    ext_id_bool BOOLEAN DEFAULT FALSE,
    ext_id VARCHAR(255),
    status_id INTEGER REFERENCES status(status_id),
    title VARCHAR(255),
    class_id INTEGER REFERENCES class(class_id)
);

CREATE TABLE exam (
    exam_id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES account(user_id),
    timedate TIMESTAMP,
    grade VARCHAR(10),
    user_score NUMERIC(6,2),
    total_points NUMERIC(6,2),
    notes TEXT,
    ext_links VARCHAR(255),
    status_id INTEGER REFERENCES status(status_id),
    title VARCHAR(255),
    class_id INTEGER REFERENCES class(class_id)
);

CREATE TABLE notify_type (
    notify_type_id SERIAL PRIMARY KEY,
    event_alert BOOLEAN DEFAULT FALSE,
    assignment_alert BOOLEAN DEFAULT FALSE,
    exam_alert BOOLEAN DEFAULT FALSE,
    goal_alert BOOLEAN DEFAULT FALSE,
    schedule_alert_conflict BOOLEAN DEFAULT FALSE
);

CREATE TABLE notifications (
    notification_id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES account(user_id),
    timedate TIMESTAMP,
    notes TEXT,
    ext_links VARCHAR(255),
    notify_type_id INTEGER REFERENCES notify_type(notify_type_id),
    notify_sound_bool BOOLEAN DEFAULT FALSE,
    notify_sound VARCHAR(255)
);

CREATE TABLE file (
    file_id SERIAL PRIMARY KEY,
    ext_links VARCHAR(255),
    file_size NUMERIC(10,2),
    user_id INTEGER REFERENCES account(user_id)
);
