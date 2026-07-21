package infraestrucutre.Adapters.Drivens.Configurations;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.test.util.ReflectionTestUtils;

import infraestrucutre.Adapters.Drivens.Entities.AllClient;
import infraestrucutre.Adapters.Drivens.Properties.ServicesUrl;

class KafkaProducerConfigTest {

    private ServicesUrl servicesUrlFor(String bootstrapAddress) {
        ServicesUrl servicesUrl = new ServicesUrl();
        ServicesUrl.KafkaProperties kafka = new ServicesUrl.KafkaProperties();
        kafka.setBootstrapAddress(bootstrapAddress);
        servicesUrl.setKafka(kafka);
        return servicesUrl;
    }

    @Test
    void producerFactory_configuresBootstrapAddressFromServicesUrl() {
        KafkaProducerConfig config = new KafkaProducerConfig(servicesUrlFor("localhost:9092"));

        ProducerFactory<String, AllClient> factory = config.producerFactory();

        assertThat(factory.getConfigurationProperties()
                .get(org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG))
                .isEqualTo("localhost:9092");
    }

    @Test
    void producerFactory_enablesAcksAllAndIdempotence() {
        KafkaProducerConfig config = new KafkaProducerConfig(servicesUrlFor("localhost:9092"));

        ProducerFactory<String, AllClient> factory = config.producerFactory();

        assertThat(factory.getConfigurationProperties()
                .get(org.apache.kafka.clients.producer.ProducerConfig.ACKS_CONFIG)).isEqualTo("all");
        assertThat(factory.getConfigurationProperties()
                .get(org.apache.kafka.clients.producer.ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG)).isEqualTo(true);
    }

    @Test
    void kafkaTemplate_isBackedByTheProducerFactory_withObservationEnabled() {
        KafkaProducerConfig config = new KafkaProducerConfig(servicesUrlFor("localhost:9092"));

        KafkaTemplate<String, AllClient> template = config.kafkaTemplate();

        assertThat(template).isNotNull();
        assertThat(ReflectionTestUtils.getField(template, "observationEnabled")).isEqualTo(true);
    }
}
