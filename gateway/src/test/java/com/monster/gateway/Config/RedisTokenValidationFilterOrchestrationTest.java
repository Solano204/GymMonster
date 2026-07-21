package com.monster.gateway.Config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monster.gateway.Entities.Token;
import com.monster.gateway.InterfaceServices.RedisHandlerInterface;
import com.monster.gateway.PropertiesUrl.ServicesUrl;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * Covers RedisTokenValidationFilter#filter(...), the orchestration method that ties
 * together shouldSkipValidation/isPublicPageRequest/validateToken/attemptRefreshToken -
 * each already unit-tested individually in TokenAndRefreshTest and
 * RedisTokenValidationFilterPublicPathsTest, but never exercised together as the real
 * request pipeline. Every branch of the method's nested flatMap/switchIfEmpty tree is
 * covered here.
 */
class RedisTokenValidationFilterOrchestrationTest {

    @Mock
    private ReactiveRedisTemplate<String, String> redisTemplate;

    @Mock
    private ReactiveValueOperations<String, String> valueOperations;

    @Mock
    private WebClient webClient;

    @Mock
    private ReactiveJwtDecoder jwtDecoder;

    @Mock
    private RedisHandlerInterface redisHandler;

    @Mock
    private ServicesUrl servicesUrl;

    @Mock
    private GatewayFilterChain chain;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec<?> requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    private ObjectMapper objectMapper;
    private RedisTokenValidationFilter filter;

    private static final String USERNAME = "carlos";
    private static final String TOKEN = "valid-jwt-token";
    private static final String STALE_TOKEN = "stale-cached-token";
    private static final String REFRESH_TOKEN = "some-refresh-token";
    private static final String PROTECTED_PATH = "/api/admin/clients";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        filter = new RedisTokenValidationFilter(
                redisTemplate, webClient, objectMapper, jwtDecoder, redisHandler, servicesUrl);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(chain.filter(any())).thenReturn(Mono.empty());

        ServicesUrl.Keycloak keycloak = new ServicesUrl.Keycloak();
        keycloak.setUrl("http://keycloak:8181");
        keycloak.setClientId("Docker-Gym");
        keycloak.setClientSecret("test-secret");
        when(servicesUrl.getKeycloak()).thenReturn(keycloak);
    }

    private ServerWebExchange exchange(HttpMethod method, String path, Map<String, String> headers) {
        MockServerHttpRequest.BaseBuilder<?> builder = MockServerHttpRequest.method(method, path);
        headers.forEach(builder::header);
        return MockServerWebExchange.from(builder.build());
    }

    private Map<String, String> authHeaders(String token, String username) {
        Map<String, String> headers = new HashMap<>();
        if (token != null) headers.put(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        if (username != null) headers.put("username", username);
        return headers;
    }

    private void mockSuccessfulKeycloakRefresh() {
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.contentType(any())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(any(BodyInserters.FormInserter.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(
                "{ \"access_token\": \"refreshed-access-token\", \"refresh_token\": \"refreshed-refresh-token\", "
                        + "\"expires_in\": 3600, \"refresh_expires_in\": 7200 }"));
    }

    @Test
    void skipsValidationEntirely_forExcludedAuthPaths() {
        ServerWebExchange exchange = exchange(HttpMethod.POST, "GymMonster/auth/login", Map.of());

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

        verify(chain).filter(exchange);
        verifyNoInteractions(redisTemplate);
        verifyNoInteractions(jwtDecoder);
    }

    @Test
    void skipsValidationEntirely_forPublicPageGetPaths() {
        ServerWebExchange exchange = exchange(HttpMethod.GET, "/api/page/allMemberships", Map.of());

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

        verify(chain).filter(exchange);
        verifyNoInteractions(redisTemplate);
    }

    @Test
    void rejectsRequest_missingAuthorizationHeader() {
        ServerWebExchange exchange = exchange(HttpMethod.GET, PROTECTED_PATH, authHeaders(null, USERNAME));

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        verify(chain, never()).filter(any());
        verifyNoInteractions(redisTemplate);
    }

    @Test
    void rejectsRequest_missingUsernameHeader() {
        ServerWebExchange exchange = exchange(HttpMethod.GET, PROTECTED_PATH, authHeaders(TOKEN, null));

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        verify(chain, never()).filter(any());
    }

    @Test
    void proceedsWithoutRevalidation_whenCachedTokenMatchesPresentedToken() {
        ServerWebExchange exchange = exchange(HttpMethod.GET, PROTECTED_PATH, authHeaders(TOKEN, USERNAME));
        when(valueOperations.get("access_token:" + USERNAME)).thenReturn(Mono.just(TOKEN));

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

        verify(chain).filter(exchange);
        verifyNoInteractions(jwtDecoder);
        verify(redisHandler, never()).saveAccessToken(anyString(), anyString());
    }

    @Test
    void revalidatesAndCaches_whenCachedTokenDiffersButPresentedTokenIsValid() {
        ServerWebExchange exchange = exchange(HttpMethod.GET, PROTECTED_PATH, authHeaders(TOKEN, USERNAME));
        when(valueOperations.get("access_token:" + USERNAME)).thenReturn(Mono.just(STALE_TOKEN));
        when(jwtDecoder.decode(TOKEN)).thenReturn(Mono.just(mock(Jwt.class)));
        when(redisHandler.saveAccessToken(USERNAME, TOKEN)).thenReturn(Mono.just(true));

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

        verify(redisHandler).saveAccessToken(USERNAME, TOKEN);
        verify(chain).filter(exchange);
    }

    @Test
    void refreshesToken_whenCachedTokenDiffersAndPresentedTokenIsInvalid_andRefreshSucceeds() {
        Map<String, String> headers = authHeaders(TOKEN, USERNAME);
        headers.put("refresh_token", REFRESH_TOKEN);
        ServerWebExchange exchange = exchange(HttpMethod.GET, PROTECTED_PATH, headers);

        when(valueOperations.get("access_token:" + USERNAME)).thenReturn(Mono.just(STALE_TOKEN));
        when(jwtDecoder.decode(TOKEN)).thenReturn(Mono.error(new RuntimeException("expired")));
        mockSuccessfulKeycloakRefresh();
        when(redisHandler.login(eq(USERNAME), any(Token.class))).thenReturn(Mono.just("Login Successful"));

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

        verify(redisHandler).login(eq(USERNAME), any(Token.class));
        verify(chain).filter(exchange);
    }

    @Test
    void rejectsRequest_whenCachedTokenDiffersAndPresentedTokenIsInvalid_andNoRefreshTokenProvided() {
        // No refresh_token header -> attemptRefreshToken returns Mono.empty() -> switchIfEmpty branch.
        ServerWebExchange exchange = exchange(HttpMethod.GET, PROTECTED_PATH, authHeaders(TOKEN, USERNAME));
        when(valueOperations.get("access_token:" + USERNAME)).thenReturn(Mono.just(STALE_TOKEN));
        when(jwtDecoder.decode(TOKEN)).thenReturn(Mono.error(new RuntimeException("expired")));

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        verify(chain, never()).filter(any());
    }

    @Test
    void rejectsRequest_whenCacheLookupBranchErrorsDuringSave() {
        ServerWebExchange exchange = exchange(HttpMethod.GET, PROTECTED_PATH, authHeaders(TOKEN, USERNAME));
        when(valueOperations.get("access_token:" + USERNAME)).thenReturn(Mono.just(STALE_TOKEN));
        when(jwtDecoder.decode(TOKEN)).thenReturn(Mono.just(mock(Jwt.class)));
        when(redisHandler.saveAccessToken(USERNAME, TOKEN)).thenReturn(Mono.error(new RuntimeException("Redis down")));

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        verify(chain, never()).filter(any());
    }

    @Test
    void revalidatesAndCaches_whenNoCachedTokenExistsAndPresentedTokenIsValid() {
        ServerWebExchange exchange = exchange(HttpMethod.GET, PROTECTED_PATH, authHeaders(TOKEN, USERNAME));
        when(valueOperations.get("access_token:" + USERNAME)).thenReturn(Mono.empty());
        when(jwtDecoder.decode(TOKEN)).thenReturn(Mono.just(mock(Jwt.class)));
        when(redisHandler.saveAccessToken(USERNAME, TOKEN)).thenReturn(Mono.just(true));

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

        verify(redisHandler).saveAccessToken(USERNAME, TOKEN);
        verify(chain).filter(exchange);
    }

    @Test
    void rejectsRequest_whenNoCachedTokenExistsAndPresentedTokenIsInvalid() {
        ServerWebExchange exchange = exchange(HttpMethod.GET, PROTECTED_PATH, authHeaders(TOKEN, USERNAME));
        when(valueOperations.get("access_token:" + USERNAME)).thenReturn(Mono.empty());
        when(jwtDecoder.decode(TOKEN)).thenReturn(Mono.error(new RuntimeException("bad signature")));

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        verify(chain, never()).filter(any());
    }

    @Test
    void rejectsRequest_whenNoCachedTokenExistsAndSaveAccessTokenErrors() {
        ServerWebExchange exchange = exchange(HttpMethod.GET, PROTECTED_PATH, authHeaders(TOKEN, USERNAME));
        when(valueOperations.get("access_token:" + USERNAME)).thenReturn(Mono.empty());
        when(jwtDecoder.decode(TOKEN)).thenReturn(Mono.just(mock(Jwt.class)));
        when(redisHandler.saveAccessToken(USERNAME, TOKEN)).thenReturn(Mono.error(new RuntimeException("Redis down")));

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        verify(chain, never()).filter(any());
    }

    @Test
    void publicPost_toRegisterClient_skipsValidation() {
        ServerWebExchange exchange = exchange(HttpMethod.POST, "/api/page/registerClient", Map.of());

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

        verify(chain).filter(exchange);
        verifyNoInteractions(redisTemplate);
    }

    @Test
    void adminPath_isNeverTreatedAsPublic_evenWithoutCredentials() {
        ServerWebExchange exchange = exchange(HttpMethod.GET, "/api/admin/clients", Map.of());

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        verify(chain, never()).filter(any());
    }
}
