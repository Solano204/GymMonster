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

import application.Ports.Drivers.IServices.EquipamentServiceInterface;
import infraestrucutre.Adapters.Drivens.Entities.Equipament;
import infraestrucutre.Adapters.Drivens.Handlers.EquipamentHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class EquipamentRouterTest {

    @Mock
    private EquipamentServiceInterface equipamentService;

    private WebTestClient client;

    @BeforeEach
    void setUp() {
        EquipamentHandler handler = new EquipamentHandler(equipamentService);
        RouterFunction<ServerResponse> routes = new EquipamentRouter().equipamentRoutes(handler);
        client = WebTestClient.bindToRouterFunction(routes).build();
    }

    private Equipament sampleEquipament() {
        return new Equipament(1L, "Treadmill", "Cardio machine", LocalDate.now(), LocalDate.now().plusYears(5), "NEW");
    }

    @Test
    void createEquipament_returnsCreatedEquipament() {
        Equipament equipament = sampleEquipament();
        when(equipamentService.createEquipament(equipament)).thenReturn(Mono.just(equipament));

        client.post().uri("/api/admin/equipaments/create")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(equipament)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Equipament.class).isEqualTo(equipament);
    }

    @Test
    void getAllEquipaments_returnsTheFullList() {
        when(equipamentService.getAllEquipaments()).thenReturn(Flux.just(sampleEquipament()));

        client.get().uri("/api/admin/equipaments/all")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Equipament.class).hasSize(1);
    }

    @Test
    void getEquipamentByName_returnsTheMatchingEquipament() {
        Equipament equipament = sampleEquipament();
        when(equipamentService.getEquipamentByName("Treadmill")).thenReturn(Mono.just(equipament));

        client.get().uri("/api/admin/equipaments/Treadmill")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Equipament.class).isEqualTo(equipament);
    }

    @Test
    void getEquipamentByName_returns404_whenNotFound() {
        when(equipamentService.getEquipamentByName("Ghost")).thenReturn(
                Mono.error(WebClientResponseException.create(
                        HttpStatus.NOT_FOUND.value(), "Not Found", HttpHeaders.EMPTY, new byte[0], null)));

        client.get().uri("/api/admin/equipaments/Ghost")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404);
    }

    @Test
    void updateEquipament_returnsTheUpdatedEquipament() {
        Equipament equipament = sampleEquipament();
        when(equipamentService.updateEquipament(1L, equipament)).thenReturn(Mono.just(equipament));

        client.put().uri("/api/admin/equipaments/update/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(equipament)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Equipament.class).isEqualTo(equipament);
    }

    @Test
    void deleteEquipamentByName_returnsConfirmationMessage() {
        when(equipamentService.deleteEquipamentByName("Treadmill")).thenReturn(Mono.just("Deleted"));

        client.delete().uri("/api/admin/equipaments/delete/Treadmill")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Deleted");
    }

    @Test
    void deleteEquipamentByName_returns500_onUnexpectedFailure() {
        when(equipamentService.deleteEquipamentByName("Treadmill"))
                .thenReturn(Mono.error(new RuntimeException("downstream unreachable")));

        client.delete().uri("/api/admin/equipaments/delete/Treadmill")
                .exchange()
                .expectStatus().is5xxServerError();
    }
}
