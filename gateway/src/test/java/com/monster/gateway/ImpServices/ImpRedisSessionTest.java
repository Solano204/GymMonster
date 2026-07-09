package com.monster.gateway.ImpServices;

import static org.mockito.Mockito.when;

import com.monster.gateway.Entities.Token;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;

import java.time.Instant;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class ImpRedisSessionTest {

    @Mock
    private ReactiveRedisTemplate<String, String> redisTemplate;

    @Mock
    private ReactiveValueOperations<String, String> valueOperations;

    private ImpRedisSession impRedisSession;

    private final String username = "test-user";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        impRedisSession = new ImpRedisSession(redisTemplate);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void loginCachesBothAccessAndRefreshTokens() {
        Token token = new Token(username, "access-123", "refresh-456", Instant.now(), Instant.now());
        when(valueOperations.set("access_token:" + username, "access-123")).thenReturn(Mono.just(true));
        when(valueOperations.set("refresh_token:" + username, "refresh-456")).thenReturn(Mono.just(true));

        StepVerifier.create(impRedisSession.login(username, token))
                .expectNext("Login Successful")
                .verifyComplete();
    }

    @Test
    void logoutDeletesBothTokensWhenBothExist() {
        when(valueOperations.delete("access_token:" + username)).thenReturn(Mono.just(true));
        when(valueOperations.delete("refresh_token:" + username)).thenReturn(Mono.just(true));

        StepVerifier.create(impRedisSession.logout(username))
                .expectNext("Logout Successful")
                .verifyComplete();
    }

    @Test
    void logoutReportsWhenAccessTokenWasAlreadyGone() {
        when(valueOperations.delete("access_token:" + username)).thenReturn(Mono.just(false));

        StepVerifier.create(impRedisSession.logout(username))
                .expectNext("Access Token not found")
                .verifyComplete();
    }

    @Test
    void logoutReportsWhenOnlyRefreshTokenWasMissing() {
        when(valueOperations.delete("access_token:" + username)).thenReturn(Mono.just(true));
        when(valueOperations.delete("refresh_token:" + username)).thenReturn(Mono.just(false));

        StepVerifier.create(impRedisSession.logout(username))
                .expectNext("Access Token deleted, Refresh Token not found")
                .verifyComplete();
    }

    @Test
    void logoutSurfacesRedisErrorsAsAMessageInsteadOfThrowing() {
        when(valueOperations.delete("access_token:" + username))
                .thenReturn(Mono.error(new RuntimeException("Redis down")));

        StepVerifier.create(impRedisSession.logout(username))
                .expectNextMatches(message -> message.startsWith("Error during logout:"))
                .verifyComplete();
    }

    @Test
    void saveAccessTokenReturnsTrueOnSuccess() {
        when(valueOperations.set("access_token:" + username, "new-access-token")).thenReturn(Mono.just(true));

        StepVerifier.create(impRedisSession.saveAccessToken(username, "new-access-token"))
                .expectNext(true)
                .verifyComplete();
    }
}
