package com.monster.server_informations.UsingConfigServer;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;

import infraestrucutre.Adapters.Drivens.DTOS.DtoKeyCloakUser;
import infraestrucutre.Adapters.Drivens.ImpServices.KeycloakServiceImpl;
import reactor.test.StepVerifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.util.Set;

@SpringBootTest
@ActiveProfiles("test")
public class KeycloakServiceImplIntegrationTest {

    @Autowired
    private KeycloakServiceImpl keycloakService;

    private static final String TEST_USERNAME = "test_user";
    private static final String TEST_PASSWORD = "securepassword";
    private static final String TEST_EMAIL = "test_user@example.com";

    @Test
    void testCreateUser() {
        DtoKeyCloakUser dtoUser = new DtoKeyCloakUser();
        dtoUser.setUsername(TEST_USERNAME);
        dtoUser.setFirstName("Test");
        dtoUser.setLastName("User");
        dtoUser.setEmail(TEST_EMAIL);
        dtoUser.setRoles(Set.of("Client"));
        dtoUser.setPassword(TEST_PASSWORD);

        StepVerifier.create(keycloakService.createUser(dtoUser))
            .expectNextMatches(user -> user.getUsername().equals(TEST_USERNAME) && user.getEmail().equals(TEST_EMAIL))
            .verifyComplete();
    }

    @Test
    void testFindAllUsers() {
        StepVerifier.create(keycloakService.findAllUsers())
            .expectNextCount(1) // Adjust based on your expected data in Keycloak
            .expectNextMatches(user -> user.getUsername().equals(TEST_USERNAME))
            .verifyComplete();
    }

    @Test
    void testSearchUserByUsername() {
        StepVerifier.create(keycloakService.searchUserByUsername(TEST_USERNAME))
            .expectNextMatches(user -> user.getUsername().equals(TEST_USERNAME))
            .verifyComplete();
    }

    @Test
    void testChangePassword() {
        String newPassword = "newSecurePassword";

        // First, change the password
        StepVerifier.create(keycloakService.changePassword(TEST_USERNAME, newPassword))
            .expectNext("Password changed successfully!")
            .verifyComplete();
    }

    @Test
    void testDeleteUser() {
        // First, ensure the user exists before deletion
        StepVerifier.create(keycloakService.searchUserByUsername(TEST_USERNAME))
            .expectNextMatches(user -> user.getUsername().equals(TEST_USERNAME))
            .verifyComplete();

        // Now, delete the user
        StepVerifier.create(keycloakService.deleteUser(TEST_USERNAME))
            .expectNext("The user was deleted successfully!")
            .verifyComplete();

        // Validate that the user no longer exists
        StepVerifier.create(keycloakService.searchUserByUsername(TEST_USERNAME))
            .expectErrorMatches(throwable -> 
                throwable instanceof UsernameNotFoundException // Adjust to your actual exception class
            )
            .verify();
    }
}
