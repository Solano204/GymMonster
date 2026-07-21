package com.monster.server_informations;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

// Without the "test" profile, application.properties' email.sender/email.password placeholders
// (${EMAIL_SENDER}/${EMAIL_PASSWORD}, no default) fail to resolve and the context never loads.
// Extends R2dbcContainerTest so the "test" profile's hardcoded r2dbc.url (localhost:3308, which
// expects docker-compose-test-inf.yaml manually started) gets overridden by a real Testcontainers
// MySQL instead - DynamicPropertySource values take priority over profile properties.
@SpringBootTest
@ActiveProfiles("test")
class ServerInformationsApplicationTests extends R2dbcContainerTest {

	@Test
	void contextLoads() {
	}

}
