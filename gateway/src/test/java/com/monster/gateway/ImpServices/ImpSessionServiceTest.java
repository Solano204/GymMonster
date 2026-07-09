package com.monster.gateway.ImpServices;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monster.gateway.Entities.Token;
import com.monster.gateway.InterfaceServices.RedisHandlerInterface;
import com.monster.gateway.PropertiesUrl.ServicesUrl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * ImpSession has no coverage today. The gateway's TokenAndRefreshTest covers
 * RedisTokenValidationFilter's per-request token refresh; this covers the separate
 * initial-login/logout flow behind POST /GymMonster/auth/login and /logout.
 */
class ImpSessionServiceTest {

    @Mock
    private WebClient webClient;

    @Mock
    private ReactiveRedisTemplate<String, String> redisTemplate;

    @Mock
    private RedisHandlerInterface redisHandler;

    private ObjectMapper objectMapper;

    @Mock
    private ServicesUrl servicesUrl;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @SuppressWarnings("rawtypes")
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    private ImpSession impSession;

    private final String username = "test-user";
    private final String password = "test-password";

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        // Constructed manually (not @InjectMocks) so the real ObjectMapper above -
        // rather than a mock - is the one ImpSession actually parses JSON with.
        impSession = new ImpSession(webClient, redisTemplate, redisHandler, objectMapper, servicesUrl);

        ServicesUrl.Keycloak keycloak = new ServicesUrl.Keycloak();
        keycloak.setClientId("Docker-Gym");
        keycloak.setClientSecret("test-client-secret");
        when(servicesUrl.getKeycloak()).thenReturn(keycloak);

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.contentType(any())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        // ImpSession.logout() passes redisHandler.logout(username) as an eagerly-evaluated
        // argument to Mono.then(), so it must never be null even in tests that only care
        // about the webClient failure path.
        when(redisHandler.logout(anyString())).thenReturn(Mono.just("Logout Successful"));
    }

    @Test
    void loginCachesTokensInRedisAndReturnsThem() {
        String keycloakResponse = "{ \"access_token\": \"new-access-token\", \"refresh_token\": \"new-refresh-token\", "
                + "\"expires_in\": 3600, \"refresh_expires_in\": 7200 }";
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(keycloakResponse));
        when(redisHandler.login(eq(username), any(Token.class)))
                .thenReturn(Mono.just("Login Successful"));

        StepVerifier.create(impSession.login(username, password))
                .assertNext(token -> {
                    assertEquals(username, token.getUsername());
                    assertEquals("new-access-token", token.getAccessToken());
                    assertEquals("new-refresh-token", token.getRefreshToken());
                })
                .verifyComplete();

        verify(redisHandler, times(1)).login(eq(username), any(Token.class));
    }

    @Test
    void loginFailsWhenKeycloakResponseIsNotValidJson() {
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("not-json"));

        StepVerifier.create(impSession.login(username, password))
                .expectErrorMatches(error -> error instanceof RuntimeException
                        && error.getMessage().contains("Failed to parse token response"))
                .verify();
    }

    @Test
    void logoutRevokesTokenAtKeycloakThenClearsRedis() {
        when(responseSpec.bodyToMono(Void.class)).thenReturn(Mono.empty());
        when(redisHandler.logout(username)).thenReturn(Mono.just("Logout Successful"));

        StepVerifier.create(impSession.logout("some-refresh-token", username))
                .expectNext("Logout Successful")
                .verifyComplete();
    }

    @Test
    void logoutReturnsAFriendlyMessageWhenKeycloakCallFails() {
        when(responseSpec.bodyToMono(Void.class))
                .thenReturn(Mono.error(new RuntimeException("Keycloak unreachable")));

        StepVerifier.create(impSession.logout("some-refresh-token", username))
                .expectNextMatches(message -> message.startsWith("Logout failed:"))
                .verifyComplete();
    }
}
