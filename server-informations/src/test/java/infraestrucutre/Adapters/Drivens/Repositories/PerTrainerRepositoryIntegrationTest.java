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
class PerTrainerRepositoryIntegrationTest extends R2dbcContainerTest {

    @Autowired
    private PerTrainerRepository perTrainerRepository;

    @Autowired
    private DatabaseClient db;

    private Mono<Long> insertDetail(String name) {
        return db.sql("INSERT INTO detail_per_trainer (name, second_name, last_name_m, last_name_p, age, weight, height) "
                        + "VALUES (:name, 'S', 'M', 'P', '30', '70', '175')")
                .bind("name", name).fetch().rowsUpdated()
                .then(db.sql("SELECT id FROM detail_per_trainer WHERE name = :name")
                        .bind("name", name).map(row -> row.get("id", Long.class)).one());
    }

    private Mono<Long> insertTrainer(String username, Long detailId) {
        return db.sql("INSERT INTO per_trainer (username, password, email, id_detail, start_date) "
                        + "VALUES (:username, 'pw', :email, :detailId, '2026-01-01')")
                .bind("username", username)
                .bind("email", username + "@test.com")
                .bind("detailId", detailId)
                .fetch().rowsUpdated()
                .then(db.sql("SELECT id FROM per_trainer WHERE username = :username")
                        .bind("username", username).map(row -> row.get("id", Long.class)).one());
    }

    private Mono<Long> seededTrainer(String username) {
        return insertDetail("Detail-" + username).flatMap(detailId -> insertTrainer(username, detailId));
    }

    private Mono<Long> insertSpecialty(String name) {
        return db.sql("INSERT INTO specialty (name, description) VALUES (:name, 'desc')")
                .bind("name", name).fetch().rowsUpdated()
                .then(db.sql("SELECT id FROM specialty WHERE name = :name")
                        .bind("name", name).map(row -> row.get("id", Long.class)).one());
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
        seededTrainer("pt1").block();

        StepVerifier.create(perTrainerRepository.findByUsername("pt1"))
                .assertNext(trainer -> assertThat(trainer.getEmail()).isEqualTo("pt1@test.com"))
                .verifyComplete();
    }

    @Test
    void findAllSpecialtyByTrainerId_joinsThroughPertrainerSpecialtyLinkTable() {
        Long trainerId = seededTrainer("pt2").block();
        Long specialtyId = insertSpecialty("Yoga-PT").block();
        db.sql("INSERT INTO pertrainer_specialty (pertrainer_id, specialty_id) VALUES (:t, :s)")
                .bind("t", trainerId).bind("s", specialtyId).fetch().rowsUpdated().block();

        StepVerifier.create(perTrainerRepository.findAllSpecialtyByTrainerId(trainerId))
                .assertNext(specialty -> assertThat(specialty.getName()).isEqualTo("Yoga-PT"))
                .verifyComplete();
    }

    @Test
    void findClientsByTrainerId_returnsDetailUserRowsForThatTrainersClients() {
        Long trainerId = seededTrainer("pt3").block();
        insertClientFor("pt3", trainerId).block();

        StepVerifier.create(perTrainerRepository.findClientsByTrainerId(trainerId, 10, 0))
                .assertNext(detail -> assertThat(detail.getName()).isEqualTo("ClientOf-pt3"))
                .verifyComplete();
    }

    @Test
    void findAllTrainersAllInformation_countsAssignedClientsPerTrainer() {
        Long trainerId = seededTrainer("pt4").block();
        insertClientFor("pt4", trainerId).block();

        StepVerifier.create(perTrainerRepository.findAllTrainersAllInformation(10, 0))
                .recordWith(java.util.ArrayList::new)
                .thenConsumeWhile(x -> true)
                .consumeRecordedWith(results -> assertThat(results)
                        .anySatisfy(info -> {
                            if (info.mamalon().equals("pt4")) {
                                assertThat(info.clients()).isEqualTo(1);
                            }
                        }))
                .verifyComplete();
    }

    @Test
    void findAllInfoTrainer_returnsFlattenedTrainerDetails() {
        Long trainerId = seededTrainer("pt5").block();

        StepVerifier.create(perTrainerRepository.findAllInfoTrainer(trainerId))
                .assertNext(info -> assertThat(info.mamalon()).isEqualTo("pt5"))
                .verifyComplete();
    }

    @Test
    void existsSpecialtyForTrainer_returnsOne_whenAssociationExists() {
        Long trainerId = seededTrainer("pt6").block();
        Long specialtyId = insertSpecialty("Boxing-PT").block();
        db.sql("INSERT INTO pertrainer_specialty (pertrainer_id, specialty_id) VALUES (:t, :s)")
                .bind("t", trainerId).bind("s", specialtyId).fetch().rowsUpdated().block();

        StepVerifier.create(perTrainerRepository.existsSpecialtyForTrainer(trainerId, "Boxing-PT"))
                .expectNext(1)
                .verifyComplete();
    }

    @Test
    void addSpecialtyToTrainer_thenRemoveSpecialtyFromTrainer_roundTrips() {
        Long trainerId = seededTrainer("pt7").block();
        Long specialtyId = insertSpecialty("Crossfit-PT").block();

        StepVerifier.create(perTrainerRepository.addSpecialtyToTrainer(trainerId, specialtyId)).verifyComplete();
        StepVerifier.create(perTrainerRepository.existsSpecialtyForTrainer(trainerId, "Crossfit-PT"))
                .expectNext(1)
                .verifyComplete();

        StepVerifier.create(perTrainerRepository.removeSpecialtyFromTrainer(trainerId, specialtyId)).verifyComplete();
        StepVerifier.create(perTrainerRepository.existsSpecialtyForTrainer(trainerId, "Crossfit-PT"))
                .expectNext(0)
                .verifyComplete();
    }

    @Test
    void deleteByUsername_removesTheRow() {
        seededTrainer("pt8").block();

        StepVerifier.create(perTrainerRepository.deleteByUsername("pt8")).verifyComplete();
        StepVerifier.create(perTrainerRepository.existsByUsername("pt8")).expectNext(false).verifyComplete();
    }
}
