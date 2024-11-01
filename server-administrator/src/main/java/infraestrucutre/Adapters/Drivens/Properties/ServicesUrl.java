package infraestrucutre.Adapters.Drivens.Properties;

import org.springframework.boot.autoconfigure.cache.CacheProperties.Redis;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.info.InfoProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties(prefix = "svc")
@Data
public class ServicesUrl {
    private InfoProperties info;
    @Data
    public static class InfoProperties {
        private String name;
        private int port;
        private String url;
    }
}
