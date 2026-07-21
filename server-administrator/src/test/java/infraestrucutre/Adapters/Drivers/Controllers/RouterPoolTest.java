package infraestrucutre.Adapters.Drivers.Controllers;

import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import application.Ports.Drivers.IServices.PoolServiceInterface;
import infraestrucutre.Adapters.Drivens.DTOS.DtoPoolSent;
import infraestrucutre.Adapters.Drivens.Handlers.PoolHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class RouterPoolTest {

    @Mock
    private PoolServiceInterface poolService;

    private WebTestClient client;

    @BeforeEach
    void setUp() {
        PoolHandler handler = new PoolHandler(poolService);
        RouterFunction<ServerResponse> routes = new RouterPool().poolRoutes(handler);
        client = WebTestClient.bindToRouterFunction(routes).build();
    }

    private DtoPoolSent sample() {
        return new DtoPoolSent(1, "Olympic Pool", "50m lap pool", LocalDate.now(), LocalDate.now(), LocalDate.now().plusMonths(1));
    }

    @Test
    void getAllPools_returnsTheFullList() {
        when(poolService.getAllPools()).thenReturn(Flux.just(sample()));

        client.get().uri("/api/admin/allPools")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(DtoPoolSent.class).hasSize(1);
    }

    @Test
    void createPool_returns201Created() {
        DtoPoolSent pool = sample();
        when(poolService.createPool(pool)).thenReturn(Mono.just(pool));

        client.post().uri("/api/admin/pools/create")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(pool)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(DtoPoolSent.class).isEqualTo(pool);
    }

    @Test
    void updatePool_returnsConfirmationMessage() {
        DtoPoolSent pool = sample();
        when(poolService.updatePool("Olympic Pool", pool)).thenReturn(Mono.just("Updated"));

        client.put().uri("/api/admin/pools/Olympic Pool/update")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(pool)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Updated");
    }

    @Test
    void deletePoolByName_returnsConfirmationMessage() {
        when(poolService.deletePoolByName("Olympic Pool")).thenReturn(Mono.just("Deleted"));

        client.delete().uri("/api/admin/pools/Olympic Pool/delete")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Deleted");
    }

    @Test
    void deletePoolByName_returns404_whenPoolDoesNotExist() {
        when(poolService.deletePoolByName("Ghost")).thenReturn(
                Mono.error(WebClientResponseException.create(
                        HttpStatus.NOT_FOUND.value(), "Not Found", HttpHeaders.EMPTY, new byte[0], null)));

        client.delete().uri("/api/admin/pools/Ghost/delete")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404);
    }
}
