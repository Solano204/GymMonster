package com.monster.server_informations.UsingTestContainer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import infraestrucutre.Adapters.Drivens.Entities.Client;
import infraestrucutre.Adapters.Drivens.Repositories.ClientRepository;
import reactor.test.StepVerifier;

@DataR2dbcTest
@Import({SqlScriptExecutor.class})
public class ClientRepositoryTest  extends BaseSqlTest {


    @Override
    protected void executeSqlScripts() {
        // Optionally override this to skip executing SQL scripts if desired.
        super.executeSqlScripts(); // Call this if you want to execute the scripts.
    }
    

    @Autowired
    private ClientRepository clientRepository;

    @Test
    void testFindClientDetailMembershipByClientId() {
        Long clientId = 1L; // Ensure this ID exists in the test database

        StepVerifier.create(clientRepository.findClientDetailMembershipByClientId(clientId))
            .expectNextMatches(client -> client.username() != null && client.email() != null)
            .verifyComplete();
    }

    @Test
    void testFindWorkClassesByClientId() {
        Long clientId = 1L; // Ensure this ID exists in the test database

        StepVerifier.create(clientRepository.findWorkClassesByClientId(clientId))
            .expectNextCount(1) // Assuming the client has at least one work class
            .verifyComplete();
    }

    @Test
    void testSaveClient() {
        Client client = new Client();
        client.setUsername("new_user");
        client.setEmail("new_user@example.com");
        client.setPassword("securepassword");

        StepVerifier.create(clientRepository.save(client))
            .expectNextMatches(savedClient -> savedClient.getUsername().equals("new_user"))
            .verifyComplete();
    }

    @Test
    void testDeleteByUsername() {
        String usernameToDelete = "new_user";

        // First, save the client to ensure it exists before deletion
        Client client = new Client();
        client.setUsername(usernameToDelete);
        client.setEmail("new_user@example.com");
        client.setPassword("securepassword");
        
        StepVerifier.create(clientRepository.save(client))
            .expectNextMatches(savedClient -> savedClient.getUsername().equals(usernameToDelete))
            .verifyComplete();

        StepVerifier.create(clientRepository.deleteByUsername(usernameToDelete))
            .verifyComplete();

        StepVerifier.create(clientRepository.existsByUsername(usernameToDelete))
            .expectNext(false) // The username should no longer exist
            .verifyComplete();
    }

    @Test
    void testPaginationQuery() {
        int size = 10;
        int offset = 0;

        StepVerifier.create(clientRepository.findAllClientsAD(size, offset))
            .expectNextCount(size) // Assuming you have enough data for this test
            .verifyComplete();
    }
}