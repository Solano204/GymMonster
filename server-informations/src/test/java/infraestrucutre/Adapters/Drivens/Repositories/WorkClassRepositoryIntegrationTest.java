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
class WorkClassRepositoryIntegrationTest extends R2dbcContainerTest {

    @Autowired
    private WorkClassRepository workClassRepository;

    @Autowired
    private DatabaseClient db;

    private Mono<Long> insertWorkClass(String name, String duration) {
        return db.sql("INSERT INTO work_class (name, description, duration) VALUES (:name, 'desc', :duration)")
                .bind("name", name).bind("duration", duration).fetch().rowsUpdated()
                .then(db.sql("SELECT id FROM work_class WHERE name = :name")
                        .bind("name", name).map(row -> row.get("id", Long.class)).one());
    }

    private Mono<Long> insertClientWithDetail(String username, String detailName) {
        return db.sql("INSERT INTO detail_user (name, second_name, last_name_m, last_name_p, age, weight, height) "
                        + "VALUES (:name, 'Q', 'Doe', 'Public', '25', '60', '165')")
                .bind("name", detailName).fetch().rowsUpdated()
                .then(db.sql("SELECT id FROM detail_user WHERE name = :name")
                        .bind("name", detailName).map(row -> row.get("id", Long.class)).one())
                .flatMap(detailId -> db.sql("INSERT INTO clients (username, password, email, id_detail) "
                                + "VALUES (:username, 'pw', :email, :detailId)")
                        .bind("username", username)
                        .bind("email", username + "@test.com")
                        .bind("detailId", detailId)
                        .fetch().rowsUpdated()
                        .then(db.sql("SELECT id FROM clients WHERE username = :username")
                                .bind("username", username).map(row -> row.get("id", Long.class)).one()));
    }

    private Mono<Long> insertTrainerWithDetail(String username, String detailName) {
        return db.sql("INSERT INTO detail_user (name, second_name, last_name_m, last_name_p, age, weight, height) "
                        + "VALUES (:name, 'Q', 'Doe', 'Public', '25', '60', '165')")
                .bind("name", detailName).fetch().rowsUpdated()
                .then(db.sql("SELECT id FROM detail_user WHERE name = :name")
                        .bind("name", detailName).map(row -> row.get("id", Long.class)).one())
                .flatMap(detailUserId -> db.sql("INSERT INTO detail_class_trainer (name, second_name, last_name_m, "
                                + "last_name_p, age, weight, height) VALUES (:name, 'S', 'M', 'P', '30', '70', '175')")
                        .bind("name", "TrainerDetail-" + detailName)
                        .fetch().rowsUpdated()
                        .then(db.sql("SELECT id FROM detail_class_trainer WHERE name = :name")
                                .bind("name", "TrainerDetail-" + detailName).map(row -> row.get("id", Long.class)).one())
                        .flatMap(trainerDetailId -> db.sql("INSERT INTO trainers_class (username, password, email, "
                                        + "id_detail, start_date) VALUES (:username, 'pw', :email, :detailId, '2026-01-01')")
                                .bind("username", username)
                                .bind("email", username + "@test.com")
                                .bind("detailId", trainerDetailId)
                                .fetch().rowsUpdated()
                                .then(db.sql("SELECT id FROM trainers_class WHERE username = :username")
                                        .bind("username", username).map(row -> row.get("id", Long.class)).one())));
    }

    @Test
    void findByName_returnsTheMatchingWorkClass() {
        insertWorkClass("Yoga-WC", "60min").block();

        StepVerifier.create(workClassRepository.findByName("Yoga-WC"))
                .assertNext(wc -> assertThat(wc.getDuration()).isEqualTo("60min"))
                .verifyComplete();
    }

    @Test
    void findByDurationGreaterThan_usesLexicographicStringComparison() {
        // duration is a VARCHAR column, not numeric - this pins the (surprising) actual
        // lexicographic comparison behavior rather than assuming numeric ordering.
        insertWorkClass("Short-WC", "30min").block();
        insertWorkClass("Long-WC", "90min").block();

        StepVerifier.create(workClassRepository.findByDurationGreaterThan("30min"))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void findAllClientsByWorkClass_joinsClientWorkClassAndDetailUser() {
        Long workClassId = insertWorkClass("Spin-WC", "45min").block();
        Long clientId = insertClientWithDetail("wcclient1", "WcClientDetail1").block();
        db.sql("INSERT INTO client_work_class (client_id, work_class_id) VALUES (:c, :w)")
                .bind("c", clientId).bind("w", workClassId).fetch().rowsUpdated().block();

        StepVerifier.create(workClassRepository.findAllClientsByWorkClass(workClassId, 10, 0))
                .assertNext(detail -> assertThat(detail.getName()).isEqualTo("WcClientDetail1"))
                .verifyComplete();
    }

    @Test
    void findAllTrainersByWorkClass_joinsTrainerClassWorkClassAndDetailUser() {
        Long workClassId = insertWorkClass("Boxing-WC", "45min").block();
        Long trainerId = insertTrainerWithDetail("wctrainer1", "WcTrainerDetail1").block();
        db.sql("INSERT INTO trainer_class_work_class (trainer_class_id, work_class_id) VALUES (:t, :w)")
                .bind("t", trainerId).bind("w", workClassId).fetch().rowsUpdated().block();

        // insertTrainerWithDetail actually stores the trainer's detail row in
        // detail_class_trainer with this prefixed name - the unprefixed name only ever
        // went into the (correctly) unrelated detail_user row.
        StepVerifier.create(workClassRepository.findAllTrainersByWorkClass(workClassId, 10, 0))
                .assertNext(detail -> assertThat(detail.getName()).isEqualTo("TrainerDetail-WcTrainerDetail1"))
                .verifyComplete();
    }
}
