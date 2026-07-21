package infraestrucutre.Adapters.Drivens.Configurations;


import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

import infraestrucutre.Adapters.Drivens.Entities.AllClient;
import infraestrucutre.Adapters.Drivens.PropertiesUrl.ServicesUrl;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;                       

@EnableKafka
@Configuration
@Data
public class KafkaConsumerConfig {

    private final ServicesUrl   servicesUrl;

    @Bean
    public ConsumerFactory<String, AllClient> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, servicesUrl.getKafka().getBootstrapAddress()); // Use the injected value
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "grupo1"); // Ensure group ID is set
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, AllClient.class.getName()); // Set default value type

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AllClient> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, AllClient> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setCommonErrorHandler(kafkaErrorHandler());
        // Same reasoning as KafkaProducerConfig.kafkaTemplate() in web-page
        // (Doc 8): this container factory is hand-built, so tracing needs
        // enabling explicitly to extract/continue the producer's span.
        factory.getContainerProperties().setObservationEnabled(true);
        return factory;
    }

    // No error handler existed before - RegisterService.consumer() now throws
    // when a registration fails (see that class), and without this bean
    // whatever Spring Kafka's implicit default retry/skip behavior is for
    // this version would apply silently instead of an explicit, bounded one.
    // 3 retries, 1s apart, then log-and-move-on so a registration that keeps
    // failing (e.g. Keycloak down) can't wedge the "flow" topic's single
    // partition forever.
    @Bean
    public CommonErrorHandler kafkaErrorHandler() {
        return new DefaultErrorHandler(new FixedBackOff(1000L, 3L));
    }
}