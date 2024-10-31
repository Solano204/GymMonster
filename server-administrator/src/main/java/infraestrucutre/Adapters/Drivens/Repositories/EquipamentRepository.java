package infraestrucutre.Adapters.Drivens.Repositories;

import lombok.Data;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import application.Ports.Drivens.RepositoriesInterfaces.EquipamentRepositoryInterface;
import infraestrucutre.Adapters.Drivens.Entities.Equipament;
@Data
@Repository
public class EquipamentRepository implements EquipamentRepositoryInterface {

    private final WebClient.Builder webClientBuilder;

    // Create a new equipament

    @Override
    public Mono<Equipament> createEquipament(Equipament equipament) {
        return this.webClientBuilder.build()
                .post()
                .uri("http://localhost:8111/api/equipaments")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(equipament)
                .retrieve()
                .bodyToMono(Equipament.class);
    }

    // Get all equipaments
    @Override
    public Flux<Equipament> getAllEquipaments() {
        return this.webClientBuilder.build()
                .get()
                .uri("http://localhost:8111/api/equipaments")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(Equipament.class);
    }

    // Get equipament by name
    @Override
    public Mono<Equipament> getEquipamentByName(String name) {
        return this.webClientBuilder.build()
                .get()
                .uri("http://localhost:8111/api/equipaments/{name}", name)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Equipament.class);
    }

    // Update equipament by I
    @Override
    public Mono<Equipament> updateEquipament(Long id, Equipament updatedEquipament) {
        return this.webClientBuilder.build()
                .put()
                .uri("http://localhost:8111/api/equipaments/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedEquipament)
                .retrieve()
                .bodyToMono(Equipament.class);
    }

    // Delete equipament by name
    @Override
    public Mono<String> deleteEquipamentByName(String name) {
        return this.webClientBuilder.build()
                .delete()
                .uri("http://localhost:8111/api/equipaments/{name}", name)
                .retrieve()
                .bodyToMono(String.class);
    }
}