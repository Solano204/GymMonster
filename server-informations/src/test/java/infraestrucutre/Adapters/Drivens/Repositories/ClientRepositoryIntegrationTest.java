package infraestrucutre.Adapters.Drivens.Repositories;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.r2dbc.core.DatabaseClient;

import com.monster.server_informations.R2dbcContainerTest;

import infraestrucutre.Adapters.Drivens.Entities.Client;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * Self-contained replacement/extension of ClientRepositoryTest (which needs
 * docker-compose-test-inf.yaml manually started and pre-existing seeded rows - see
 * "Steps to Test with Server-Configuration.txt"). Seeds every row itself via DatabaseClient so
 * it runs standalone, and specifically exercises the two hand-written @Query joins
 * (findClientDetailMembershipByClientId, findWorkClassesByClientId, findAllClientsAD) since those
 * are the highest-risk spots (column/alias typos fail silently as nulls, not compile errors).
 */
@DataR2dbcTest
class ClientRepositoryIntegrationTest extends R2dbcContainerTest {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private DatabaseClient db;

    private Mono<Long> insertMembership(String type) {
        return db.sql("INSERT INTO membership (membership_type, description, has_cardio, has_pool, has_food_court) "
                        + "VALUES (:type, 'desc', true, true, true)")
                .bind("type", type)
                .fetch().rowsUpdated()
                .then(db.sql("SELECT id FROM membership WHERE membership_type = :type")
                        .bind("type", type)
                        .map(row -> row.get("id", Long.class)).one());
    }

    private Mono<Long> insertDetailPerTrainer(String name) {
        return db.sql("INSERT INTO detail_per_trainer (name, second_name, last_name_m, last_name_p, age, weight, height) "
                        + "VALUES (:name, 'S', 'M', 'P', '30', '70', '175')")
                .bind("name", name)
                .fetch().rowsUpdated()
                .then(db.sql("SELECT id FROM detail_per_trainer WHERE name = :name")
                        .bind("name", name)
                        .map(row -> row.get("id", Long.class)).one());
    }

    private Mono<Long> insertTrainer(String username, Long detailId) {
        return db.sql("INSERT INTO per_trainer (username, password, email, id_detail, start_date) "
                        + "VALUES (:username, 'pw', :email, :detailId, '2026-01-01')")
                .bind("username", username)
                .bind("email", username + "@test.com")
                .bind("detailId", detailId)
                .fetch().rowsUpdated()
                .then(db.sql("SELECT id FROM per_trainer WHERE username = :username")
                        .bind("username", username)
                        .map(row -> row.get("id", Long.class)).one());
    }

    private Mono<Long> insertDetailUser(String name) {
        return db.sql("INSERT INTO detail_user (name, second_name, last_name_m, last_name_p, age, weight, height) "
                        + "VALUES (:name, 'Q', 'Doe', 'Public', '25', '60', '165')")
                .bind("name", name)
                .fetch().rowsUpdated()
                .then(db.sql("SELECT id FROM detail_user WHERE name = :name")
                        .bind("name", name)
                        .map(row -> row.get("id", Long.class)).one());
    }

    private Mono<Long> insertClient(String username, Long membershipId, Long trainerId, Long detailId) {
        return db.sql("INSERT INTO clients (username, password, email, id_membership, id_trainer, id_detail) "
                        + "VALUES (:username, 'pw', :email, :membershipId, :trainerId, :detailId)")
                .bind("username", username)
                .bind("email", username + "@test.com")
                .bind("membershipId", membershipId)
                .bind("trainerId", trainerId)
                .bind("detailId", detailId)
                .fetch().rowsUpdated()
                .then(db.sql("SELECT id FROM clients WHERE username = :username")
                        .bind("username", username)
                        .map(row -> row.get("id", Long.class)).one());
    }

    private Mono<Long> fullySeededClient(String username) {
        return insertMembership("GOLD-" + username)
                .flatMap(membershipId -> insertDetailPerTrainer("Coach-" + username)
                        .flatMap(trainerDetailId -> insertTrainer("coach-" + username, trainerDetailId))
                        .flatMap(trainerId -> insertDetailUser("Client-" + username)
                                .flatMap(detailId -> insertClient(username, membershipId, trainerId, detailId))));
    }

    @Test
    void findClientDetailMembershipByClientId_joinsAcrossMembershipDetailAndInscription() {
        Long clientId = fullySeededClient("jdoe1").block();
        db.sql("INSERT INTO Inscription (id_client, date_inscription, start_month, end_month, price) "
                        + "VALUES (:clientId, '2026-01-01', '2026-01-01', '2027-01-01', 49.99)")
                .bind("clientId", clientId)
                .fetch().rowsUpdated().block();

        StepVerifier.create(clientRepository.findClientDetailMembershipByClientId(clientId))
                .assertNext(allClient -> {
                    assertThat(allClient.username()).isEqualTo("jdoe1");
                    assertThat(allClient.name()).isEqualTo("Client-jdoe1");
                    assertThat(allClient.membershiptype()).isEqualTo("GOLD-jdoe1");
                    assertThat(allClient.trainername()).isEqualTo("coach-jdoe1");
                    assertThat(allClient.price()).isEqualTo(49.99);
                })
                .verifyComplete();
    }

    @Test
    void findWorkClassesByClientId_joinsThroughClientWorkClassLinkTable() {
        Long clientId = fullySeededClient("jdoe2").block();
        Long workClassId = db.sql("INSERT INTO work_class (name, description, duration) VALUES ('Yoga', 'desc', '60min')")
                .fetch().rowsUpdated()
                .then(db.sql("SELECT id FROM work_class WHERE name = 'Yoga'").map(row -> row.get("id", Long.class)).one())
                .block();
        db.sql("INSERT INTO client_work_class (client_id, work_class_id) VALUES (:clientId, :workClassId)")
                .bind("clientId", clientId)
                .bind("workClassId", workClassId)
                .fetch().rowsUpdated().block();

        StepVerifier.create(clientRepository.findWorkClassesByClientId(clientId))
                .assertNext(workClass -> assertThat(workClass.getName()).isEqualTo("Yoga"))
                .verifyComplete();
    }

    @Test
    void findByUsername_returnsTheMatchingClient() {
        fullySeededClient("jdoe3").block();

        StepVerifier.create(clientRepository.findByUsername("jdoe3"))
                .assertNext(client -> assertThat(client.getEmail()).isEqualTo("jdoe3@test.com"))
                .verifyComplete();
    }

    @Test
    void findByUsername_returnsEmpty_whenNoMatch() {
        StepVerifier.create(clientRepository.findByUsername("ghost")).verifyComplete();
    }

    @Test
    void existsByUsername_reflectsWhetherTheRowExists() {
        fullySeededClient("jdoe4").block();

        StepVerifier.create(clientRepository.existsByUsername("jdoe4")).expectNext(true).verifyComplete();
        StepVerifier.create(clientRepository.existsByUsername("ghost")).expectNext(false).verifyComplete();
    }

    @Test
    void existsByEmail_reflectsWhetherTheRowExists() {
        fullySeededClient("jdoe5").block();

        StepVerifier.create(clientRepository.existsByEmail("jdoe5@test.com")).expectNext(true).verifyComplete();
    }

    @Test
    void deleteByUsername_removesTheRow() {
        fullySeededClient("jdoe6").block();

        StepVerifier.create(clientRepository.deleteByUsername("jdoe6")).verifyComplete();
        StepVerifier.create(clientRepository.existsByUsername("jdoe6")).expectNext(false).verifyComplete();
    }

    @Test
    void findAllClientsAD_respectsLimitAndOffset() {
        fullySeededClient("page1").flatMap(id ->
                db.sql("INSERT INTO Inscription (id_client, date_inscription, start_month, end_month, price) "
                                + "VALUES (:id, '2026-01-01', '2026-01-01', '2027-01-01', 10)")
                        .bind("id", id).fetch().rowsUpdated()).block();
        fullySeededClient("page2").flatMap(id ->
                db.sql("INSERT INTO Inscription (id_client, date_inscription, start_month, end_month, price) "
                                + "VALUES (:id, '2026-01-01', '2026-01-01', '2027-01-01', 10)")
                        .bind("id", id).fetch().rowsUpdated()).block();

        StepVerifier.create(clientRepository.findAllClientsAD(1, 0))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void save_persistsANewClient() {
        Long membershipId = insertMembership("SILVER-save").block();
        Long trainerDetailId = insertDetailPerTrainer("Coach-save").block();
        Long trainerId = insertTrainer("coach-save", trainerDetailId).block();
        Long detailId = insertDetailUser("Client-save").block();

        Client client = new Client();
        client.setUsername("newuser-save");
        client.setPassword("pw");
        client.setEmail("newuser-save@test.com");
        client.setId_membership(membershipId);
        client.setId_trainer(trainerId);
        client.setId_detail(detailId);

        StepVerifier.create(clientRepository.save(client))
                .assertNext(saved -> assertThat(saved.getId()).isNotNull())
                .verifyComplete();
    }
}
