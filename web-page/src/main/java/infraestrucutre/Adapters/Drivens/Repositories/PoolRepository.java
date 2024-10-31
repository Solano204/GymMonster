package infraestrucutre.Adapters.Drivens.Repositories;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;

import application.Ports.Drivens.InterfaceRepositories.PoolRepositoryInterface;
import infraestrucutre.Adapters.Drivens.DTOS.DtoPoolReciving;
import infraestrucutre.Adapters.Drivens.DTOS.DtoPoolSent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Flux;

@Repository
@Data
@AllArgsConstructor
public class PoolRepository implements PoolRepositoryInterface {

    private final WebClient.Builder webClientBuilder;


    @Override
    public Flux<DtoPoolReciving> getAllPools() {
        return this.webClientBuilder.build()
                .get()
                //.uri("lb://pool-service/api/pools")
                .uri("http://localhost:8111/api/pools")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(DtoPoolReciving.class);
    }

}
