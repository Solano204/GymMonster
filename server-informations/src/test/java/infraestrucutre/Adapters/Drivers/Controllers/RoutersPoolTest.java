package infraestrucutre.Adapters.Drivers.Controllers;

import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import application.Ports.Drivers.IServices.PoolServiceInterface;
import infraestrucutre.Adapters.Drivens.Entities.Pool;
import infraestrucutre.Adapters.Drivens.Handlers.PoolHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class RoutersPoolTest {

    @Mock
    private PoolServiceInterface poolService;

    private WebTestClient client;

    @BeforeEach
    void setUp() {
        PoolHandler handler = new PoolHandler(poolService);
        RouterFunction<ServerResponse> routes = new RoutersPool().poolRoutes(handler);
        client = WebTestClient.bindToRouterFunction(routes).build();
    }

    private Pool sample() {
        Pool pool = new Pool();
        pool.setId(1);
        pool.setName("Olympic");
        pool.setDescription("desc");
        pool.setDateClean(LocalDate.now());
        pool.setStartDate(LocalDate.now());
        pool.setEndDate(LocalDate.now().plusMonths(1));
        return pool;
    }

    @Test
    void createPool_returns201Created() {
        Pool pool = sample();
        when(poolService.createPool(pool)).thenReturn(Mono.just(pool));

        client.post().uri("/api/pools")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(pool)
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    void getAllPools_returnsTheFullList() {
        when(poolService.getAllPools()).thenReturn(Flux.just(sample()));

        client.get().uri("/api/pools")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Pool.class).hasSize(1);
    }

    @Test
    void getPoolById_returns404_whenNotFound() {
        when(poolService.getPoolByName("Ghost")).thenReturn(Mono.empty());

        client.get().uri("/api/pools/Ghost")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void updatePool_returnsCreatedStatus_matchingHandlerImplementation() {
        // The handler intentionally (or not) responds 201 CREATED on update, not 200 OK -
        // this pins actual behavior rather than assuming a REST-conventional 200.
        Pool pool = sample();
        when(poolService.updatePoolById(1L, pool)).thenReturn(Mono.just(pool));

        client.put().uri("/api/pools/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(pool)
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    void deletePool_returns204NoContent() {
        when(poolService.deletePoolByName("Olympic")).thenReturn(Mono.empty());

        client.delete().uri("/api/pools/Olympic")
                .exchange()
                .expectStatus().isNoContent();
    }
}
