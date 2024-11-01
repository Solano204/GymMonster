package infraestrucutre.Adapters.Drivens.Repositories;

import java.util.Collections;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;

import application.Ports.Drivens.InterfaceRepositories.WorkClassRepositoryInterface;
import infraestrucutre.Adapters.Drivens.DTOS.DtoDetailUserReciving;
import infraestrucutre.Adapters.Drivens.DTOS.DtoDetailUserSent;
import infraestrucutre.Adapters.Drivens.Entities.Schedule;
import infraestrucutre.Adapters.Drivens.Entities.WorkClass;
import infraestrucutre.Adapters.Drivens.Properties.ServicesUrl;
import lombok.*;
import reactor.core.publisher.Flux;

@Repository
@Data
public class WorkClassRepository implements WorkClassRepositoryInterface {

    private final WebClient.Builder webClientBuilder;

    private final ServicesUrl servicesUrl;

@Override
public Flux<WorkClass> getAllWorkClasses() {
    return this.webClientBuilder.build()
            .get()
            .uri(servicesUrl.getInfo().getUrl() + "/api/workclasses")  // Use servicesUrl for base URL
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToFlux(WorkClass.class);
}

@Override
public Flux<DtoDetailUserReciving> getClientsByWorkClassWithPagination(String className, int page, int size) {
    return this.webClientBuilder.build()
            .get()
            .uri(uriBuilder -> uriBuilder
                    .path(servicesUrl.getInfo().getUrl() + "/api/workclasses/{name}/clients")  // Use servicesUrl for base URL
                    .queryParam("page", page)  // Add pagination query parameters
                    .queryParam("size", size)
                    .build(Collections.singletonMap("name", className)))  // Dynamically pass class name
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToFlux(DtoDetailUserReciving.class);
}

@Override
public Flux<DtoDetailUserReciving> getTrainersByWorkClassWithPagination(String name, int page, int size) {
    return this.webClientBuilder.build()
            .get()
            .uri(uriBuilder -> uriBuilder
                    .path(servicesUrl.getInfo().getUrl() + "/api/workclasses/{name}/trainers")  // Use servicesUrl for base URL
                    .queryParam("page", page)  // Add pagination query parameters
                    .queryParam("size", size)
                    .build(Collections.singletonMap("name", name)))  // Dynamically pass class name
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToFlux(DtoDetailUserReciving.class);
}

@Override
public Flux<Schedule> getCSchedulesByWorkClassWithPagination(String className) {
    return this.webClientBuilder.build()
            .get()
            .uri(uriBuilder -> uriBuilder
                    .path(servicesUrl.getInfo().getUrl() + "/api/workclasses/{name}/schedules")  // Use servicesUrl for base URL
                    .build(Collections.singletonMap("name", className)))  // Dynamically pass class name
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToFlux(Schedule.class);
}
    

}
