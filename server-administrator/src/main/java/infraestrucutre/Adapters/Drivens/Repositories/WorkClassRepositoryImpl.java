package infraestrucutre.Adapters.Drivens.Repositories;

import java.util.Collections;
import java.util.List;
import infraestrucutre.Adapters.Drivens.Entities.DetailsUser;
import infraestrucutre.Adapters.Drivens.Entities.Schedule;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.password.CompromisedPasswordDecision;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiReactivePasswordChecker;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;

import application.Ports.Drivens.RepositoriesInterfaces.WorkClassRepositoryInterface;
import application.Ports.Drivens.RepositoriesInterfaces.WorkTrainerRepositoryInterface;
import infraestrucutre.Adapters.Drivens.DTOS.DtoTrainerData;
import infraestrucutre.Adapters.Drivens.DTOS.DtoDetailUserReciving;
import infraestrucutre.Adapters.Drivens.DTOS.DtoDetailUserSent;
import infraestrucutre.Adapters.Drivens.DTOS.DtoSpecialtyRecived;
import infraestrucutre.Adapters.Drivens.Entities.AllTrainer;
import infraestrucutre.Adapters.Drivens.Entities.WorkClass;
import lombok.AllArgsConstructor;
import lombok.Data;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import lombok.AllArgsConstructor;
import lombok.Data;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import lombok.AllArgsConstructor;
import lombok.Data;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
@AllArgsConstructor
public class WorkClassRepositoryImpl implements WorkClassRepositoryInterface {

    private final WebClient.Builder webClientBuilder;

    @Override
    public Flux<WorkClass> getAllWorkClasses() {
        return this.webClientBuilder.build()
                .get()
                .uri("http://localhost:8111/api/workclasses")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(WorkClass.class);
    }

    @Override
    public Mono<WorkClass> createWorkclass(WorkClass workClass) {
        return this.webClientBuilder.build()
                .post()
                .uri("http://localhost:8111/api/workclasses")
                .body(Mono.just(workClass), WorkClass.class)
                .retrieve()
                .bodyToMono(WorkClass.class);
    }

    @Override
    public Mono<WorkClass> updateWorkClass(WorkClass workClass, String name) {
        return this.webClientBuilder.build()
                .put()
                .uri("http://localhost:8111/api/workclasses/{name}", name)
                .body(Mono.just(workClass), WorkClass.class)
                .retrieve()
                .bodyToMono(WorkClass.class);
    }

    @Override
    public Mono<String> deleteWorkClass(String name) {
        return this.webClientBuilder.build()
                .delete()
                .uri("http://localhost:8111/api/workclasses/{name}", name)
                .retrieve()
                .bodyToMono(String.class);
    }

    @Override
    public Flux<DtoDetailUserReciving> getClientsByWorkClassWithPagination(String className, int page, int size) {
        return this.webClientBuilder.build()
                .get()
                .uri(uriBuilder -> uriBuilder
                    .scheme("http")
                    .host("localhost")
                    .port(8111)
                    .path("/api/workclasses/{name}/clients")
                    .queryParam("page", page)
                    .queryParam("size", size)
                    .build(Collections.singletonMap("name", className)))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(DtoDetailUserReciving.class);
    }

    @Override
    public Flux<DtoDetailUserReciving> getTrainersByWorkClassWithPagination(String name, int page, int size) {
        return this.webClientBuilder.build()
                .get()
                .uri(uriBuilder -> uriBuilder
                    .scheme("http")
                    .host("localhost")
                    .port(8111)
                    .path("/api/workclasses/{name}/trainers")
                    .queryParam("page", page)
                    .queryParam("size", size)
                    .build(Collections.singletonMap("name", name)))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(DtoDetailUserReciving.class);
    }

    @Override
    public Flux<Schedule> getCSchedulesByWorkClassWithPagination(String className) {
        return this.webClientBuilder.build()
                .get()
                .uri(uriBuilder -> uriBuilder
                    .scheme("http")
                    .host("localhost")
                    .port(8111)
                    .path("/api/workclasses/{name}/schedules")
                    .build(Collections.singletonMap("name", className)))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(Schedule.class);
    }
}
