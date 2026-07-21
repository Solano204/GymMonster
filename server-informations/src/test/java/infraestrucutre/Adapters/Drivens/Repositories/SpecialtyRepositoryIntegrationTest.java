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
class SpecialtyRepositoryIntegrationTest extends R2dbcContainerTest {

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @Autowired
    private DatabaseClient db;

    private Mono<Long> insertSpecialty(String name) {
        return db.sql("INSERT INTO specialty (name, description) VALUES (:name, 'desc')")
                .bind("name", name).fetch().rowsUpdated()
                .then(db.sql("SELECT id FROM specialty WHERE name = :name")
                        .bind("name", name).map(row -> row.get("id", Long.class)).one());
    }

    private Mono<Long> insertTrainer(String username) {
        return db.sql("INSERT INTO detail_per_trainer (name, second_name, last_name_m, last_name_p, age, weight, height) "
                        + "VALUES (:name, 'S', 'M', 'P', '30', '70', '175')")
                .bind("name", "Detail-" + username).fetch().rowsUpdated()
                .then(db.sql("SELECT id FROM detail_per_trainer WHERE name = :name")
                        .bind("name", "Detail-" + username).map(row -> row.get("id", Long.class)).one())
                .flatMap(detailId -> db.sql("INSERT INTO per_trainer (username, password, email, id_detail, start_date) "
                                + "VALUES (:username, 'pw', :email, :detailId, '2026-01-01')")
                        .bind("username", username)
                        .bind("email", username + "@test.com")
                        .bind("detailId", detailId)
                        .fetch().rowsUpdated()
                        .then(db.sql("SELECT id FROM per_trainer WHERE username = :username")
                                .bind("username", username).map(row -> row.get("id", Long.class)).one()));
    }

    @Test
    void findByName_returnsTheMatchingSpecialty() {
        insertSpecialty("Yoga-S").block();

        StepVerifier.create(specialtyRepository.findByName("Yoga-S"))
                .assertNext(s -> assertThat(s.getDescription()).isEqualTo("desc"))
                .verifyComplete();
    }

    @Test
    void deleteByName_removesTheRow() {
        insertSpecialty("Temp-S").block();

        StepVerifier.create(specialtyRepository.deleteByName("Temp-S")).verifyComplete();
        StepVerifier.create(specialtyRepository.findByName("Temp-S")).verifyComplete();
    }

    @Test
    void findAllByTrainerId_joinsThroughPertrainerSpecialtyLinkTable() {
        Long trainerId = insertTrainer("specialtytrainer1").block();
        Long specialtyId = insertSpecialty("Boxing-S").block();
        db.sql("INSERT INTO pertrainer_specialty (pertrainer_id, specialty_id) VALUES (:t, :s)")
                .bind("t", trainerId).bind("s", specialtyId).fetch().rowsUpdated().block();

        StepVerifier.create(specialtyRepository.findAllByTrainerId(trainerId))
                .assertNext(s -> assertThat(s.getName()).isEqualTo("Boxing-S"))
                .verifyComplete();
    }
}
