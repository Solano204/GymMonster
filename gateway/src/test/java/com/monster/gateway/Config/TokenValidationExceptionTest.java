package com.monster.gateway.Config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class TokenValidationExceptionTest {

    @Test
    void constructor_preservesMessage() {
        TokenValidationException exception = new TokenValidationException("Missing token or username");

        assertThat(exception.getMessage()).isEqualTo("Missing token or username");
    }

    @Test
    void isARuntimeException_soItDoesNotForceCallersToDeclareThrows() {
        TokenValidationException exception = new TokenValidationException("boom");

        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    void toleratesNullMessage() {
        TokenValidationException exception = new TokenValidationException(null);

        assertThat(exception.getMessage()).isNull();
    }
}
