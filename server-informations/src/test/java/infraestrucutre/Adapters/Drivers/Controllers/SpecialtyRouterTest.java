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
import infraestrucutre.Adapters.Drivens.Entities.Specialty;
import infraestrucutre.Adapters.Drivens.Handlers.SpecialtyHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class SpecialtyRouterTest {

    @Mock
    private SpecialtyServiceInterface specialtyService;

    private WebTestClient client;

    @BeforeEach
    void setUp() {
        SpecialtyHandler handler = new SpecialtyHandler(specialtyService);
        RouterFunction<ServerResponse> routes = new SpecialtyRouter().specialtyRoutes(handler);
        client = WebTestClient.bindToRouterFunction(routes).build();
    }

    private Specialty sample() {
        return new Specialty(1L, "Yoga", "desc");
    }

    @Test
    void createSpecialty_returns201Created() {
        Specialty specialty = sample();
        when(specialtyService.createSpecialty(specialty)).thenReturn(Mono.just(specialty));

        client.post().uri("/api/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(specialty)
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    void getAllSpecialties_returnsTheFullList() {
        when(specialtyService.getAllSpecialties()).thenReturn(Flux.just(sample()));

        client.get().uri("/api/specialties")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Specialty.class).hasSize(1);
    }

    @Test
    void getSpecialtyByName_returns404_whenNotFound() {
        when(specialtyService.getSpecialtyByName("Ghost")).thenReturn(Mono.empty());

        client.get().uri("/api/specialties/Ghost")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void updateSpecialty_returnsTheUpdatedSpecialty() {
        Specialty specialty = sample();
        when(specialtyService.updateSpecialtyName("Yoga", specialty)).thenReturn(Mono.just(specialty));

        client.put().uri("/api/specialties/Yoga")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(specialty)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void deleteSpecialtyName_returns204NoContent() {
        when(specialtyService.deleteSpecialtyByName("Yoga")).thenReturn(Mono.empty());

        client.delete().uri("/api/specialties/Yoga")
                .exchange()
                .expectStatus().isNoContent();
    }
}
