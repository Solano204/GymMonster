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
class ClassTrainerRepositoryIntegrationTest extends R2dbcContainerTest {

    @Autowired
    private ClassTrainerRepository classTrainerRepository;

    @Autowired
    private DatabaseClient db;

    private Mono<Long> insertDetail(String name) {
        return db.sql("INSERT INTO detail_class_trainer (name, second_name, last_name_m, last_name_p, age, weight, height) "
                        + "VALUES (:name, 'S', 'M', 'P', '30', '70', '175')")
                .bind("name", name).fetch().rowsUpdated()
                .then(db.sql("SELECT id FROM detail_class_trainer WHERE name = :name")
                        .bind("name", name).map(row -> row.get("id", Long.class)).one());
    }

    private Mono<Long> insertTrainer(String username, Long detailId) {
        return db.sql("INSERT INTO trainers_class (username, password, email, id_detail, start_date) "
                        + "VALUES (:username, 'pw', :email, :detailId, '2026-01-01')")
                .bind("username", username)
                .bind("email", username + "@test.com")
                .bind("detailId", detailId)
                .fetch().rowsUpdated()
                .then(db.sql("SELECT id FROM trainers_class WHERE username = :username")
                        .bind("username", username).map(row -> row.get("id", Long.class)).one());
    }

    private Mono<Long> insertSpecialty(String name) {
        return db.sql("INSERT INTO specialty (name, description) VALUES (:name, 'desc')")
                .bind("name", name).fetch().rowsUpdated()
                .then(db.sql("SELECT id FROM specialty WHERE name = :name")
                        .bind("name", name).map(row -> row.get("id", Long.class)).one());
    }

    private Mono<Long> insertWorkClass(String name) {
        return db.sql("INSERT INTO work_class (name, description, duration) VALUES (:name, 'desc', '60min')")
                .bind("name", name).fetch().rowsUpdated()
                .then(db.sql("SELECT id FROM work_class WHERE name = :name")
                        .bind("name", name).map(row -> row.get("id", Long.class)).one());
    }

    private Mono<Long> seededTrainer(String username) {
        return insertDetail("Detail-" + username).flatMap(detailId -> insertTrainer(username, detailId));
    }

    @Test
    void findByUsername_returnsTheMatchingTrainer() {
        seededTrainer("coach1").block();

        StepVerifier.create(classTrainerRepository.findByUsername("coach1"))
                .assertNext(trainer -> assertThat(trainer.getEmail()).isEqualTo("coach1@test.com"))
                .verifyComplete();
    }

    @Test
    void findAllSpecialty_joinsThroughTrainerClassSpecialtyLinkTable() {
        Long trainerId = seededTrainer("coach2").block();
        Long specialtyId = insertSpecialty("Yoga-CT").block();
        db.sql("INSERT INTO trainer_class_specialty (trainer_class_id, specialty_id) VALUES (:t, :s)")
                .bind("t", trainerId).bind("s", specialtyId).fetch().rowsUpdated().block();

        StepVerifier.create(classTrainerRepository.findAllSpecialty(trainerId))
                .assertNext(specialty -> assertThat(specialty.getName()).isEqualTo("Yoga-CT"))
                .verifyComplete();
    }

    @Test
    void findAllClassByTrainer_joinsThroughTrainerClassWorkClassLinkTable() {
        Long trainerId = seededTrainer("coach3").block();
        Long workClassId = insertWorkClass("Pilates-CT").block();
        db.sql("INSERT INTO trainer_class_work_class (trainer_class_id, work_class_id) VALUES (:t, :w)")
                .bind("t", trainerId).bind("w", workClassId).fetch().rowsUpdated().block();

        StepVerifier.create(classTrainerRepository.findAllClassByTrainer(trainerId))
                .assertNext(workClass -> assertThat(workClass.getName()).isEqualTo("Pilates-CT"))
                .verifyComplete();
    }

    @Test
    void existsSpecialtyForTrainer_returnsTrue_whenAssociationExists() {
        Long trainerId = seededTrainer("coach4").block();
        Long specialtyId = insertSpecialty("Boxing-CT").block();
        db.sql("INSERT INTO trainer_class_specialty (trainer_class_id, specialty_id) VALUES (:t, :s)")
                .bind("t", trainerId).bind("s", specialtyId).fetch().rowsUpdated().block();

        StepVerifier.create(classTrainerRepository.existsSpecialtyForTrainer(trainerId, "Boxing-CT"))
                .expectNext(1)
                .verifyComplete();
    }

    @Test
    void existsSpecialtyForTrainer_returnsZero_whenNoAssociation() {
        Long trainerId = seededTrainer("coach5").block();

        StepVerifier.create(classTrainerRepository.existsSpecialtyForTrainer(trainerId, "NonExistent"))
                .expectNext(0)
                .verifyComplete();
    }

    @Test
    void addSpecialtyToTrainer_thenRemoveSpecialtyFromTrainer_roundTrips() {
        Long trainerId = seededTrainer("coach6").block();
        Long specialtyId = insertSpecialty("Crossfit-CT").block();

        StepVerifier.create(classTrainerRepository.addSpecialtyToTrainer(trainerId, specialtyId)).verifyComplete();
        StepVerifier.create(classTrainerRepository.existsSpecialtyForTrainer(trainerId, "Crossfit-CT"))
                .expectNext(1)
                .verifyComplete();

        StepVerifier.create(classTrainerRepository.removeSpecialtyFromTrainer(trainerId, specialtyId)).verifyComplete();
        StepVerifier.create(classTrainerRepository.existsSpecialtyForTrainer(trainerId, "Crossfit-CT"))
                .expectNext(0)
                .verifyComplete();
    }

    @Test
    void addClassToTrainer_thenRemoveClassFromTrainer_roundTrips() {
        Long trainerId = seededTrainer("coach7").block();
        Long workClassId = insertWorkClass("Spin-CT").block();

        StepVerifier.create(classTrainerRepository.addClassToTrainer(trainerId, workClassId)).verifyComplete();
        StepVerifier.create(classTrainerRepository.existsClassForTrainer(trainerId, "Spin-CT"))
                .expectNext(1)
                .verifyComplete();

        StepVerifier.create(classTrainerRepository.removeClassFromTrainer(trainerId, workClassId)).verifyComplete();
        StepVerifier.create(classTrainerRepository.existsClassForTrainer(trainerId, "Spin-CT"))
                .expectNext(0)
                .verifyComplete();
    }

    @Test
    void findAllTrainers_respectsLimitAndOffset() {
        seededTrainer("pagect1").block();
        seededTrainer("pagect2").block();

        StepVerifier.create(classTrainerRepository.findAllTrainers(1, 0))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void deleteByUsername_removesTheRow() {
        seededTrainer("coach8").block();

        StepVerifier.create(classTrainerRepository.deleteByUsername("coach8")).verifyComplete();
        StepVerifier.create(classTrainerRepository.existsByUsername("coach8")).expectNext(false).verifyComplete();
    }
}
