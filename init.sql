use mscv_information;

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
    id INT AUTO_INCREMENT PRIMARY KEY, -- Auto-increment for the primary key
    username VARCHAR(255) NOT NULL, -- Username of the trainer
    password VARCHAR(255) NOT NULL, -- Password for the trainer
    email VARCHAR(255) NOT NULL, -- Email of the trainer
    id_detail INT, -- Foreign key to the Membership table
    start_date DATE,
    FOREIGN KEY (id_detail) REFERENCES detail_per_trainer(id)
);

CREATE TABLE membership (
    id INT PRIMARY KEY AUTO_INCREMENT,  -- The primary key field for Membership
    membership_type VARCHAR(255),  -- The column for membershipType
    description TEXT,  -- A text field for the description
    has_cardio BOOLEAN,  -- Boolean field indicating if it has cardio
    has_pool BOOLEAN,  -- Boolean field indicating if it has a pool
    has_food_court BOOLEAN  -- Boolean field indicating if it has a food court
);






CREATE TABLE clients (
    id INT AUTO_INCREMENT PRIMARY KEY, -- Auto-increment for the primary key
    username VARCHAR(50) NOT NULL,  -- Username as the shared primary key
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    id_membership INT, -- Foreign key to the Membership table
    FOREIGN KEY (id_membership) REFERENCES membership(id),-- Foreign key relationship to Membership
    id_trainer INT, -- Foreign key to the Membership table
    FOREIGN KEY (id_trainer) REFERENCES per_trainer(id), -- Foreign key relationship to Membership
    id_detail INT, -- Foreign key to the Membership table
    FOREIGN KEY (id_detail) REFERENCES detail_user(id)
);


CREATE TABLE Inscription (
    id INT PRIMARY KEY AUTO_INCREMENT,
    id_client INT NOT NULL,
    date_inscription DATE NOT NULL,
    start_month DATE NOT NULL,
    end_month DATE NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (id_client) REFERENCES clients(id)  -- Assuming `clients` table has `id` as the primary key
);







-- Table for Specialty
CREATE TABLE specialty (
    id INT PRIMARY KEY AUTO_INCREMENT, -- Auto-increment for ID
    name VARCHAR(255) NOT NULL,
    description TEXT
);


CREATE TABLE trainers_class (
     id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,  -- Username as the shared primary key
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    id_detail INT, -- Foreign key to the Membership table
    start_date DATE,
    FOREIGN KEY (id_detail) REFERENCES detail_class_trainer(id)
);


CREATE TABLE work_class (
    id INT PRIMARY KEY AUTO_INCREMENT,  -- No auto-increment, ID is handled manually
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    duration VARCHAR(50)
);


-- Table for Schedules
CREATE TABLE schedules (
    id INT PRIMARY KEY AUTO_INCREMENT,  -- No auto-increment, you will handle the ID generation manually
    day VARCHAR(50),
    start_time VARCHAR(50),
    end_time VARCHAR(50)
);


CREATE TABLE schedulesGym (
    id INT PRIMARY KEY AUTO_INCREMENT,  -- No auto-increment, you will handle the ID generation manually
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

-- Many-to-Many relationship between TrainerClass and Specialty
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

-- Join Table for Many-to-Many Relationship
CREATE TABLE trainer_class_work_class (
    trainer_class_id INT,  -- Foreign key to TrainerClass
    work_class_id INT,                -- Foreign key to WorkClass
    PRIMARY KEY (trainer_class_id, work_class_id),
    FOREIGN KEY (trainer_class_id) REFERENCES trainers_class(id) ON DELETE CASCADE,
    FOREIGN KEY (work_class_id) REFERENCES work_class(id) ON DELETE CASCADE
);

CREATE TABLE client_work_class (
    client_id INT NOT NULL, -- Foreign key to the clients table
    work_class_id INT NOT NULL, -- Foreign key to the work_class table
    PRIMARY KEY (client_id, work_class_id), -- Composite primary key
    FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE CASCADE, -- Foreign key relationship to clients
    FOREIGN KEY (work_class_id) REFERENCES work_class(id) ON DELETE CASCADE -- Foreign key relationship to work_class
);





CREATE TABLE promotion (
    id INT PRIMARY KEY AUTO_INCREMENT, -- Auto-increment for the primary key
    description TEXT,
    duration VARCHAR(255),
    percentage_discount INT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,																																
    active BOOLEAN NOT NULL
);

 

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


