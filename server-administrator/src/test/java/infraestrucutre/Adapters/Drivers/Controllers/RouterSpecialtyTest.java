package infraestrucutre.Adapters.Drivers.Controllers;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import application.Ports.Drivers.IServices.SpecialtyServiceInterface;
import infraestrucutre.Adapters.Drivens.DTOS.DtoSpecialtyRecived;
import infraestrucutre.Adapters.Drivens.Handlers.SpecialtyHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class RouterSpecialtyTest {

    @Mock
    private SpecialtyServiceInterface specialtyService;

    private WebTestClient client;

    @BeforeEach
    void setUp() {
        SpecialtyHandler handler = new SpecialtyHandler(specialtyService);
        RouterFunction<ServerResponse> routes = new RouterSpecialty(handler).routesSpecialty();
        client = WebTestClient.bindToRouterFunction(routes).build();
    }

    private DtoSpecialtyRecived sample() {
        return new DtoSpecialtyRecived("Yoga", "Relaxing class");
    }

    @Test
    void getAllSpecialties_returnsTheFullList() {
        when(specialtyService.getAllSpecialties()).thenReturn(Flux.just(sample()));

        client.get().uri("/api/admin/specialties")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(DtoSpecialtyRecived.class).hasSize(1);
    }

    @Test
    void getAllTrainers_returnsTheFullList() {
        when(specialtyService.getAllTrainers()).thenReturn(Flux.just(sample()));

        client.get().uri("/api/admin/trainers")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(DtoSpecialtyRecived.class).hasSize(1);
    }

    @Test
    void createSpecialty_returnsTheCreatedSpecialty() {
        DtoSpecialtyRecived specialty = sample();
        when(specialtyService.createSpecialty(specialty)).thenReturn(Mono.just(specialty));

        client.post().uri("/api/admin/specialties/create")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(specialty)
                .exchange()
                .expectStatus().isOk()
                .expectBody(DtoSpecialtyRecived.class).isEqualTo(specialty);
    }

    @Test
    void getSpecialtyByName_returnsTheMatchingSpecialty() {
        DtoSpecialtyRecived specialty = sample();
        when(specialtyService.getSpecialtyByName("Yoga")).thenReturn(Mono.just(specialty));

        client.get().uri("/api/admin/specialties/Yoga")
                .exchange()
                .expectStatus().isOk()
                .expectBody(DtoSpecialtyRecived.class).isEqualTo(specialty);
    }

    @Test
    void updateSpecialty_returnsConfirmationMessage() {
        DtoSpecialtyRecived specialty = sample();
        when(specialtyService.updateSpecialty("Yoga", specialty)).thenReturn(Mono.just("Updated"));

        client.put().uri("/api/admin/specialties/Yoga/update")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(specialty)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Updated");
    }

    @Test
    void deleteSpecialty_returnsConfirmationMessage() {
        when(specialtyService.deleteSpecialty("Yoga")).thenReturn(Mono.just("Deleted"));

        client.delete().uri("/api/admin/specialties/Yoga/delete")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Deleted");
    }

    @Test
    void deleteSpecialty_returns500_whenServiceFails() {
        when(specialtyService.deleteSpecialty("Ghost")).thenReturn(Mono.error(new RuntimeException("boom")));

        client.delete().uri("/api/admin/specialties/Ghost/delete")
                .exchange()
                .expectStatus().is5xxServerError();
    }
}
