package com.monster.server_informations;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.MountableFile;

import java.util.List;

/**
 * Self-contained MySQL Testcontainers base for the R2DBC repository layer - unlike
 * ClientRepositoryTest/KeycloakServiceImplIntegrationTest (which need docker-compose-test-inf.yaml
 * manually started first, per "Steps to Test with Server-Configuration.txt"), tests extending this
 * need no external setup: the container starts itself and loads init.sql (the real schema, copied
 * into src/test/resources) before any repository bean is created. Mirrors the pattern gateway's
 * FATHER already established for Redis in this same portfolio.
 *
 * Testcontainers' "singleton container" pattern, NOT @Testcontainers/@Container: the static
 * `mysql` field below is inherited (not redeclared) by all 14 repository test subclasses, so
 * it's the same field/object for every one of them. @Testcontainers manages container lifecycle
 * per test class (start in beforeAll, stop in afterAll) - since it's the same shared instance,
 * the first subclass's afterAll stops the ONE container, and every subsequent subclass then
 * tries to reconnect through a port mapping that no longer points at anything running
 * ("Connection refused"). Starting it once here, with no JUnit-managed stop, avoids that -
 * Testcontainers' own Ryuk reaper removes it when the JVM exits.
 */
// @DataR2dbcTest's default @SpringBootConfiguration auto-detection scans upward from each
// subclass's OWN package (infraestrucutre.Adapters.Drivens.Repositories), which never reaches
// ServerInformationsApplication's package (com.monster.server_informations) - declaring it here
// on the shared base fixes every subclass at once instead of annotating each one individually.
@ContextConfiguration(classes = ServerInformationsApplication.class)
public abstract class R2dbcContainerTest {

    // MySQL's entrypoint briefly opens the port for an internal init server before the real
    // server starts; the default wait strategy can latch onto that first port-open and hand
    // control back before the server is actually ready, causing flaky connection-refused errors.
    static final MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("mscv_information")
            .withUsername("test")
            .withPassword("test")
            .withCopyFileToContainer(MountableFile.forClasspathResource("init.sql"), "/docker-entrypoint-initdb.d/init.sql")
            .waitingFor(Wait.forLogMessage(".*ready for connections.*", 2));

    static {
        mysql.start();
    }

    @DynamicPropertySource
    static void r2dbcProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", () -> "r2dbc:mysql://" + mysql.getHost() + ":" + mysql.getMappedPort(3306)
                + "/" + mysql.getDatabaseName());
        registry.add("spring.r2dbc.username", mysql::getUsername);
        registry.add("spring.r2dbc.password", mysql::getPassword);
    }

    @Autowired
    private DatabaseClient databaseClient;

    private static final List<String> TABLES = List.of(
            "client_work_class", "work_class_schedules", "pertrainer_specialty", "trainer_class_specialty",
            "trainer_class_work_class", "Inscription", "clients", "trainers_class", "per_trainer",
            "detail_user", "detail_class_trainer", "detail_per_trainer", "membership", "specialty",
            "work_class", "schedules", "schedulesGym", "promotion", "equipament", "pool"
    );

    // The database now lives for the whole suite (see the singleton-container comment above),
    // so every test class sharing it needs a clean slate of its own instead of relying on
    // whatever a previous class's tests happened to leave behind - especially since most of
    // these fixtures use small hardcoded ids that collide across classes once the schema isn't
    // recreated per class anymore. FK checks are disabled around the truncation since these
    // tables reference each other and TRUNCATE order would otherwise matter.
    @BeforeEach
    void resetDatabase() {
        databaseClient.sql("SET FOREIGN_KEY_CHECKS = 0").fetch().rowsUpdated().block();
        for (String table : TABLES) {
            databaseClient.sql("TRUNCATE TABLE " + table).fetch().rowsUpdated().block();
        }
        databaseClient.sql("SET FOREIGN_KEY_CHECKS = 1").fetch().rowsUpdated().block();
    }
}
