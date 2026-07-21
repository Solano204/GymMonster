package com.monster.web_page;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

// Without this, RedissonClient (connects eagerly at context startup, unlike a lazy Kafka
// producer factory) fails with "Unable to connect to Redis server: localhost/127.0.0.1:6379" -
// mirrors the same Testcontainers Redis setup already used in SecurityConfigIntegrationTest.
@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
class WebPageApplicationTests {

	@Container
	static GenericContainer<?> redisContainer = new GenericContainer<>(DockerImageName.parse("redis:6.0.9"))
			.withExposedPorts(6379)
			.withCommand("redis-server", "--requirepass", "testing");

	@DynamicPropertySource
	static void redisProperties(DynamicPropertyRegistry registry) {
		registry.add("svc.redis.host", redisContainer::getHost);
		registry.add("svc.redis.port", () -> redisContainer.getMappedPort(6379));
		registry.add("svc.redis.password", () -> "testing");
	}

	@Test
	void contextLoads() {
	}

}
