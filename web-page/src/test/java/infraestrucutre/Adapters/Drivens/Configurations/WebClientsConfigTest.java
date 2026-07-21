package infraestrucutre.Adapters.Drivens.Configurations;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

class WebClientsConfigTest {

    @Test
    void webClientBuilder_buildsAUsableWebClient() {
        WebClientsConfig config = new WebClientsConfig();

        WebClient.Builder builder = config.webClientBuilder();

        assertThat(builder).isNotNull();
        assertThat(builder.build()).isNotNull();
    }

    @Test
    void webClientBuilder_returnsANewBuilderInstanceOnEachCall() {
        WebClientsConfig config = new WebClientsConfig();

        WebClient.Builder first = config.webClientBuilder();
        WebClient.Builder second = config.webClientBuilder();

        assertThat(first).isNotSameAs(second);
    }
}
