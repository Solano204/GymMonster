package com.monster.gateway;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
public abstract class FATHER {

    static GenericContainer<?> redisContainer = new GenericContainer<>(DockerImageName.parse("redis:6.0.9"))
        .withExposedPorts(6379)
        .withEnv("REDIS_PASSWORD", "testing")  
        .withCommand("redis-server", "--requirepass", "testing");
    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) throws InterruptedException {
        redisContainer.start();
        registry.add("svc.redis.host", redisContainer::getHost);
        registry.add("svc.redis.port", () -> redisContainer.getMappedPort(6379));
        registry.add("svc.redis.password", () -> "testing");
        registry.add("svc.keycloak.url", () -> "http://localhost:8080");
        registry.add("svc.keycloak.docker-real", () -> "master");
        registry.add("svc.keycloak.client-id", () -> "test-client");
        registry.add("svc.keycloak.client-secret", () -> "test-secret");
    }
}
