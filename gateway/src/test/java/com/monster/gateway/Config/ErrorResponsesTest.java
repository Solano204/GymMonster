package com.monster.gateway.Config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

class ErrorResponsesTest {

    @Test
    void constructor_setsStatusAndMessage() {
        ErrorResponses response = new ErrorResponses(401, "Missing token or username");

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getMessage()).isEqualTo("Missing token or username");
    }

    @Test
    void ofFactory_isEquivalentToConstructor() {
        ErrorResponses viaOf = ErrorResponses.of(500, "boom");
        ErrorResponses viaConstructor = new ErrorResponses(500, "boom");

        assertThat(viaOf).isEqualTo(viaConstructor);
    }

    @Test
    void settersMutateState() {
        ErrorResponses response = ErrorResponses.of(200, "ok");

        response.setStatus(403);
        response.setMessage("Forbidden");

        assertThat(response.getStatus()).isEqualTo(403);
        assertThat(response.getMessage()).isEqualTo("Forbidden");
    }

    @Test
    void equalsAndHashCode_matchForEqualFieldValues() {
        ErrorResponses a = ErrorResponses.of(401, "Invalid token");
        ErrorResponses b = ErrorResponses.of(401, "Invalid token");

        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    void equals_isFalse_whenStatusDiffers() {
        ErrorResponses a = ErrorResponses.of(401, "Invalid token");
        ErrorResponses b = ErrorResponses.of(403, "Invalid token");

        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void serialization_omitsNullMessageField_dueToJsonIncludeNonNull() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ErrorResponses response = new ErrorResponses(500, null);

        String json = mapper.writeValueAsString(response);

        assertThat(json).contains("\"status\":500");
        assertThat(json).doesNotContain("message");
    }

    @Test
    void serialization_includesMessage_whenPresent() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ErrorResponses response = ErrorResponses.of(401, "Unauthorized");

        String json = mapper.writeValueAsString(response);

        assertThat(json).contains("\"status\":401");
        assertThat(json).contains("\"message\":\"Unauthorized\"");
    }
}
