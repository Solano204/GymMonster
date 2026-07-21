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

import application.Ports.Drivers.IServices.MembershipServiceInterface;
import infraestrucutre.Adapters.Drivens.DTOS.DtoMembershipReciving;
import infraestrucutre.Adapters.Drivens.Handlers.MembershipHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class RouterMembershipTest {

    @Mock
    private MembershipServiceInterface membershipService;

    private WebTestClient client;

    @BeforeEach
    void setUp() {
        MembershipHandler handler = new MembershipHandler(membershipService);
        RouterFunction<ServerResponse> routes = new RouterMembership().membershipRoutes(handler);
        client = WebTestClient.bindToRouterFunction(routes).build();
    }

    private DtoMembershipReciving sample() {
        return new DtoMembershipReciving("GOLD", "Full access", true, true, true);
    }

    @Test
    void getAllMemberships_returnsTheFullList() {
        when(membershipService.getAllMemberships()).thenReturn(Flux.just(sample()));

        client.get().uri("/api/admin/memberships")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(DtoMembershipReciving.class).hasSize(1);
    }

    @Test
    void createMembership_returnsTheCreatedMembership() {
        DtoMembershipReciving membership = sample();
        when(membershipService.createMembership(membership)).thenReturn(Mono.just(membership));

        client.post().uri("/api/admin/memberships")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(membership)
                .exchange()
                .expectStatus().isOk()
                .expectBody(DtoMembershipReciving.class).isEqualTo(membership);
    }

    @Test
    void createMembership_returns500_whenServiceFails() {
        DtoMembershipReciving membership = sample();
        when(membershipService.createMembership(membership)).thenReturn(Mono.error(new RuntimeException("boom")));

        client.post().uri("/api/admin/memberships")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(membership)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void updateMembership_returnsConfirmationMessage() {
        DtoMembershipReciving membership = sample();
        when(membershipService.updateMembership("5", membership)).thenReturn(Mono.just("Updated"));

        client.put().uri("/api/admin/memberships/5")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(membership)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Updated");
    }

    @Test
    void deleteMembershipByType_returnsConfirmationMessage() {
        when(membershipService.deleteMembershipByType("GOLD")).thenReturn(Mono.just("Deleted"));

        client.delete().uri("/api/admin/memberships/GOLD")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Deleted");
    }
}
