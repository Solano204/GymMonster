CREATE TABLE detail_user (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    second_name VARCHAR(255),
    last_name_m VARCHAR(255),
    last_name_p VARCHAR(255),
    age VARCHAR(3),
    weight VARCHAR(10),
    height VARCHAR(10)
);

CREATE TABLE detail_class_trainer (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    second_name VARCHAR(255),
    last_name_m VARCHAR(255),
    last_name_p VARCHAR(255),
    age VARCHAR(3),
    weight VARCHAR(10),
    height VARCHAR(10)
);

CREATE TABLE detail_per_trainer (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    second_name VARCHAR(255),
    last_name_m VARCHAR(255),
    last_name_p VARCHAR(255),
    age VARCHAR(3),
    weight VARCHAR(10),
    height VARCHAR(10)
);

CREATE TABLE per_trainer (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    id_detail INT,
    start_date DATE,
    FOREIGN KEY (id_detail) REFERENCES detail_per_trainer(id)
);

CREATE TABLE membership (
    id INT PRIMARY KEY AUTO_INCREMENT,
    membership_type VARCHAR(255),
    description TEXT,
    has_cardio BOOLEAN,
    has_pool BOOLEAN,
    has_food_court BOOLEAN
);

CREATE TABLE clients (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    id_membership INT,
    FOREIGN KEY (id_membership) REFERENCES membership(id),
    id_trainer INT,
    FOREIGN KEY (id_trainer) REFERENCES per_trainer(id),
    id_detail INT,
    FOREIGN KEY (id_detail) REFERENCES detail_user(id),
    CONSTRAINT uq_clients_username UNIQUE (username),
    CONSTRAINT uq_clients_email UNIQUE (email)
);

CREATE TABLE Inscription (
    id INT PRIMARY KEY AUTO_INCREMENT,
    id_client INT NOT NULL,
    date_inscription DATE NOT NULL,
    start_month DATE NOT NULL,
    end_month DATE NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (id_client) REFERENCES clients(id)
);

CREATE TABLE specialty (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description TEXT
);

CREATE TABLE trainers_class (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    id_detail INT,
    start_date DATE,
    FOREIGN KEY (id_detail) REFERENCES detail_class_trainer(id)
);

CREATE TABLE work_class (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    duration VARCHAR(50)
);

CREATE TABLE schedules (
    id INT PRIMARY KEY AUTO_INCREMENT,
    day VARCHAR(50),
    start_time VARCHAR(50),
    end_time VARCHAR(50)
);

CREATE TABLE schedulesGym (
    id INT PRIMARY KEY AUTO_INCREMENT,
    day VARCHAR(50),
    start_time VARCHAR(50),
    end_time VARCHAR(50)
);

CREATE TABLE work_class_schedules (
    work_class_id INT,
    schedule_id INT,
    PRIMARY KEY (work_class_id, schedule_id),
    FOREIGN KEY (work_class_id) REFERENCES work_class(id),
    FOREIGN KEY (schedule_id) REFERENCES schedules(id)
);

CREATE TABLE pertrainer_specialty (
    pertrainer_id INT,
    specialty_id INT,
    PRIMARY KEY (pertrainer_id, specialty_id),
    FOREIGN KEY (pertrainer_id) REFERENCES per_trainer(id),
    FOREIGN KEY (specialty_id) REFERENCES specialty(id)
);

CREATE TABLE trainer_class_specialty (
    trainer_class_id INT,
    specialty_id INT,
    PRIMARY KEY (trainer_class_id, specialty_id),
    FOREIGN KEY (trainer_class_id) REFERENCES trainers_class(id),
    FOREIGN KEY (specialty_id) REFERENCES specialty(id)
);

CREATE TABLE trainer_class_work_class (
    trainer_class_id INT,
    work_class_id INT,
    PRIMARY KEY (trainer_class_id, work_class_id),
    FOREIGN KEY (trainer_class_id) REFERENCES trainers_class(id) ON DELETE CASCADE,
    FOREIGN KEY (work_class_id) REFERENCES work_class(id) ON DELETE CASCADE
);

CREATE TABLE client_work_class (
    client_id INT NOT NULL,
    work_class_id INT NOT NULL,
    PRIMARY KEY (client_id, work_class_id),
    FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE CASCADE,
    FOREIGN KEY (work_class_id) REFERENCES work_class(id) ON DELETE CASCADE
);

CREATE TABLE promotion (
    id INT PRIMARY KEY AUTO_INCREMENT,
    description TEXT,
    duration VARCHAR(255),
    percentage_discount INT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    active BOOLEAN NOT NULL
);

-- Matches the real schema (init.sql at the repo root) exactly: the table is
-- "equipament", not "equipment". EquipamentRepository's @Table("equipment")
-- annotation does not match this - see EquipamentRepositoryIntegrationTest,
-- which documents that mismatch as a currently-failing test rather than
-- silently "fixing" the schema to make it pass.
CREATE TABLE equipament (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(100),
    start_date DATE,
    end_date DATE,
    age_status VARCHAR(30)
);

CREATE TABLE pool (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    date_clean DATE,
    start_date DATE,
    end_date DATE
);
