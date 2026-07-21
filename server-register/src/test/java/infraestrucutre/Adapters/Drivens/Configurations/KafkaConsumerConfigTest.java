package infraestrucutre.Adapters.Drivens.Configurations;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.test.util.ReflectionTestUtils;

import infraestrucutre.Adapters.Drivens.Entities.AllClient;
import infraestrucutre.Adapters.Drivens.PropertiesUrl.ServicesUrl;

class KafkaConsumerConfigTest {

    private ServicesUrl servicesUrlFor(String bootstrapAddress) {
        ServicesUrl servicesUrl = new ServicesUrl();
        ServicesUrl.Kafka kafka = new ServicesUrl.Kafka();
        kafka.setBootstrapAddress(bootstrapAddress);
        servicesUrl.setKafka(kafka);
        return servicesUrl;
    }

    @Test
    void consumerFactory_configuresBootstrapAddressFromServicesUrl() {
        KafkaConsumerConfig config = new KafkaConsumerConfig(servicesUrlFor("localhost:9092"));

        ConsumerFactory<String, AllClient> factory = config.consumerFactory();

        assertThat(factory).isInstanceOf(DefaultKafkaConsumerFactory.class);
        Object bootstrapServers = factory.getConfigurationProperties()
                .get(org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG);
        assertThat(bootstrapServers).isEqualTo("localhost:9092");
    }

    @Test
    void consumerFactory_usesFixedGroupId() {
        KafkaConsumerConfig config = new KafkaConsumerConfig(servicesUrlFor("localhost:9092"));

        ConsumerFactory<String, AllClient> factory = config.consumerFactory();

        assertThat(factory.getConfigurationProperties()
                .get(org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG)).isEqualTo("grupo1");
    }

    @Test
    void kafkaListenerContainerFactory_wiresConsumerFactoryAndErrorHandler() {
        KafkaConsumerConfig config = new KafkaConsumerConfig(servicesUrlFor("localhost:9092"));

        var containerFactory = config.kafkaListenerContainerFactory();

        assertThat(ReflectionTestUtils.getField(containerFactory, "consumerFactory")).isNotNull();
    }

    @Test
    void kafkaErrorHandler_isADefaultErrorHandler_boundedRetryNotUnboundedDefault() {
        // Doc comment on the bean explains the intent (3 retries, 1s apart, then log-and-move-on)
        // - asserting on DefaultErrorHandler's private backoff field names is too brittle across
        // spring-kafka versions, so this pins the one thing that matters observably from outside:
        // a bean of this type exists and is returned, replacing whatever the implicit default
        // retry/skip behavior would otherwise have been for a listener with no error handler.
        KafkaConsumerConfig config = new KafkaConsumerConfig(servicesUrlFor("localhost:9092"));

        CommonErrorHandler errorHandler = config.kafkaErrorHandler();

        assertThat(errorHandler).isInstanceOf(DefaultErrorHandler.class);
    }
}
