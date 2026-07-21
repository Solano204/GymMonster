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

import application.Ports.Drivers.IServices.EquipamentServiceInterface;
import infraestrucutre.Adapters.Drivens.Entities.Equipament;
import infraestrucutre.Adapters.Drivens.Handlers.EquipamentHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class RoutersEquipamentTest {

    @Mock
    private EquipamentServiceInterface equipamentService;

    private WebTestClient client;

    @BeforeEach
    void setUp() {
        EquipamentHandler handler = new EquipamentHandler(equipamentService);
        RouterFunction<ServerResponse> routes = new RoutersEquipament().equiRoutes(handler);
        client = WebTestClient.bindToRouterFunction(routes).build();
    }

    private Equipament sample() {
        return new Equipament(1L, "Treadmill", "desc", LocalDate.now(), LocalDate.now().plusYears(5), "NEW");
    }

    @Test
    void createEquipament_returns201Created() {
        Equipament equipament = sample();
        when(equipamentService.createEquipament(equipament)).thenReturn(Mono.just(equipament));

        client.post().uri("/api/equipaments")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(equipament)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Equipament.class).isEqualTo(equipament);
    }

    @Test
    void getAllEquipaments_returnsTheFullList() {
        when(equipamentService.getAllEquipaments()).thenReturn(Flux.just(sample()));

        client.get().uri("/api/equipaments")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Equipament.class).hasSize(1);
    }

    @Test
    void getEquipamentByName_returns404_whenNotFound() {
        when(equipamentService.getEquipamentByName("Ghost")).thenReturn(Mono.empty());

        client.get().uri("/api/equipaments/Ghost")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void updateEquipament_returnsTheUpdatedEquipament() {
        Equipament equipament = sample();
        when(equipamentService.updateEquipament(1L, equipament)).thenReturn(Mono.just(equipament));

        client.put().uri("/api/equipaments/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(equipament)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Equipament.class).isEqualTo(equipament);
    }

    @Test
    void deleteEquipamentByName_returns204NoContent() {
        when(equipamentService.deleteEquipament("Treadmill")).thenReturn(Mono.empty());

        client.delete().uri("/api/equipaments/Treadmill")
                .exchange()
                .expectStatus().isNoContent();
    }
}
