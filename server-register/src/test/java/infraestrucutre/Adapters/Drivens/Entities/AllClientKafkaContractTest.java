package infraestrucutre.Adapters.Drivens.Entities;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * AllClient is hand-duplicated (not shared) between web-page (Kafka producer) and
 * server-register (Kafka consumer) to define the registration-event contract. This
 * test — mirrored byte-for-byte in web-page — pins that contract to a checked-in
 * fixture, so an edit to only one side's AllClient breaks that side's build instead of
 * silently desyncing the Kafka message shape. Plain Jackson, no Spring context: the
 * app's @SpringBootConfiguration lives under com.monster.* while this class lives under
 * infraestrucutre.*, so @JsonTest's package-upward config search can't find it.
 */
class AllClientKafkaContractTest {

    // Mirrors the ObjectMapper spring-kafka's JsonSerializer/JsonDeserializer actually
    // use at runtime (JacksonUtils.enhancedObjectMapper()): JavaTimeModule registered,
    // dates written as ISO strings rather than timestamp arrays.
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private String fixtureJson() throws Exception {
        try (var reader = new InputStreamReader(
                new ClassPathResource("contracts/all-client-kafka-contract.json").getInputStream(),
                StandardCharsets.UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        }
    }

    @Test
    void deserializesEveryFieldFromTheContractFixture() throws Exception {
        AllClient client = objectMapper.readValue(fixtureJson(), AllClient.class);

        assertThat(client.id()).isEqualTo(1L);
        assertThat(client.username()).isEqualTo("contract-test-user");
        assertThat(client.password()).isEqualTo("contract-test-password");
        assertThat(client.email()).isEqualTo("contract-test@example.com");
        assertThat(client.trainername()).isEqualTo("contract-test-trainer");
        assertThat(client.name()).isEqualTo("Contract");
        assertThat(client.secondname()).isEqualTo("Test");
        assertThat(client.lastnamep()).isEqualTo("Paternal");
        assertThat(client.lastnamem()).isEqualTo("Maternal");
        assertThat(client.age()).isEqualTo("30");
        assertThat(client.height()).isEqualTo("180");
        assertThat(client.weight()).isEqualTo("75");
        assertThat(client.membershiptype()).isEqualTo("premium");
        assertThat(client.startdate()).isEqualTo(LocalDate.of(2024, 1, 1));
        assertThat(client.startinscription()).isEqualTo(LocalDate.of(2024, 1, 1));
        assertThat(client.endinscription()).isEqualTo(LocalDate.of(2025, 1, 1));
        assertThat(client.price()).isEqualTo(99.99);
    }

    @Test
    void roundTripsBackToExactlyTheContractFixture() throws Exception {
        AllClient client = objectMapper.readValue(fixtureJson(), AllClient.class);

        JsonNode roundTripped = objectMapper.readTree(objectMapper.writeValueAsString(client));
        JsonNode expected = objectMapper.readTree(fixtureJson());

        assertThat(roundTripped).isEqualTo(expected);
    }
}
