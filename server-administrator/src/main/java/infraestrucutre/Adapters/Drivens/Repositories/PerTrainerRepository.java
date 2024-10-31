package infraestrucutre.Adapters.Drivens.Repositories;

import java.util.Collections;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiReactivePasswordChecker;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;

import application.Ports.Drivens.RepositoriesInterfaces.PerTrainerRepositoryInterface;
import application.Ports.Drivers.IServices.PerTrainerInterface;
import infraestrucutre.Adapters.Drivens.DTOS.DtoTrainerData;
import infraestrucutre.Adapters.Drivens.DTOS.DtoDetailUserReciving;
import infraestrucutre.Adapters.Drivens.DTOS.DtoSpecialtyRecived;
import infraestrucutre.Adapters.Drivens.Entities.AllTrainer;
import lombok.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import lombok.AllArgsConstructor;

@Repository
@AllArgsConstructor
public class PerTrainerRepository implements PerTrainerRepositoryInterface {

    private final WebClient.Builder webClientBuilder;
    private final HaveIBeenPwnedRestApiReactivePasswordChecker passwordChecker;

    @Override
    public Mono<String> createPerTrainer(AllTrainer newTrainer) {
        return this.webClientBuilder.build()
                .post()
                .uri("http://localhost:8111/api/pertrainers/AD/create")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(newTrainer)
                .retrieve()
                .bodyToMono(String.class);
    }

    @Override
    public Flux<DtoTrainerData> getAllTrainers(int page, int size) {
        return this.webClientBuilder.build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("localhost")
                        .port(8111)
                        .path("/api/pertrainers/allInformation")
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(DtoTrainerData.class);
    }

    @Override
    public Mono<String> deletePerTrainer(String username, String password) {
        return this.webClientBuilder.build()
                .delete()
                .uri("http://localhost:8111/api/pertrainers/AD/{username}/{password}/delete", username, password)
                .retrieve()
                .bodyToMono(String.class);
    }

    @Override
    public Flux<DtoDetailUserReciving> getAllClientsFromTrainer(String username, int page, int size) {
        return this.webClientBuilder.build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("localhost")
                        .port(8111)
                        .path("/api/pertrainers/AllClients/{username}")
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build(username))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(DtoDetailUserReciving.class);
    }

    @Override
    public Mono<List<DtoSpecialtyRecived>> getAllSpecialtiesFromTrainer(String username) {
        return this.webClientBuilder.build()
                .get()
                .uri("http://localhost:8111/api/pertrainers/{username}/specialties", username)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<DtoSpecialtyRecived>>() {});
    }

    @Override
    public Mono<String> updateTrainerPassword(String username, String newPassword, String oldPassword) {
        return this.webClientBuilder.build()
                .put()
                .uri("http://localhost:8111/api/pertrainers/AD/{username}/{newPassword}/{oldPassword}/updatePassword", username, newPassword, oldPassword)
                .retrieve()
                .bodyToMono(String.class);
    }

    @Override
    public Mono<String> updateEmail(String username, String newEmail) {
        return this.webClientBuilder.build()
                .put()
                .uri("http://localhost:8111/api/pertrainers/AD/{username}/{newEmail}/updateEmail", username, newEmail)
                .retrieve()
                .bodyToMono(String.class);
    }

    @Override
    public Mono<String> updateTrainerUsername(String oldUsername, String newUsername) {
        return this.webClientBuilder.build()
                .put()
                .uri("http://localhost:8111/api/pertrainers/AD/{oldUsername}/{newUsername}/updateUsername", oldUsername, newUsername)
                .retrieve()
                .bodyToMono(String.class);
    }

    @Override
    public Mono<String> updateTrainerDetails(String username, DtoDetailUserReciving details) {
        return this.webClientBuilder.build()
                .put()
                .uri("http://localhost:8111/api/pertrainers/{username}/AD/updateBasicInformation", username)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(details)
                .retrieve()
                .bodyToMono(String.class);
    }

    @Override
    public Mono<String> addSpecialtyToTrainer(String username, String newSpecialty) {
        return this.webClientBuilder.build()
                .post()
                .uri("http://localhost:8111/api/pertrainers/AD/{username}/{newSpecialty}/assignSpecialty", username, newSpecialty)
                .retrieve()
                .bodyToMono(String.class);
    }

    @Override
    public Mono<String> removeSpecialtyFromTrainer(String username, String specialty) {
        return this.webClientBuilder.build()
                .delete()
                .uri("http://localhost:8111/api/pertrainers/AD/{username}/{newSpecialty}/dessingSpecialty", username, specialty)
                .retrieve()
                .bodyToMono(String.class);
    }
}
