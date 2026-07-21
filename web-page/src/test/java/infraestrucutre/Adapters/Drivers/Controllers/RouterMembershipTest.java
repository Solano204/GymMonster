package infraestrucutre.Adapters.Drivers.Controllers;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import application.Ports.Drivers.IServices.MembershipInterface;
import infraestrucutre.Adapters.Drivens.DTOS.DtoMembershipReciving;
import infraestrucutre.Adapters.Drivens.Handlers.MembershipHandler;
import reactor.core.publisher.Flux;

@ExtendWith(MockitoExtension.class)
class RouterMembershipTest {

    @Mock
    private MembershipInterface membershipService;

    private WebTestClient client;

    @BeforeEach
    void setUp() {
        MembershipHandler handler = new MembershipHandler(membershipService);
        RouterFunction<ServerResponse> routes = new RouterMembership().membershipRoutes(handler);
        client = WebTestClient.bindToRouterFunction(routes).build();
    }

    @Test
    void getAllMemberships_returnsTheFullList() {
        when(membershipService.getAllMemberships())
                .thenReturn(Flux.just(new DtoMembershipReciving("GOLD", "desc", true, true, true)));

        client.get().uri("/api/page/allMemberships")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(DtoMembershipReciving.class).hasSize(1);
    }

    @Test
    void getAllMemberships_returns500_whenServiceFails() {
        when(membershipService.getAllMemberships()).thenReturn(Flux.error(new RuntimeException("Redis down")));

        client.get().uri("/api/page/allMemberships")
                .exchange()
                .expectStatus().is5xxServerError();
    }
}
