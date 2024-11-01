package infraestrucutre.Adapters.Drivens.Repositories;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import application.Ports.Drivens.RepositoriesInterfaces.PoolRepositoryInterface;
import infraestrucutre.Adapters.Drivens.DTOS.DtoPoolSent;
import infraestrucutre.Adapters.Drivens.Properties.ServicesUrl;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.MediaType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@AllArgsConstructor
public class PoolRepository implements PoolRepositoryInterface {
    
    private final WebClient.Builder webClientBuilder;
    private final ServicesUrl servicesUrl;

    @Override
    public Flux<DtoPoolSent> getAllPools() {
        return this.webClientBuilder.build()
                .get()
                .uri(servicesUrl.getInfo().getUrl() + "/api/pools")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(DtoPoolSent.class);
    }
    
    @Override
    public Mono<DtoPoolSent> createPool(DtoPoolSent pool) {
        return this.webClientBuilder.build()
                .post()
                .uri(servicesUrl.getInfo().getUrl() + "/api/pools")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(pool)
                .retrieve()
                .bodyToMono(DtoPoolSent.class);
    }
    
    @Override
    public Mono<String> updatePool(String name, DtoPoolSent updatedPool) {
        return this.webClientBuilder.build()
                .put()
                .uri(servicesUrl.getInfo().getUrl() + "/api/pools/{name}", name)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedPool)
                .retrieve()
                .bodyToMono(String.class);
    }
    
    @Override
    public Mono<String> deletePoolByName(String name) {
        return this.webClientBuilder.build()
                .delete()
                .uri(servicesUrl.getInfo().getUrl() + "/api/pools/{name}", name)
                .retrieve()
                .bodyToMono(String.class);
    }
}
