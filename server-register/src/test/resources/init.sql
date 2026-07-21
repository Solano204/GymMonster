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
