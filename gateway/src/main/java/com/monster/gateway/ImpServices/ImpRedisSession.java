package com.monster.gateway.ImpServices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;

import com.monster.gateway.Entities.Token;
import com.monster.gateway.InterfaceServices.RedisHandlerInterface;

import lombok.AllArgsConstructor;
import lombok.Data;
import reactor.core.publisher.Mono;

@Data
@AllArgsConstructor
@Component
public class ImpRedisSession implements RedisHandlerInterface {

    private final ReactiveRedisTemplate<String, String> redisTemplate;
     private static final Logger logger = LoggerFactory.getLogger(ImpRedisSession.class);

    @Override
    public Mono<String> login(String username, Token newTokens) {
        return redisTemplate.opsForValue().set("access_token:" + username, newTokens.getAccessToken())
                .then(redisTemplate.opsForValue().set("refresh_token:" + username, newTokens.getRefreshToken()))
                .then(Mono.just("Login Successful"))
                .doOnError(e -> logger.error("Failed to cache new tokens for user: {}. Error: {}", username,
                        e.getMessage(), e)); // Log caching error

    }

    @Override
public Mono<String> logout(String username) {
    // Construct keys for access and refresh tokens
    String accessTokenKey = "access_token:" + username;
    String refreshTokenKey = "refresh_token:" + username;

    return redisTemplate.opsForValue().delete(accessTokenKey)
            .flatMap(accessDeleted -> {
                if (Boolean.TRUE.equals(accessDeleted)) {
                    // If access token is deleted successfully, attempt to delete refresh token
                    return redisTemplate.opsForValue().delete(refreshTokenKey)
                            .flatMap(refreshDeleted -> {
                                if (Boolean.TRUE.equals(refreshDeleted)) {
                                    // Both tokens deleted successfully
                                    return Mono.just("Logout Successful");
                                } else {
                                    // Access token deleted but refresh token not found
                                    return Mono.just("Access Token deleted, Refresh Token not found");
                                }
                            });
                } else {
                    // Access token not found
                    return Mono.just("Access Token not found");
                }
            })
            .onErrorResume(e -> {
                // Handle any errors that occur during the delete operations
                return Mono.just("Error during logout: " + e.getMessage());
            });
}

    @Override
    public Mono<Boolean> saveAccessToken(String username, String accessToken) {
        return redisTemplate.opsForValue().set("access_token:" + username, accessToken)
        .then(Mono.just(true))
        .doOnError(e -> logger.error("Failed to cache new tokens for user: {}. Error: {}", username,
                e.getMessage(), e));
    }

}
