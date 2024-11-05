package com.monster.server_information;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ActiveProfiles("test")
class ServerInformationApplicationTests {

	@Autowired
    private Environment env;

	@BeforeAll
    static void setup() {
        System.setProperty("TEST_ENV", "test");
    }

	@Test
    void verifyTestProfileIsActive() {
		assertTrue(env.acceptsProfiles(Profiles.of("test")), "Expected 'test' profile to be active");
    }

}
