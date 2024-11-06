package com.monster.server_informations.UsingTestContainer;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import infraestrucutre.Adapters.Drivens.DTOS.DtoKeyCloakUser;
import infraestrucutre.Adapters.Drivens.ImpServices.KeycloakServiceImpl;
import reactor.test.StepVerifier;

@SpringBootTest
@ActiveProfiles("test")
public class KeycloakServiceImplIntegrationTest  extends BaseKeycloakTest {
    
    @Autowired
    private KeycloakServiceImpl keycloakService;

    private static final String TEST_USERNAME = "test_user";
    private static final String TEST_PASSWORD = "securepassword";
    private static final String TEST_EMAIL = "test_user@example.com";
    
    @Test
    public void testCreateUser() {
        DtoKeyCloakUser dtoUser = new DtoKeyCloakUser();
        dtoUser.setUsername(TEST_USERNAME);
        dtoUser.setFirstName("Test");
        dtoUser.setLastName("User");
        dtoUser.setEmail(TEST_EMAIL);
        dtoUser.setPassword(TEST_PASSWORD);

        StepVerifier.create(keycloakService.createUser(dtoUser))
            .expectNextMatches(user -> user.getUsername().equals(TEST_USERNAME))
            .verifyComplete();


    }

    @Test
    public  void testFindAllUsers() {
        StepVerifier.create(keycloakService.findAllUsers())
            .expectNextCount(1) // Adjust based on your data in Keycloak
            .verifyComplete();
    }

    @Test
    public void testSearchUserByUsername() {
        StepVerifier.create(keycloakService.searchUserByUsername(TEST_USERNAME))
            .expectNextMatches(user -> user.getUsername().equals(TEST_USERNAME))
            .verifyComplete();
    }

    @Test
    public void testChangePassword() {
        StepVerifier.create(keycloakService.changePassword(TEST_USERNAME, "newSecurePassword"))
            .expectNext("Password changed successfully!")
            .verifyComplete();
    }

    @Test
    public void testDeleteUser() {
        StepVerifier.create(keycloakService.deleteUser(TEST_USERNAME))
            .expectNext("The user was deleted successfully!")
            .verifyComplete();
    }
}