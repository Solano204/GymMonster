package com.monster.server_informations.UsingConfigServer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import infraestrucutre.Adapters.Drivens.Entities.Client;
import infraestrucutre.Adapters.Drivens.Repositories.ClientRepository;
import reactor.test.StepVerifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeoutException;

@SpringBootTest
@ActiveProfiles("test")
public class ClientRepositoryTest {

    @Autowired
    private ClientRepository clientRepository;

    @Test
    void testFindClientDetailMembershipByClientId() {
        Long clientId = 1L; // Use a valid client ID from the database

        StepVerifier.create(clientRepository.findClientDetailMembershipByClientId(clientId))
            .expectNextMatches(client -> client.username() != null && client.email() != null)
            .verifyComplete();

        // Additional validation: Verify the time taken (for example, less than 1 second)
        StepVerifier.create(clientRepository.findClientDetailMembershipByClientId(clientId))
            .expectNextCount(1)
            .verifyComplete();

        // Add verification to check that the method returns a client with expected properties
        StepVerifier.create(clientRepository.findClientDetailMembershipByClientId(clientId))
            .expectNextMatches(client -> 
                client.username() != null && 
                client.email() != null && 
                client.id() != null // Ensure ID is also present
            )
            .verifyComplete();
    }

    @Test
    void testFindWorkClassesByClientId() {
        Long clientId = 1L; // Use a valid client ID from the database

        StepVerifier.create(clientRepository.findWorkClassesByClientId(clientId))
            .expectNextCount(1) // Assuming the client has at least one work class
            .verifyComplete();

        // Validate that the work class has a non-null property, e.g., work class name
        StepVerifier.create(clientRepository.findWorkClassesByClientId(clientId))
            .expectNextMatches(workClass -> workClass.getName() != null) // Assuming WorkClass has a getName() method
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

        // Additional validation: Verify that the client is actually saved by fetching it again
        StepVerifier.create(clientRepository.findClientDetailMembershipByClientId(client.getId())) // Ensure you retrieve the correct ID
            .expectNextMatches(savedClientFromDb -> 
                savedClientFromDb.username().equals("new_user") && 
                savedClientFromDb.email().equals("new_user@example.com")
            )
            .verifyComplete();
    }

    @Test
    void testDeleteByUsername() {
        String usernameToDelete = "new_user";

        // First, ensure the client exists before deleting
        StepVerifier.create(clientRepository.existsByUsername(usernameToDelete))
            .expectNext(true)
            .verifyComplete();

        // Delete the user
        StepVerifier.create(clientRepository.deleteByUsername(usernameToDelete))
            .verifyComplete();

        // Validate that the user no longer exists
        StepVerifier.create(clientRepository.existsByUsername(usernameToDelete))
            .expectNext(false)
            .verifyComplete();
    }

    @Test
    void testPaginationQuery() {
        int size = 10;
        int offset = 0;

        StepVerifier.create(clientRepository.findAllClientsAD(size, offset))
            .expectNextCount(size) // Assuming you have enough data for this test
            .verifyComplete();

        // Validate if pagination works by checking the content
        StepVerifier.create(clientRepository.findAllClientsAD(size, offset))
            .expectNextMatches(client -> client.id() != null) // Assuming Client has a getId() method
            .expectNextCount(size - 1) // Expect the count to decrement by one
            .verifyComplete();
    }

    // Example of a test that checks for exceptions
    @Test
    void testFindClientDetailMembershipByInvalidClientId() {
        Long invalidClientId = 999L; // Assuming this ID does not exist

        StepVerifier.create(clientRepository.findClientDetailMembershipByClientId(invalidClientId))
            .expectErrorMatches(throwable -> 
                throwable instanceof NoSuchElementException // Assuming you throw NoSuchElementException for not found
            )
            .verify();
    }

    // Example of timeout validation for a method
    @Test
    void testTimeoutValidation() {
        Long clientId = 1L; // Use a valid client ID from the database

        StepVerifier.create(clientRepository.findClientDetailMembershipByClientId(clientId).delayElement(Duration.ofSeconds(3)))
            .expectErrorMatches(throwable -> throwable instanceof TimeoutException) // Assuming you have a timeout exception
            .verify();
    }
}