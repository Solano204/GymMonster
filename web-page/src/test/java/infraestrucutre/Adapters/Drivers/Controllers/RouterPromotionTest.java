package infraestrucutre.Adapters.Drivers.Controllers;

import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import application.Ports.Drivers.IServices.PromotionInterface;
import infraestrucutre.Adapters.Drivens.Entities.Promotion;
import infraestrucutre.Adapters.Drivens.Handlers.PromotionHandler;
import reactor.core.publisher.Flux;

@ExtendWith(MockitoExtension.class)
class RouterPromotionTest {

    @Mock
    private PromotionInterface promotionService;

    private WebTestClient client;

    @BeforeEach
    void setUp() {
        PromotionHandler handler = new PromotionHandler(promotionService);
        RouterFunction<ServerResponse> routes = new RouterPromotion().promotionRoutes(handler);
        client = WebTestClient.bindToRouterFunction(routes).build();
    }

    private Promotion sample() {
        return new Promotion(1, "Summer sale", "1 month", 20, LocalDate.now(), LocalDate.now().plusMonths(1), true);
    }

    @Test
    void getAllCurrentPromotions_readsCurrentDateFromThePathVariable() {
        when(promotionService.getAllCurrentPromotions("2026-01-01")).thenReturn(Flux.just(sample()));

        client.get().uri("/api/page/promotions/currentPromotions/2026-01-01")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Promotion.class).hasSize(1);
    }

    @Test
    void getAllPromotionsByDate_returns500_becauseTheHandlerReadsAQueryParamButTheRouteOnlyDeclaresAPathVariable() {
        // The route is GET /api/page/promotions/specificDate/{date} (a path variable), but
        // PromotionHandler.getAllPromotionsByDate reads request.queryParam("date") - a query
        // string parameter, never populated by the path variable. orElseThrow() always fires,
        // and the handler has no errorHandler wrapping around the queryParam() call itself (it's
        // thrown before the Mono chain that ServerResponse.ok().body(...) builds even starts),
        // so this 500s on every real request instead of ever reaching promotionService.
        client.get().uri("/api/page/promotions/specificDate/2026-01-01")
                .exchange()
                .expectStatus().is5xxServerError();

        org.mockito.Mockito.verifyNoInteractions(promotionService);
    }

    @Test
    void getAllPromotionsByDate_worksIfCalledWithAQueryParamInstead_confirmingTheHandlerLogicItselfIsFine() {
        when(promotionService.getAllPromotionsByDate("2026-01-01")).thenReturn(Flux.just(sample()));

        client.get().uri("/api/page/promotions/specificDate/ignored?date=2026-01-01")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Promotion.class).hasSize(1);
    }
}
