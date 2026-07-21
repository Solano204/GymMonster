package infraestrucutre.Adapters.Drivers.Controllers;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import application.Ports.Drivers.IServices.PoolServiceInterface;
import infraestrucutre.Adapters.Drivens.DTOS.DtoPoolReciving;
import infraestrucutre.Adapters.Drivens.Handlers.PoolHandler;
import reactor.core.publisher.Flux;

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

    @Test
    void getAllPools_returnsTheFullList() {
        when(poolService.getAllPools()).thenReturn(Flux.just(new DtoPoolReciving(1, "Olympic", "desc")));

        client.get().uri("/api/page/allPools")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(DtoPoolReciving.class).hasSize(1);
    }

    @Test
    void getAllPools_returns500_whenServiceFails() {
        when(poolService.getAllPools()).thenReturn(Flux.error(new RuntimeException("Redis down")));

        client.get().uri("/api/page/allPools")
                .exchange()
                .expectStatus().is5xxServerError();
    }
}
