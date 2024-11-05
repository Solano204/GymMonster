package com.monster.gateway;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
/*
 * 
 @ActiveProfiles("test")
 @SpringBootTest(
    webEnvironment = WebEnvironment.RANDOM_PORT
    )
    @AutoConfigureWebTestClient

public class RedisTokenValidationFilterIntegrationTest {
    
@Autowired
private ObjectMapper objectMapper;
private String validToken;
private String invalidToken;
private String refreshToken;
private String username;

@LocalServerPort
int port;

protected RequestSpecification requestSpecification;

@BeforeEach
public void setUp() {
         requestSpecification = new RequestSpecBuilder()
         .setPort(port)
         .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
         .build();
         username = "josue_admin";
         
        }

        
        @Test
        public void ya() {
            
    }
    
    @Test
    public void shouldAllowAccessForValidToken() {
        validToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        var status = given(requestSpecification)
        .header("Authorization", "Bearer " + validToken)
            .header("username", username)
            .when()
            .get("/api/admin/any")
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .statusCode();
            
            assertEquals(501, status); // Expecting status 200 (OK) for valid token
        }
        
        @Test
        public void shouldDenyAccessForInvalidToken() {
            invalidToken = "invalidToken";
            var status = given(requestSpecification)
            .header("Authorization", "Bearer " + invalidToken)
            .header("username", username)
            .when()
            .get("/api/admin/any")
            .then()
            .statusCode(HttpStatus.UNAUTHORIZED.value()) 
            .extract()
            .statusCode();
            
            assertEquals(HttpStatus.UNAUTHORIZED.value(), status); // Validate Unauthorized status
        }
        
        @Test
        public void shouldRefreshTokenWhenExpired() {
            String expiredToken = "dklhd";
        var status = given(requestSpecification)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
            .header("Authorization", "Bearer expired-token") // Simulate expired token
            .header("username", username)
            .header("refresh_token", refreshToken)
            .when()
            .post("/api/admin/any") // Adjust endpoint if needed
            .then()
            .statusCode(HttpStatus.OK.value()) // Expecting status 200 on token refresh
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .extract()
            .statusCode();
            
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), status); // Validate successful refresh
        }
        
        @Test
        public void shouldReturnErrorForMissingToken() {
            String username = "josue_admin";
        var status = given(requestSpecification)
        .header("username", username)
        .when()
        .get("/api/admin/any") // No Authorization header
        .then()
        .statusCode(HttpStatus.UNAUTHORIZED.value()) // Expecting Unauthorized (401)
        .extract()
        .statusCode();
        
        assertEquals(HttpStatus.UNAUTHORIZED.value(), status); // Validate missing token results in Unauthorized
    }
    
    // Add more tests as needed
}

*/