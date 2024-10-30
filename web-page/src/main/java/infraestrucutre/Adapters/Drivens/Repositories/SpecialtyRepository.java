package infraestrucutre.Adapters.Drivens.Repositories;

import java.util.Collections;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;

import application.Ports.Drivens.InterfaceRepositories.SpecialtyRepositoryInterface;
import infraestrucutre.Adapters.Drivens.DTOS.DtoSpecialtyRecived;
import infraestrucutre.Adapters.Drivens.DTOS.DtoSpecialtySent;
import infraestrucutre.Adapters.Drivens.Mappers.Mappersito;
import lombok.*;
import reactor.core.publisher.Flux;

@Repository
@AllArgsConstructor
@Data
public class SpecialtyRepository implements SpecialtyRepositoryInterface{
    
        private final WebClient.Builder webClientBuilder;
        

        @Override
        public Flux<DtoSpecialtyRecived> getAllSpecialties() {
        return this.webClientBuilder.build()
                .get()
                // .uri("lb://specialty-service/api/pertrainers/{username}/specialties",
                // Collections.singletonMap("username", username))
                .uri("http://localhost:8111/api/specialties")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(DtoSpecialtySent.class)
                .map(Mappersito.INSTANCE::toDtoSpecialtyRecived );
    }

}
