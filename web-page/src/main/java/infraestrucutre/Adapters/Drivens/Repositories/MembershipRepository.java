package infraestrucutre.Adapters.Drivens.Repositories;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;

import application.Ports.Drivens.InterfaceRepositories.MembershipRepositoryInterface;
import application.Ports.Drivers.IServices.MembershipInterface;
import infraestrucutre.Adapters.Drivens.DTOS.DtoMembershipReciving;
import infraestrucutre.Adapters.Drivens.DTOS.DtoMembershipSent;
import infraestrucutre.Adapters.Drivens.Properties.ServicesUrl;
import lombok.AllArgsConstructor;
import lombok.Data;
import reactor.core.publisher.Flux;

@Repository
@Data
@AllArgsConstructor
public class MembershipRepository implements MembershipRepositoryInterface {

    private final WebClient.Builder webClientBuilder;
    private final ServicesUrl servicesUrl;

    @Override
public Flux<DtoMembershipReciving> getAllMemberships() {
    return this.webClientBuilder.build()
            .get()
            .uri(servicesUrl.getInfo().getUrl() + "/api/memberships") // Use servicesUrl here
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToFlux(DtoMembershipReciving.class);
}

}
