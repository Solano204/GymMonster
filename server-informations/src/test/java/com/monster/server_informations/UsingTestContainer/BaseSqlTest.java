package com.monster.server_informations.UsingTestContainer;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import com.monster.server_informations.ServerInformationsApplication;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import infraestrucutre.Adapters.Drivens.Repositories.ClientRepository;

@Import(SqlScriptExecutor.class)
public abstract class BaseSqlTest {


    @Autowired
    private SqlScriptExecutor sqlScriptExecutor;
    
    static MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0.33")
            .withDatabaseName("mscv_information_test")
            .withUsername("root")
            .withPassword("testing");

    static {
        mysqlContainer.start();
    }

    @DynamicPropertySource
    static void mysqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", () ->
                String.format("r2dbc:mysql://%s:%d/mscv_information_test",
                        mysqlContainer.getHost(),
                        mysqlContainer.getFirstMappedPort())
        );
        registry.add("spring.r2dbc.username", mysqlContainer::getUsername);
        registry.add("spring.r2dbc.password", mysqlContainer::getPassword);

       
    }

    @BeforeEach
    void setup() {
        // Subclasses can override this method if they don't want to execute scripts.
        executeSqlScripts();
    }

    protected void executeSqlScripts() {
        sqlScriptExecutor.execute();
    }
}