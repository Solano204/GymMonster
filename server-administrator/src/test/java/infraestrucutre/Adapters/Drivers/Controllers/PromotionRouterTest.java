package infraestrucutre.Adapters.Drivers.Controllers;

import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import infraestrucutre.Adapters.Drivens.Entities.Promotion;
import infraestrucutre.Adapters.Drivens.Handlers.PromotionHandler;
import infraestrucutre.Adapters.Drivens.ImpServices.PromotionService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * PromotionHandler wires createPromotion/updatePromotion/deletePromotionById WITHOUT the
 * shared errorHandler (unlike the 4 GET routes) - so those 3 routes surface any downstream
 * failure as WebFlux's default error response, not the handler's structured JSON body. The
 * "returns500_plainly" tests below pin that asymmetry as current behavior.
 */
@ExtendWith(MockitoExtension.class)
class PromotionRouterTest {

    @Mock
    private PromotionService promotionService;

    private WebTestClient client;

    @BeforeEach
    void setUp() {
        PromotionHandler handler = new PromotionHandler(promotionService);
        RouterFunction<ServerResponse> routes = new PromotionRouter().promotionRoutes(handler);
        client = WebTestClient.bindToRouterFunction(routes).build();
    }

    private Promotion sample() {
        return new Promotion(1, "Summer sale", "1 month", 20, LocalDate.now(), LocalDate.now().plusMonths(1), true);
    }

    @Test
    void getAllPromotions_returnsTheFullList() {
        when(promotionService.getAllPromotions()).thenReturn(Flux.just(sample()));

        client.get().uri("/api/admin/promotions")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Promotion.class).hasSize(1);
    }

    @Test
    void getAllCurrentPromotions_parsesDatePathVariable_andReturnsMatches() {
        when(promotionService.getPromotionsByCurrentDate(LocalDate.of(2026, 1, 1)))
                .thenReturn(Flux.just(sample()));

        client.get().uri("/api/admin/promotions/current/2026-01-01")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Promotion.class).hasSize(1);
    }

    @Test
    void getAllPromotionsByStartDate_parsesDatePathVariable() {
        when(promotionService.getPromotionsByStartDate(LocalDate.of(2026, 1, 1)))
                .thenReturn(Flux.just(sample()));

        client.get().uri("/api/admin/promotions/start/2026-01-01")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Promotion.class).hasSize(1);
    }

    @Test
    void getAllPromotionsByEndDate_parsesDatePathVariable() {
        when(promotionService.getPromotionsByEndDate(LocalDate.of(2026, 1, 1)))
                .thenReturn(Flux.just(sample()));

        client.get().uri("/api/admin/promotions/end/2026-01-01")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Promotion.class).hasSize(1);
    }

    @Test
    void getAllCurrentPromotions_returns404_whenServiceReportsNotFound() {
        when(promotionService.getPromotionsByCurrentDate(LocalDate.of(2026, 1, 1))).thenReturn(
                Flux.error(WebClientResponseException.create(
                        HttpStatus.NOT_FOUND.value(), "Not Found", HttpHeaders.EMPTY, new byte[0], null)));

        client.get().uri("/api/admin/promotions/current/2026-01-01")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404);
    }

    @Test
    void createPromotion_returnsTheCreatedPromotion() {
        Promotion promotion = sample();
        when(promotionService.createPromotion(promotion)).thenReturn(Mono.just(promotion));

        client.post().uri("/api/admin/promotions")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(promotion)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Promotion.class).isEqualTo(promotion);
    }

    @Test
    void createPromotion_returns500Plainly_whenServiceFails_becauseNoErrorHandlerIsWired() {
        Promotion promotion = sample();
        when(promotionService.createPromotion(promotion)).thenReturn(Mono.error(new RuntimeException("boom")));

        client.post().uri("/api/admin/promotions")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(promotion)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void updatePromotion_returnsServiceResponse() {
        Promotion promotion = sample();
        when(promotionService.updatePromotion(1, promotion)).thenReturn(Mono.just("Updated"));

        client.put().uri("/api/admin/promotions/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(promotion)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Updated");
    }

    @Test
    void deletePromotionById_returnsServiceResponse() {
        when(promotionService.deletePromotionById(1)).thenReturn(Mono.just("Deleted"));

        client.delete().uri("/api/admin/promotions/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Deleted");
    }
}
