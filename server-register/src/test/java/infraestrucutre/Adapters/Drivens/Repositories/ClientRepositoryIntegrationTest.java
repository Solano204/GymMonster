package infraestrucutre.Adapters.Drivens.Repositories;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.r2dbc.core.DatabaseClient;

import com.monster.server_register.R2dbcContainerTest;

import infraestrucutre.Adapters.Drivens.Entities.Client;
import reactor.test.StepVerifier;

@DataR2dbcTest
class ClientRepositoryIntegrationTest extends R2dbcContainerTest {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private DatabaseClient db;

    private void insertClient(String username) {
        db.sql("INSERT INTO clients (username, password, email) VALUES (:username, 'pw', :email)")
                .bind("username", username)
                .bind("email", username + "@test.com")
                .fetch().rowsUpdated().block();
    }

    @Test
    void findByUsername_returnsTheMatchingClient() {
        insertClient("regclient1");

        StepVerifier.create(clientRepository.findByUsername("regclient1"))
                .assertNext(client -> assertThat(client.getEmail()).isEqualTo("regclient1@test.com"))
                .verifyComplete();
    }

    @Test
    void findByUsername_returnsEmpty_whenNoMatch() {
        StepVerifier.create(clientRepository.findByUsername("ghost")).verifyComplete();
    }

    @Test
    void existsByUsername_reflectsWhetherTheRowExists() {
        insertClient("regclient2");

        StepVerifier.create(clientRepository.existsByUsername("regclient2")).expectNext(true).verifyComplete();
        StepVerifier.create(clientRepository.existsByUsername("ghost")).expectNext(false).verifyComplete();
    }

    @Test
    void existsByEmail_reflectsWhetherTheRowExists() {
        insertClient("regclient3");

        StepVerifier.create(clientRepository.existsByEmail("regclient3@test.com")).expectNext(true).verifyComplete();
    }

    @Test
    void deleteByUsername_removesTheRow() {
        insertClient("regclient4");

        StepVerifier.create(clientRepository.deleteByUsername("regclient4")).verifyComplete();
        StepVerifier.create(clientRepository.existsByUsername("regclient4")).expectNext(false).verifyComplete();
    }

    @Test
    void save_persistsANewClient() {
        Client client = new Client();
        client.setUsername("regclient5");
        client.setPassword("pw");
        client.setEmail("regclient5@test.com");

        StepVerifier.create(clientRepository.save(client))
                .assertNext(saved -> assertThat(saved.getId()).isNotNull())
                .verifyComplete();
    }
}
