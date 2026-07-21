package infraestrucutre.Adapters.Drivens.Repositories;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.r2dbc.core.DatabaseClient;

import com.monster.server_register.R2dbcContainerTest;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DataR2dbcTest
class PerTrainerRepositoryIntegrationTest extends R2dbcContainerTest {

    @Autowired
    private PerTrainerRepository perTrainerRepository;

    @Autowired
    private DatabaseClient db;

    private Mono<Long> insertTrainer(String username) {
        return db.sql("INSERT INTO per_trainer (username, password, email) VALUES (:username, 'pw', :email)")
                .bind("username", username)
                .bind("email", username + "@test.com")
                .fetch().rowsUpdated()
                .then(db.sql("SELECT id FROM per_trainer WHERE username = :username")
                        .bind("username", username).map(row -> row.get("id", Long.class)).one());
    }

    private Mono<Long> insertClientFor(String trainerUsername, Long trainerId) {
        return db.sql("INSERT INTO detail_user (name, second_name, last_name_m, last_name_p, age, weight, height) "
                        + "VALUES ('ClientOf-" + trainerUsername + "', 'Q', 'Doe', 'Public', '25', '60', '165')")
                .fetch().rowsUpdated()
                .then(db.sql("SELECT id FROM detail_user WHERE name = 'ClientOf-" + trainerUsername + "'")
                        .map(row -> row.get("id", Long.class)).one())
                .flatMap(detailId -> db.sql("INSERT INTO clients (username, password, email, id_trainer, id_detail) "
                                + "VALUES (:username, 'pw', :email, :trainerId, :detailId)")
                        .bind("username", "cli-" + trainerUsername)
                        .bind("email", "cli-" + trainerUsername + "@test.com")
                        .bind("trainerId", trainerId)
                        .bind("detailId", detailId)
                        .fetch().rowsUpdated());
    }

    @Test
    void findByUsername_returnsTheMatchingTrainer() {
        insertTrainer("regtrainer1").block();

        StepVerifier.create(perTrainerRepository.findByUsername("regtrainer1"))
                .assertNext(trainer -> assertThat(trainer.getEmail()).isEqualTo("regtrainer1@test.com"))
                .verifyComplete();
    }

    @Test
    void deleteByUsername_removesTheRow() {
        insertTrainer("regtrainer2").block();

        StepVerifier.create(perTrainerRepository.deleteByUsername("regtrainer2")).verifyComplete();
        StepVerifier.create(perTrainerRepository.findByUsername("regtrainer2")).verifyComplete();
    }

    @Test
    void findClientsByTrainerId_joinsClientsAndDetailUser() {
        Long trainerId = insertTrainer("regtrainer3").block();
        insertClientFor("regtrainer3", trainerId).block();

        StepVerifier.create(perTrainerRepository.findClientsByTrainerId(trainerId, 10, 0))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void findAllTrainers_respectsLimitAndOffset() {
        insertTrainer("regpage1").block();
        insertTrainer("regpage2").block();

        StepVerifier.create(perTrainerRepository.findAllTrainers(1, 0))
                .expectNextCount(1)
                .verifyComplete();
    }
}
