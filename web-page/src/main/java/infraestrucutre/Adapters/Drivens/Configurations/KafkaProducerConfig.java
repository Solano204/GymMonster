package infraestrucutre.Adapters.Drivens.Configurations;
import java.security.Provider.Service;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import infraestrucutre.Adapters.Drivens.Entities.AllClient;
import infraestrucutre.Adapters.Drivens.Properties.ServicesUrl;
import lombok.Data;

 @Configuration
 @Data
 public class KafkaProducerConfig {
	
  private final ServicesUrl servicesUrl;
	
 @Bean
 public ProducerFactory<String, AllClient> producerFactory() {
  Map<String, Object> configProps = new HashMap<>();
  configProps.put(
    ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, 
    servicesUrl.getKafka().getBootstrapAddress());
    configProps.put(
      ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, 
      StringSerializer.class);
      configProps.put(
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, 
        JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
      }
      
      @Bean
      public KafkaTemplate<String, AllClient> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
      }
    }
    
