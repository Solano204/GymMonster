package com.monster.server_informations.UsingTestContainer;

import java.io.File;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.utility.MountableFile;

import dasniko.testcontainers.keycloak.KeycloakContainer;

public abstract class BaseKeycloakTest {

    static KeycloakContainer keycloakContainer = new KeycloakContainer("quay.io/keycloak/keycloak:21.0.2")
            .withExposedPorts(8181)
            .withEnv("KEYCLOAK_USER", "admin")
            .withEnv("KEYCLOAK_PASSWORD", "admin")
            .withEnv("DB_VENDOR", "POSTGRES")
            .withEnv("DB_ADDR", "db-keycloak")
            .withEnv("DB_DATABASE", "db_keycloak")
            .withEnv("DB_PORT", "5433")
            .withEnv("DB_USER", "uncledave")
            .withEnv("DB_PASSWORD", "Test123")
            .withCustomCommand("start-dev");

    // Path to the directory containing your Keycloak configuration files
    private static final String CONFIG_DIR = "C:/Users/GAMER/Pictures/MICRO-PROJEC2/Storage Keycloak";

    static {
        // Copy configuration files to the container before starting it
        File configDir = new File(CONFIG_DIR);
        if (configDir.exists() && configDir.isDirectory()) {
            for (File file : configDir.listFiles()) {
                if (file.isFile()) {
                    keycloakContainer.withCopyFileToContainer(
                            MountableFile.forHostPath(file.getAbsolutePath()), 
                            "/opt/keycloak/data/" + file.getName()
                    );
                }
            }
        }
        keycloakContainer.start();
    }

    @DynamicPropertySource
    static void keycloakProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri", () ->
                String.format("http://%s:%d/realms/docker-real",
                        keycloakContainer.getHost(),
                        keycloakContainer.getMappedPort(8181))
        );
        registry.add("spring.security.oauth2.resourceserver.jwt.jwk-set-uri", () ->
                String.format("http://%s:%d/realms/docker-real/protocol/openid-connect/certs",
                        keycloakContainer.getHost(),
                        keycloakContainer.getMappedPort(8181))
        );
        // Additional properties related to Keycloak can be added here
    }

}