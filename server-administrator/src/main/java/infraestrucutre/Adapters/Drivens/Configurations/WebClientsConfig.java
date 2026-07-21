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

    // Doc 9: had no timeout at all - a slow/hung server-informations or
    // Keycloak call would hang whatever request depended on it indefinitely.
    // 3s to connect, 5s to read/write a response - this service's own calls
    // are simple CRUD proxying, not long-running work.
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

}
