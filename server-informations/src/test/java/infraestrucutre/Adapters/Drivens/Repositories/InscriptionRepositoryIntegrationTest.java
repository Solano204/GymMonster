package infraestrucutre.Adapters.Drivens.Repositories;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.r2dbc.core.DatabaseClient;

import com.monster.server_informations.R2dbcContainerTest;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DataR2dbcTest
class InscriptionRepositoryIntegrationTest extends R2dbcContainerTest {

    @Autowired
    private InscriptionRepository inscriptionRepository;

    @Autowired
    private DatabaseClient db;

    private Mono<Long> insertClient(String username) {
        return db.sql("INSERT INTO clients (username, password, email) VALUES (:username, 'pw', :email)")
                .bind("username", username)
                .bind("email", username + "@test.com")
                .fetch().rowsUpdated()
                .then(db.sql("SELECT id FROM clients WHERE username = :username")
                        .bind("username", username).map(row -> row.get("id", Long.class)).one());
    }

    @Test
    void deleteByClientId_removesOnlyThatClientsInscription() {
        Long clientId = insertClient("insc1").block();
        db.sql("INSERT INTO Inscription (id_client, date_inscription, start_month, end_month, price) "
                        + "VALUES (:id, '2026-01-01', '2026-01-01', '2027-01-01', 20)")
                .bind("id", clientId).fetch().rowsUpdated().block();

        StepVerifier.create(inscriptionRepository.deleteByClientId(clientId)).verifyComplete();

        Long remaining = db.sql("SELECT COUNT(*) AS c FROM Inscription WHERE id_client = :id")
                .bind("id", clientId).map(row -> row.get("c", Long.class)).one().block();
        assertThat(remaining).isZero();
    }
}
