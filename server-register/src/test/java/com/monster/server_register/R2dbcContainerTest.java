package com.monster.server_register;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.MountableFile;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Self-contained MySQL Testcontainers base for the R2DBC repository layer - mirrors
 * server-informations' R2dbcContainerTest (same portfolio, same schema). No external
 * docker-compose setup needed, unlike RegisterServiceKafkaListenerIntegrationTest's
 * repository @MockBean approach - this exercises the real hand-written SQL.
 *
 * Testcontainers' "singleton container" pattern, NOT @Testcontainers/@Container: the static
 * `mysql` field below is inherited (not redeclared) by every repository test subclass, so it's
 * the same field/object for all of them. @Testcontainers manages container lifecycle per test
 * class (start in beforeAll, stop in afterAll) - since it's the same shared instance, the first
 * subclass's afterAll stops the ONE container, and every subsequent subclass then tries to
 * reconnect through a port mapping that no longer points at anything running ("Connection
 * refused"). Starting it once here, with no JUnit-managed stop, avoids that - Testcontainers'
 * own Ryuk reaper removes it when the JVM exits.
 */
// @DataR2dbcTest's default @SpringBootConfiguration auto-detection scans upward from each
// subclass's OWN package (infraestrucutre.Adapters.Drivens.Repositories), which never reaches
// ServerRegisterApplication's package (com.monster.server_register) - declaring it here on the
// shared base fixes every subclass at once instead of annotating each one individually.
@ContextConfiguration(classes = ServerRegisterApplication.class)
public abstract class R2dbcContainerTest {

    // MySQL's entrypoint briefly opens the port for an internal init server before the real
    // server starts; the default wait strategy can latch onto that first port-open and hand
    // control back before the server is actually ready, causing flaky connection-refused errors.
    static final MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("mscv_register")
            .withUsername("test")
            .withPassword("test")
            .withCopyFileToContainer(MountableFile.forClasspathResource("init.sql"), "/docker-entrypoint-initdb.d/init.sql")
            .waitingFor(Wait.forLogMessage(".*ready for connections.*", 2));

    static {
        mysql.start();
        awaitSchemaReady();
    }

    // The "ready for connections" log-message wait above only proves the server accepts
    // connections, not that docker-entrypoint-initdb.d/init.sql has actually finished running
    // against it - the two normally happen in the right order, but on a slower/more contended
    // CI runner the schema can still be mid-creation for a moment after the port opens, so
    // truncating "membership" (or any of TABLES) here occasionally hits "table doesn't exist yet"
    // (observed as intermittent CI-only failures that never reproduced locally). Polling for the
    // last table init.sql creates via a real synchronous JDBC query - not R2DBC, this runs once
    // in a static initializer before Spring's reactive machinery exists - removes the guesswork.
    private static void awaitSchemaReady() {
        long deadline = System.currentTimeMillis() + 30_000;
        SQLException lastError = null;
        while (System.currentTimeMillis() < deadline) {
            try (Connection conn = DriverManager.getConnection(mysql.getJdbcUrl(), mysql.getUsername(), mysql.getPassword())) {
                try (ResultSet rs = conn.getMetaData().getTables(null, null, "Inscription", null)) {
                    if (rs.next()) {
                        return;
                    }
                }
            } catch (SQLException e) {
                lastError = e;
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Interrupted while waiting for init.sql to finish", e);
            }
        }
        throw new IllegalStateException("init.sql did not finish creating the schema within 30s", lastError);
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
            "Inscription", "clients", "per_trainer", "detail_user", "detail_per_trainer", "membership"
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
