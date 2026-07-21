package infraestrucutre.Adapters.Drivers.Controllers;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import infraestrucutre.Adapters.Drivens.Entities.Membership;
import infraestrucutre.Adapters.Drivens.Handlers.MembershipHandler;
import infraestrucutre.Adapters.Drivens.ImpServices.MembershipService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class RoutersMemberTest {

    @Mock
    private MembershipService membershipService;

    private WebTestClient client;

    @BeforeEach
    void setUp() {
        MembershipHandler handler = new MembershipHandler(membershipService);
        RouterFunction<ServerResponse> routes = new RoutersMember().membershipRoutes(handler);
        client = WebTestClient.bindToRouterFunction(routes).build();
    }

    private Membership sample() {
        return new Membership(1L, "GOLD", "desc", true, true, true, "49.99");
    }

    @Test
    void createMembership_returns201Created() {
        Membership membership = sample();
        when(membershipService.createMembership(membership)).thenReturn(Mono.just(membership));

        client.post().uri("/api/memberships")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(membership)
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    void getAllMemberships_returnsTheFullList() {
        when(membershipService.getAllMemberships()).thenReturn(Flux.just(sample()));

        client.get().uri("/api/memberships")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Membership.class).hasSize(1);
    }

    @Test
    void getMembershipByType_returns404_whenNotFound() {
        when(membershipService.getMembershipByName("GHOST")).thenReturn(Mono.empty());

        client.get().uri("/api/memberships/GHOST")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void updateMembership_returnsTheUpdatedMembership() {
        Membership membership = sample();
        when(membershipService.updateMembership(1L, membership)).thenReturn(Mono.just(membership));

        client.put().uri("/api/memberships/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(membership)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void deleteMembershipByType_returns204NoContent() {
        when(membershipService.deleteMembership("GOLD")).thenReturn(Mono.empty());

        client.delete().uri("/api/memberships/GOLD")
                .exchange()
                .expectStatus().isNoContent();
    }
}
