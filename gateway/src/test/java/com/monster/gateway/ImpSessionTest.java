package com.monster.gateway;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monster.gateway.Config.RedisTokenValidationFilter;
import com.monster.gateway.Entities.Token;
import com.monster.gateway.InterfaceServices.RedisHandlerInterface;
import com.monster.gateway.PropertiesUrl.ServicesUrl;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monster.gateway.ImpServices.ImpRedisSession;
import com.monster.gateway.ImpServices.ImpSession;
import com.monster.gateway.PropertiesUrl.ServicesUrl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.checkerframework.checker.units.qual.t;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.spock.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SpringBootTest
public class ImpSessionTest extends FATHER {


    @Mock
    private ObjectMapper objectMapper;

    @Autowired
    private ImpRedisSession impRedisSession;

    @Autowired
    private ReactiveRedisTemplate<String, String> redisTemplate;

    private final String username = "test-user";
    private final String password = "securepassword";
    private final String accessToken = "new-access-token";
    private final String refreshToken = "new-refresh-token";



    @Test
    void testLogin() {
        Token token = new Token(username, accessToken, refreshToken, 
        Instant.now().plusSeconds(3600), Instant.now().plusSeconds(3600));
        StepVerifier.create(impRedisSession.login(username, token))
                .expectNext("Login Successful")
                .verifyComplete();
    }

    @Test
    void testLogout () {
        testLogin();
        StepVerifier.create(impRedisSession.logout(username))
                .expectNext("Logout Successful")
                .verifyComplete();
    }
}