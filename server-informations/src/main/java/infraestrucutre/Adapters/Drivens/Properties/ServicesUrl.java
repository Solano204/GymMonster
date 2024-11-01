package infraestrucutre.Adapters.Drivens.Properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties(prefix = "svc")
@Data
public class ServicesUrl {
    

    private Keycloak keycloak;

    @Data
    public static class Keycloak {
        private String realm;
        private String dockerReal;
        private String username;
        private String password;
        private String clientId;
        private String clientSecret;
        private String url;
    }

  
}