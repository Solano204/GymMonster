package infraestrucutre.Adapters.Drivens.Repositories;

import java.util.Collections;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;

import application.Ports.Drivens.RepositoriesInterfaces.SpecialtyRepositoryInterface;
import infraestrucutre.Adapters.Drivens.DTOS.DtoSpecialtyRecived;
import lombok.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@AllArgsConstructor
public class SpecialtyRepository implements SpecialtyRepositoryInterface {

    private final WebClient.Builder webClientBuilder;

    @Override
    public Flux<DtoSpecialtyRecived> getAllSpecialties() {
        return this.webClientBuilder.build()
                .get()
                .uri("http://localhost:8111/api/specialties")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(DtoSpecialtyRecived.class);
    }

    @Override
    public Flux<DtoSpecialtyRecived> getAllTrainers() {
        return this.webClientBuilder.build()
                .get()
                .uri("http://localhost:8111/api/specialties/allTrainers")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(DtoSpecialtyRecived.class);
    }

    @Override
    public Mono<DtoSpecialtyRecived> createSpecialty(DtoSpecialtyRecived specialty) {
        return this.webClientBuilder.build()
                .post()
                .uri("http://localhost:8111/api/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(specialty)
                .retrieve()
                .bodyToMono(DtoSpecialtyRecived.class);
    }

    @Override
    public Mono<DtoSpecialtyRecived> getSpecialtyByName(String name) {
        return this.webClientBuilder.build()
                .get()
                .uri("http://localhost:8111/api/specialties/{name}", name)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(DtoSpecialtyRecived.class);
    }

    @Override
    public Mono<String> updateSpecialty(String name, DtoSpecialtyRecived updatedSpecialty) {
        return this.webClientBuilder.build()
                .put()
                .uri("http://localhost:8111/api/specialties/{name}", name)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedSpecialty)
                .retrieve()
                .bodyToMono(String.class);
    }

    @Override
    public Mono<String> deleteSpecialty(String name) {
        return this.webClientBuilder.build()
                .delete()
                .uri("http://localhost:8111/api/specialties/{name}", name)
                .retrieve()
                .bodyToMono(String.class);
    }
}