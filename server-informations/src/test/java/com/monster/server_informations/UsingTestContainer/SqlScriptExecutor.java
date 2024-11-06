package com.monster.server_informations.UsingTestContainer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;


@TestConfiguration
public class SqlScriptExecutor {

    @Autowired
    private DatabaseClient databaseClient;

    public void execute() {
        // SQL statement for creating the membership table
        String createMembershipTable = "CREATE TABLE membership (" +
                "id INT PRIMARY KEY AUTO_INCREMENT, " +
                "membership_type VARCHAR(255), " +
                "description TEXT, " +
                "has_cardio BOOLEAN, " +
                "has_pool BOOLEAN, " +
                "has_food_court BOOLEAN" +
                ");";

        // SQL statement for creating the detail_user table
        String createDetailUserTable = "CREATE TABLE detail_user (" +
                "id INT PRIMARY KEY, " +
                "name VARCHAR(255), " +
                "second_name VARCHAR(255), " +
                "last_name_m VARCHAR(255), " +
                "last_name_p VARCHAR(255), " +
                "age VARCHAR(3), " +
                "weight VARCHAR(10), " +
                "height VARCHAR(10)" +
                ");";

        // SQL statement for creating the per_trainer table
        String createPerTrainerTable = "CREATE TABLE per_trainer (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "username VARCHAR(255) NOT NULL, " +
                "password VARCHAR(255) NOT NULL, " +
                "email VARCHAR(255) NOT NULL, " +
                "id_detail INT, " +
                "start_date DATE, " +
                "FOREIGN KEY (id_detail) REFERENCES detail_user(id)" +
                ");";

        // SQL statement for creating the clients table
        String createClientsTable = "CREATE TABLE clients (" +
                "id INT PRIMARY KEY AUTO_INCREMENT, " +
                "username VARCHAR(50) NOT NULL, " +
                "password VARCHAR(100) NOT NULL, " +
                "email VARCHAR(100) NOT NULL, " +
                "id_membership INT, " +
                "FOREIGN KEY (id_membership) REFERENCES membership(id), " +
                "id_trainer INT, " +
                "FOREIGN KEY (id_trainer) REFERENCES per_trainer(id), " +
                "id_detail INT, " +
                "FOREIGN KEY (id_detail) REFERENCES detail_user(id)" +
                ");";

        // SQL statement for creating the Inscription table
        String createInscriptionTable = "CREATE TABLE Inscription (" +
                "id INT PRIMARY KEY, " +
                "id_client INT NOT NULL, " +
                "date_inscription DATE NOT NULL, " +
                "start_month DATE NOT NULL, " +
                "end_month DATE NOT NULL, " +
                "price DECIMAL(10, 2) NOT NULL, " +
                "FOREIGN KEY (id_client) REFERENCES clients(id)" +
                ");";

        // Execute all the SQL statements using DatabaseClient
        databaseClient.sql(createMembershipTable).fetch().rowsUpdated().block();
        databaseClient.sql(createDetailUserTable).fetch().rowsUpdated().block();
        databaseClient.sql(createPerTrainerTable).fetch().rowsUpdated().block();
        databaseClient.sql(createClientsTable).fetch().rowsUpdated().block();
        databaseClient.sql(createInscriptionTable).fetch().rowsUpdated().block();
    }
}
