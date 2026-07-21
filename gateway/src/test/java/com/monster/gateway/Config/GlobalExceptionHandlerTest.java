package com.monster.gateway.Config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleTokenValidationException_returns401Status() {
        ResponseEntity<ErrorResponses> response =
                handler.handleTokenValidationException(new TokenValidationException("Invalid token"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void handleTokenValidationException_bodyCarriesOriginalMessage() {
        ResponseEntity<ErrorResponses> response =
                handler.handleTokenValidationException(new TokenValidationException("Token expired"));

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(401);
        assertThat(response.getBody().getMessage()).isEqualTo("Token expired");
    }

    @Test
    void handleTokenValidationException_toleratesNullMessage() {
        ResponseEntity<ErrorResponses> response =
                handler.handleTokenValidationException(new TokenValidationException(null));

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isNull();
    }
}
