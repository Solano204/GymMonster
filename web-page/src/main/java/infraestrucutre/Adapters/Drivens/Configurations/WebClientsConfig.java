package infraestrucutre.Adapters.Drivens.Configurations;
import io.micrometer.observation.ObservationRegistry;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientsConfig {

    
    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
    
    @LoadBalanced
    @Bean
    public WebClient keycloakWebClient() {
        return WebClient.builder()
                    .baseUrl("http://localhost:8111") // Direct URL to Keycloak
                .build();
    }
    
}
