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

import application.Ports.Drivers.IServices.SpecialtyInterface;
import infraestrucutre.Adapters.Drivens.DTOS.DtoSpecialtyRecived;
import infraestrucutre.Adapters.Drivens.Handlers.SpecialtyHandler;
import reactor.core.publisher.Flux;

@ExtendWith(MockitoExtension.class)
class RouterSpecialtyTest {

    @Mock
    private SpecialtyInterface specialtyService;

    private WebTestClient client;

    @BeforeEach
    void setUp() {
        SpecialtyHandler handler = new SpecialtyHandler(specialtyService);
        RouterFunction<ServerResponse> routes = new RouterSpecialty().specialtyRoutes(handler);
        client = WebTestClient.bindToRouterFunction(routes).build();
    }

    @Test
    void getAllSpecialties_returnsTheFullList() {
        when(specialtyService.getAllSpecialties()).thenReturn(Flux.just(new DtoSpecialtyRecived("Yoga", "desc")));

        client.get().uri("/api/page/allSpecialties")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(DtoSpecialtyRecived.class).hasSize(1);
    }

    @Test
    void getAllSpecialties_returns500_whenServiceFails() {
        when(specialtyService.getAllSpecialties()).thenReturn(Flux.error(new RuntimeException("Redis down")));

        client.get().uri("/api/page/allSpecialties")
                .exchange()
                .expectStatus().is5xxServerError();
    }
}
