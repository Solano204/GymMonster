package com.monster.gateway.Config;
import org.apache.http.HttpHeaders;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monster.gateway.Entities.Token;
import com.monster.gateway.InterfaceServices.RedisHandlerInterface;
import com.monster.gateway.InterfaceServices.Session;
import com.monster.gateway.PropertiesUrl.ServicesUrl;

import lombok.AllArgsConstructor;
import lombok.Data;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.*;
import java.time.Instant;

@Component
@Data
@AllArgsConstructor
public class RedisTokenValidationFilter implements GlobalFilter, Ordered {


  

    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final ReactiveJwtDecoder jwtDecoder;
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    private static final Set<String> EXCLUDED_PATHS = Set.of("GymMonster/auth/login", "GymMonster/auth/logout");

    private final RedisHandlerInterface redisHandler;
    private final ServicesUrl servicesUrl;

    private static final Logger logger = LoggerFactory.getLogger(RedisTokenValidationFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String requestPath = exchange.getRequest().getURI().getPath();
        
        // Check if the path should skip validation
        if (shouldSkipValidation(requestPath)) {
            return chain.filter(exchange); // Skip token validation
        }

        String token = extractToken(exchange);
        String username = exchange.getRequest().getHeaders().getFirst("username");

        if (token == null || username == null) {
            return handleError(exchange, "Missing token or username", HttpStatus.UNAUTHORIZED);
        }

        return redisTemplate.opsForValue().get("access_token:" + username)
                .flatMap(cachedToken -> {
                    if (Objects.equals(cachedToken, token)) {
                        return chain.filter(exchange); // Proceed if tokens match
                    } else {
                        return validateToken(token)
                                .flatMap(isValid -> {
                                    if (isValid) {
                                        return redisHandler.saveAccessToken(username, token)
                                                .then(chain.filter(exchange)); // Cache and proceed
                                    } else {
                                        return attemptRefreshToken(exchange)
                                                .flatMap(newTokens -> cacheNewTokens(username, newTokens)
                                                        .then(chain.filter(exchange)))
                                                .switchIfEmpty(Mono.defer(() -> {
                                                    return handleError(exchange, "Unable to refresh token",
                                                            HttpStatus.UNAUTHORIZED);
                                                }));
                                    }
                                })
                                .onErrorResume(e -> {
                                    return handleError(exchange, "Token validation error: " + e.getMessage(),
                                            HttpStatus.UNAUTHORIZED);
                                });
                    }
                })
                .switchIfEmpty(Mono.defer(() -> {
                    return validateToken(token)
                            .flatMap(isValid -> {
                                if (isValid) {
                                    return redisHandler.saveAccessToken(username, token)
                                            .then(chain.filter(exchange));
                                } else {
                                    return handleError(exchange, "Invalid token for user: " + username,
                                            HttpStatus.UNAUTHORIZED);
                                }
                            })
                            .onErrorResume(e -> {
                                return handleError(exchange, "Token validation error: " + e.getMessage(),
                                        HttpStatus.UNAUTHORIZED);
                            });
                }));
    }

    private Mono<Void> handleError(ServerWebExchange exchange, String message, HttpStatus status) {
        // Set the response status
        exchange.getResponse().setStatusCode(status);

        // Set the content type to JSON
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        // Create the error response
        ErrorResponses errorResponse = ErrorResponses.of(status.value(), message);

        // Serialize the error response to JSON
        byte[] responseBytes;
        try {
            responseBytes = objectMapper.writeValueAsBytes(errorResponse);
        } catch (JsonProcessingException e) {
            // Fallback if serialization fails
            responseBytes = "{\"status\":500,\"message\":\"Internal Server Error\"}".getBytes();
        }

        // Write the response
        DataBufferFactory bufferFactory = exchange.getResponse().bufferFactory();
        DataBuffer dataBuffer = bufferFactory.wrap(responseBytes);

        return exchange.getResponse().writeWith(Mono.just(dataBuffer));
    }

    // Check if validation should be skipped based on specific patterns
    private boolean shouldSkipValidation(String requestPath) {
        return EXCLUDED_PATHS.stream().anyMatch(excludedPath -> PATH_MATCHER.match(excludedPath, requestPath));
    }

    @Override
    public int getOrder() {
        return -1; // Define order if needed
    }

    public Mono<Boolean> validateToken(String token) {
        return jwtDecoder.decode(token)
                .map(jwt -> {
                    logger.info("Token is valid: {}", jwt.getTokenValue());
                    return true; // Token is valid
                })
                .onErrorResume(e -> {
                    logger.error("Token validation failed. Error METHOD : {}", e.getMessage(), e);
                    return Mono.just(false); // Token is invalid
                });
    }

    @SuppressWarnings("unlikely-arg-type")
    private Mono<Token> attemptRefreshToken(ServerWebExchange exchange) {
        String refreshToken = extractRefreshToken(exchange);
        String username = exchange.getRequest().getHeaders().getFirst("username");

        if (refreshToken != null) {
            return webClient.post()
                    .uri(servicesUrl.getKeycloak().getUrl()+"/realms/docker-real/protocol/openid-connect/token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData("client_id", servicesUrl.getKeycloak().getClientId())
                            .with("client_secret", servicesUrl.getKeycloak().getClientSecret())
                            .with("grant_type", "refresh_token")
                            .with("refresh_token", refreshToken))
                    .retrieve()
                    .onStatus(HttpStatus.INTERNAL_SERVER_ERROR::equals, clientResponse -> {
                        return clientResponse.bodyToMono(String.class).flatMap(body -> {
                            logger.error("Error response: {}", body);
                            return Mono.error(new RuntimeException("Server returned an error"));
                        });
                    })
                    .bodyToMono(String.class)
                    .flatMap(response -> {
                        try {
                            // Parse the JSON response
                            Map<String, Object> responseMap = objectMapper.readValue(response,
                                    new TypeReference<Map<String, Object>>() {
                                    });
                            String newAccessToken = (String) responseMap.get("access_token");
                            String newRefreshToken = (String) responseMap.get("refresh_token");
                            Instant expiresAt = Instant.now()
                                    .plusSeconds(Long.parseLong(responseMap.get("expires_in").toString()));
                            Instant refreshExpiresAt = Instant.now()
                                    .plusSeconds(Long.parseLong(responseMap.get("refresh_expires_in").toString()));

                            // Log success
                            logger.info("Successfully refreshed token for user: {}", username);
                            return Mono.just(
                                    new Token(username,  newAccessToken, newRefreshToken, expiresAt, refreshExpiresAt));
                        } catch (Exception e) {
                            logger.error("Failed to parse token response. Error: {}", e.getMessage(), e);
                            return Mono.error(new RuntimeException("Failed to parse token response"));
                        }
                    })
                    .doOnError(e -> logger.error("FAILED TO REFRESH TOKEN. Error: {}", e.getMessage(), e)); // Log
                                                                                                            // refresh
                                                                                                            // token
                                                                                                            // error
        }

        // Log if refresh token is not present
        logger.warn("Refresh token is null for user: {}", username);
        return Mono.empty();
    }

    private Mono<Boolean> cacheNewTokens(String username, Token newTokens) {
        return redisHandler.login(username, newTokens).then(Mono.just(true)).doOnError(e -> logger.error("Failed to cache new tokens for user: {}. Error: {}", username,
        e.getMessage(), e));
    }

    private String extractToken(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7); // Remove "Bearer " prefix
        }
        return null;
    }

    private String extractRefreshToken(ServerWebExchange exchange) {
        return exchange.getRequest().getHeaders().getFirst("refresh_token");
    }

    
}
