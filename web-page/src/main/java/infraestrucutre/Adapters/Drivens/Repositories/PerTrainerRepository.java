package infraestrucutre.Adapters.Drivens.Repositories;

import java.util.Collections;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;

import application.Ports.Drivens.InterfaceRepositories.PerTrainerRepositoryInteface;
import infraestrucutre.Adapters.Drivens.DTOS.DtoDetailUserReciving;
import infraestrucutre.Adapters.Drivens.DTOS.DtoInfoTrainerSent;
import infraestrucutre.Adapters.Drivens.DTOS.DtoSpecialtyRecived;
import infraestrucutre.Adapters.Drivens.DTOS.DtoSpecialtySent;
import infraestrucutre.Adapters.Drivens.DTOS.DtoTrainerData;
import infraestrucutre.Adapters.Drivens.DTOS.DtoTrainerReciving;
import infraestrucutre.Adapters.Drivens.Mappers.Mappersito;
import lombok.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Data
@Repository
@AllArgsConstructor
public class PerTrainerRepository implements PerTrainerRepositoryInteface{

    private final WebClient.Builder webClientBuilder;



    


    @Override
    // Get all PerTrainers with all information
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
    // Get specialties of a specific trainer
    public Mono<List<DtoSpecialtyRecived>> getAllSpecialtiesFromTrainer(String username) {
        return this.webClientBuilder.build()
                .get()
                .uri("http://localhost:8111/api/pertrainers/{username}/specialties", username)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<DtoSpecialtyRecived>>() {});
    }

    
    

    

}
