package infraestrucutre.Adapters.Drivens.Repositories;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import application.Ports.Drivens.RepositoriesInterfaces.MembershipRepositoryInterface;
import infraestrucutre.Adapters.Drivens.DTOS.DtoMembershipReciving;
import infraestrucutre.Adapters.Drivens.DTOS.DtoMembershipSent;
import lombok.AllArgsConstructor;
import lombok.Data;
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
public class MembershipRepository implements MembershipRepositoryInterface {

    private final WebClient.Builder webClientBuilder;

    @Override
    public Flux<DtoMembershipReciving> getAllMemberships() {
        return this.webClientBuilder.build()
                .get()
                .uri("http://localhost:8111/api/memberships")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(DtoMembershipReciving.class);
    }

    @Override
    public Mono<DtoMembershipReciving> getMembershipByType(String type) {
        return this.webClientBuilder.build()
                .get()
                .uri("http://localhost:8111/api/memberships/{type}", type)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(DtoMembershipReciving.class);
    }

    @Override
    public Mono<DtoMembershipReciving> createMembership(DtoMembershipReciving membership) {
        return this.webClientBuilder.build()
                .post()
                .uri("http://localhost:8111/api/memberships")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(membership)
                .retrieve()
                .bodyToMono(DtoMembershipReciving.class);
    }

    @Override
    public Mono<String> updateMembership(String id, DtoMembershipReciving updatedMembership) {
        return this.webClientBuilder.build()
                .put()
                .uri("http://localhost:8111/api/memberships/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedMembership)
                .retrieve()
                .bodyToMono(String.class);
    }

    @Override
    public Mono<String> deleteMembershipByType(String type) {
        return this.webClientBuilder.build()
                .delete()
                .uri("http://localhost:8111/api/memberships/{type}", type)
                .retrieve()
                .bodyToMono(String.class);
    }
}