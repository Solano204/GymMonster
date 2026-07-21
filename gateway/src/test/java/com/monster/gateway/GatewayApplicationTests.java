package com.monster.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Full context-load smoke test - mirrors the *ApplicationTests pattern used by the other
 * 4 services. Gateway previously had no such test, so a bean-wiring break (e.g. a missing
 * @Bean dependency among coder/RedisConfig/SecurityConfig/WebClientConfig) could only ever
 * surface indirectly through an unrelated test failing to load its context.
 */
@ActiveProfiles("test")
@SpringBootTest
class GatewayApplicationTests extends FATHER {

    @Test
    void contextLoads() {
    }

}
