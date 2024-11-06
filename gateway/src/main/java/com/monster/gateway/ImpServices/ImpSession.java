package com.monster.gateway.ImpServices;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monster.gateway.Entities.Token;
import com.monster.gateway.InterfaceServices.RedisHandlerInterface;
import com.monster.gateway.InterfaceServices.Session;
import com.monster.gateway.PropertiesUrl.ServicesUrl;

import lombok.Data;
import reactor.core.publisher.Mono;

import java.security.Provider.Service;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Data
@Component
public class ImpSession implements Session {

    private final WebClient webClient;
    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final RedisHandlerInterface redisHandler;

    private final ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(ImpSession.class);

    private final ServicesUrl servicesUrl;
    
    @Override
    public Mono<Token> login(String username, String password) {

     
        
        return webClient
                .post()
                .uri("/realms/docker-real/protocol/openid-connect/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters
                        .fromFormData("client_id",  servicesUrl.getKeycloak().getClientId())
                        .with("client_secret", servicesUrl.getKeycloak().getClientSecret())
                        .with("grant_type", "password")
                        .with("username", username)
                        .with("password", password))
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(response -> {
                    try {
                        Map<String, Object> responseMap;
                        responseMap = objectMapper.readValue(response,
                                new TypeReference<Map<String, Object>>() {
                                });
                        // Parse access token and expiration details
                        String accessToken = parseFieldFromResponse(response, "access_token");
                        String refreshToken = parseFieldFromResponse(response, "refresh_token");
                        Instant expiresAt = Instant.now()
                                .plusSeconds(Long.parseLong(responseMap.get("expires_in").toString()));
                        Instant refreshExpiresAt = Instant.now()
                                .plusSeconds(Long.parseLong(responseMap.get("refresh_expires_in").toString()));


                        Token token = new Token(
                                username,
                                accessToken,
                                refreshToken,
                                expiresAt,
                                refreshExpiresAt);

                        // Store tokens in Redis with expiration times
                        return redisHandler.login(username, token)
                                .then(Mono.just(token));
                    } catch (JsonProcessingException e) {
                        logger.error("Failed to parse token response. Error: {}", e.getMessage(), e);
                        return Mono.error(new RuntimeException("Failed to parse token response"));
                    }
                });
    }

    // Helper method to extract fields from the JSON response
    private String parseFieldFromResponse(String response, String fieldName) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response);
            return jsonNode.path(fieldName).asText();
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to parse response: " + e.getMessage());
        }
    }

    @Override
    public Mono<String> logout(String refreshToken, String username) {
        // Define the request to revoke the refresh token
        return webClient
                .post()
                .uri("/realms/docker-real/protocol/openid-connect/logout")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters
                        .fromFormData("client_id", servicesUrl.getKeycloak().getClientId())
                        .with("client_secret", servicesUrl.getKeycloak().getClientSecret())
                        .with("refresh_token", refreshToken))
                .retrieve()
                .bodyToMono(Void.class)
                .then(redisHandler.logout(username))
                .onErrorResume(e -> {
                    // Handle errors appropriately (e.g., log the error)
                    return Mono.just("Logout failed: " + e.getMessage());
                });
    }

}
