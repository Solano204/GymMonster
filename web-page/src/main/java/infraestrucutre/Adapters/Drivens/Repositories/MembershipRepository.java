package infraestrucutre.Adapters.Drivens.Repositories;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;

import application.Ports.Drivens.InterfaceRepositories.MembershipRepositoryInterface;
import application.Ports.Drivers.IServices.MembershipInterface;
import infraestrucutre.Adapters.Drivens.DTOS.DtoMembershipReciving;
import infraestrucutre.Adapters.Drivens.DTOS.DtoMembershipSent;
import lombok.AllArgsConstructor;
import lombok.Data;
import reactor.core.publisher.Flux;

@Repository
@Data
@AllArgsConstructor
public class MembershipRepository implements MembershipRepositoryInterface {

    private final WebClient.Builder webClientBuilder;

    @Override
    public Flux<DtoMembershipReciving> getAllMemberships() {
    return this.webClientBuilder.build()
            .get()
            // .uri("lb://membership-service/api/memberships")
            .uri("http://localhost:8111/api/memberships")
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToFlux(DtoMembershipReciving.class);
        }   
}
