package infraestrucutre.Adapters.Drivens.Configurations;
import io.micrometer.observation.ObservationRegistry;

import java.util.concurrent.TimeUnit;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientsConfig {

    // Doc 9: had no timeout at all - see server-administrator's WebClientsConfig
    // for the same fix and reasoning.
    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                .responseTimeout(java.time.Duration.ofSeconds(5))
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(5, TimeUnit.SECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(5, TimeUnit.SECONDS)));
        return WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient));
    }

    // Removed keycloakWebClient(): confirmed unused anywhere in web-page (grepped
    // the whole module), and what it had was actively wrong - baseUrl hardcoded to
    // "http://localhost:8111", which is server-informations' port, not Keycloak's
    // (8181), under a comment claiming it was "Direct URL to Keycloak". Also
    // combined @LoadBalanced with a literal host:port, which defeats what
    // @LoadBalanced is for (resolving a logical service name through the load
    // balancer) - the two don't make sense together. Dead code that was also
    // wrong, not something worth fixing in place.

}
