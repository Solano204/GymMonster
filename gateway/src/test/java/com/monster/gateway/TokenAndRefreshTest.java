package com.monster.gateway;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monster.gateway.Config.RedisTokenValidationFilter;
import com.monster.gateway.Entities.Token;
import com.monster.gateway.InterfaceServices.RedisHandlerInterface;
import com.monster.gateway.PropertiesUrl.ServicesUrl;
import com.monster.gateway.PropertiesUrl.ServicesUrl.Keycloak;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import org.apache.http.protocol.HTTP;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebExchangeDecorator;
import org.springframework.web.server.adapter.DefaultServerWebExchange;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TokenAndRefreshTest {

    @Mock
    private ReactiveRedisTemplate<String, String> redisTemplate;

    @Mock
    private WebClient webClient;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ReactiveJwtDecoder jwtDecoder;

    @Mock
    private RedisHandlerInterface redisHandler;

    @Mock
    private ServicesUrl servicesUrl;


    // This allow me to mock a request (with body and uri(useful in put or post))
    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    // This allow me to mock a request (with headers(for authorization tokens and more))
    @Mock
    private WebClient.RequestHeadersSpec<?> requestHeadersSpec;

    // This allow me to mock a request (with headers(for authorization tokens and more) but without body )
    @Mock
    private WebClient.RequestHeadersUriSpec<?> requestHeadersUriSpec;

    // This allow me to mock a response (status and body)
    @Mock
    private WebClient.ResponseSpec responseSpec;

    
    @InjectMocks
    private RedisTokenValidationFilter filter;

    private final String refreshToken = "sample-refresh-token";
    private final String username = "test-user";


    @BeforeEach
    void setUp(TestInfo testInfo) throws Exception {
    MockitoAnnotations.openMocks(this); // initialize mocks
    if (testInfo.getDisplayName().equals("testAttemptRefreshToken_Success")) {
        
    // Mock the Keycloak URL and client credentials
    ServicesUrl.Keycloak keycloak = mock(ServicesUrl.Keycloak.class);
    when(keycloak.getUrl()).thenReturn("http://mock-keycloak-url.com");
    when(keycloak.getClientId()).thenReturn("mock-client-id");
    when(keycloak.getClientSecret()).thenReturn("mock-client-secret");
    when(servicesUrl.getKeycloak()).thenReturn(keycloak);

    // Mock WebClient behavior ONLY FOR POST REQUEST NOT OTHERS(GET,PUT,DELETE)
    when(webClient.post()).thenReturn(requestBodyUriSpec); // It simulates starting a POST request, which would typically follow in the WebClient flow.
    when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodyUriSpec);//This allows for method chaining, meaning after specifying the URI, the next call can continue the configuration (like setting headers or the body).
    when(requestBodyUriSpec.contentType(MediaType.APPLICATION_FORM_URLENCODED)).thenReturn(requestBodyUriSpec);// This indicates that the request will be sent with the content type set to application/x-www-form-urlencoded, which is a common type for form submissions.
    when(requestBodyUriSpec.body(any(BodyInserters.FormInserter.class))).thenReturn(requestHeadersSpec); // This specifies that when a body is set using the body, This prepares the request to include form data, and again supports method chaining to proceed to set headers.
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);// What it does: It specifies that when the retrieve() method is called on the requestHeadersSpec, it should return the responseSpec mock. Purpose: This simulates the action of sending the HTTP request and preparing to handle the response.
    when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);// What it does: This line specifies that when onStatus is called on the responseSpec, it should return the responseSpec mock again.  Purpose: This allows further chaining of calls to handle the response status, enabling you to simulate handling various HTTP response statuses.
    when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(
        "{ \"access_token\": \"new-access-token\", \"refresh_token\": \"new-refresh-token\", \"expires_in\": 3600, \"refresh_expires_in\": 7200 }"
    ));// What it does: This line sets up the response body to return a mocked Mono containing a JSON string when bodyToMono(String.class) is called on responseSpec.Purpose: This simulates a successful response from the HTTP request, returning a JSON representation of access and refresh tokens along with their expiration details.
    when(objectMapper.readValue(anyString(), any(TypeReference.class)))
            .thenReturn(Map.of(
                    "access_token", "new-access-token",
                    "refresh_token", "new-refresh-token",
                    "expires_in", 3600,
                    "refresh_expires_in", 7200
            )); // What it does: This line specifies that when readValue is called on the objectMapper mock with any string and a TypeReference, it should return a predefined map containing the token data. Purpose: This simulates the behavior of parsing the JSON response into a Java Map, which your application code may use to access the tokens and expiration information.
    }

    if (testInfo.getDisplayName().equals("testValidateToken_Success")) {
        when(jwtDecoder.decode("valid-token")).thenReturn(Mono.just(mock(Jwt.class)));
    }
}


    @Test
    void testAttemptRefreshToken_Success() {
        // Set up the request and exchange with headers
        HttpHeaders headers = new HttpHeaders();
        headers.add("refresh_token", refreshToken);
        headers.add("username", username);

        MockServerHttpRequest mockRequest = MockServerHttpRequest.get("/")
                .headers(headers)
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(mockRequest);

        StepVerifier.create(filter.attemptRefreshToken(exchange))
                .expectNextMatches(token -> 
                        token.getAccessToken().equals("new-access-token") &&
                        token.getRefreshToken().equals("new-refresh-token") &&
                        token.getUsername().equals(username) &&
                        token.getExpiresAt().isAfter(Instant.now()) &&
                        token.getRefreshExpiresAt().isAfter(Instant.now())
                )
                .verifyComplete();
    }

    @Test
    void testExtractToken() {
        // Create a mock ServerWebExchange
        ServerWebExchange exchange = mock(ServerWebExchange.class);
        ServerHttpRequest request = mock(ServerHttpRequest.class);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer test-token");
        when(request.getHeaders()).thenReturn(headers);
        when(exchange.getRequest()).thenReturn(request);
        RedisTokenValidationFilter filter = new RedisTokenValidationFilter(
            redisTemplate,
            webClient,
            objectMapper,
            jwtDecoder,
            redisHandler,
            servicesUrl
        );

        // Call the extractToken method
        String token = filter.extractToken(exchange);
        
        // Verify that the token is correctly extracted
        assertEquals("test-token", token);
    }


    @Test
void testShouldSkipValidation() {
    String skipPath = "GymMonster/auth/login";
    String validatePath = "GymMonster/api/resource";

    assertTrue(filter.shouldSkipValidation(skipPath));
    assertFalse(filter.shouldSkipValidation(validatePath));
}

@Test
void testValidateToken_Success() {
    when(jwtDecoder.decode("valid-token")).thenReturn(Mono.just(mock(Jwt.class)));

    StepVerifier.create(filter.validateToken("valid-token"))
            .expectNext(true)
            .verifyComplete();
}

@Test
void testValidateToken_Failure() {
    when(jwtDecoder.decode("invalid-token")).thenReturn(Mono.error(new RuntimeException("Invalid token")));

    StepVerifier.create(filter.validateToken("invalid-token"))
            .expectNext(false)
            .verifyComplete();
}
{}

@Test
void testCacheNewTokens_Success() {
    Token token = new Token("user123", "new-access-token", "new-refresh-token", Instant.now(), Instant.now());

    when(redisHandler.login("user123", token)).thenReturn(Mono.empty());

    StepVerifier.create(filter.cacheNewTokens("user123", token))
            .expectNext(true)
            .verifyComplete();
}


@Test
void testHandleError() {
    ServerWebExchange exchange = mock(ServerWebExchange.class);
    ServerHttpResponse response = mock(ServerHttpResponse.class);
    when(exchange.getResponse()).thenReturn(response);

    Mono<Void> result = filter.handleError(exchange, "Unauthorized", HttpStatus.UNAUTHORIZED);

    StepVerifier.create(result)
            .expectComplete()
            .verify();
}

@Test
void testAttemptRefreshToken_WebClientPostRequestFails() {
    
    ServerWebExchange exchange = mock(ServerWebExchange.class);
    ServerHttpRequest request = mock(ServerHttpRequest.class);
    HttpHeaders headers = new HttpHeaders();
    headers.add("refresh_token", "sample-refresh-token");
    headers.add("username", "test-user");
    when(request.getHeaders()).thenReturn(headers);
    when(exchange.getRequest()).thenReturn(request);

    // Mock the WebClient to return a simulated error response
    WebClient.RequestBodyUriSpec requestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
    WebClient.RequestHeadersSpec<?> requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
    WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

    // Chain the mocks to simulate the WebClient call sequence
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.contentType(MediaType.APPLICATION_FORM_URLENCODED)).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.body(any(BodyInserters.FormInserter.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.error(new RuntimeException("Simulated Keycloak failure")));

    StepVerifier.create(filter.attemptRefreshToken(exchange))
            .expectError(RuntimeException.class)
            .verify();
}


}

