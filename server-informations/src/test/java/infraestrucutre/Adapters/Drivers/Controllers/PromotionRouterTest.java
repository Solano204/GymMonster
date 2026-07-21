package infraestrucutre.Adapters.Drivers.Controllers;

import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import application.Ports.Drivers.IServices.PromotionServiceInterface;
import infraestrucutre.Adapters.Drivens.Entities.Promotion;
import infraestrucutre.Adapters.Drivens.Handlers.PromotionHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class PromotionRouterTest {

    @Mock
    private PromotionServiceInterface promotionService;

    private WebTestClient client;

    @BeforeEach
    void setUp() {
        PromotionHandler handler = new PromotionHandler(promotionService);
        RouterFunction<ServerResponse> routes = new PromotionRouter().promotionsRouterFunction(handler);
        client = WebTestClient.bindToRouterFunction(routes).build();
    }

    private Promotion sample() {
        Promotion promotion = new Promotion();
        promotion.setId(1);
        promotion.setDescription("Summer sale");
        promotion.setDuration("1 month");
        promotion.setPercentageDiscount(20);
        promotion.setStartDate(LocalDate.of(2026, 6, 1));
        promotion.setEndDate(LocalDate.of(2026, 7, 1));
        promotion.setActive(true);
        return promotion;
    }

    @Test
    void getAllPromotions_returnsTheFullList() {
        when(promotionService.getAllPromotions()).thenReturn(Flux.just(sample()));

        client.get().uri("/api/promotions")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Promotion.class).hasSize(1);
    }

    @Test
    void getPromotionStartDate_parsesIsoDatePathVariable() {
        when(promotionService.getPromotionByStartDate(LocalDate.of(2026, 6, 1))).thenReturn(Flux.just(sample()));

        client.get().uri("/api/promotions/startDate/2026-06-01")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Promotion.class).hasSize(1);
    }

    @Test
    void getPromotionStartDate_returns404_whenNoMatches() {
        when(promotionService.getPromotionByStartDate(LocalDate.of(2026, 6, 1))).thenReturn(Flux.empty());

        client.get().uri("/api/promotions/startDate/2026-06-01")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void getPromotionEndDate_parsesIsoDatePathVariable() {
        when(promotionService.getPromotionByEndDate(LocalDate.of(2026, 7, 1))).thenReturn(Flux.just(sample()));

        client.get().uri("/api/promotions/endDate/2026-07-01")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Promotion.class).hasSize(1);
    }

    @Test
    void getPromotionCurrentDate_actuallyDelegatesToStartDateLookup() {
        // getPromotionCurrentDate calls getPromotionByStartDate, not a "current date" query -
        // pins this as the real (likely unintended) behavior rather than assuming it filters
        // active promotions as of "now".
        when(promotionService.getPromotionByStartDate(LocalDate.of(2026, 6, 1))).thenReturn(Flux.just(sample()));

        client.get().uri("/api/promotions/CurrentDate/2026-06-01")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Promotion.class).hasSize(1);
    }

    @Test
    void createPromotion_returns201Created() {
        Promotion promotion = sample();
        when(promotionService.createPromotion(promotion)).thenReturn(Mono.just(promotion));

        client.post().uri("/api/promotions")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(promotion)
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    void updatePromotion_returnsTheUpdatedPromotion() {
        Promotion promotion = sample();
        when(promotionService.updatePromotion(1, promotion)).thenReturn(Mono.just(promotion));

        client.put().uri("/api/promotions/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(promotion)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void deletePromotion_returns204NoContent() {
        when(promotionService.deletePromotion(1)).thenReturn(Mono.empty());

        client.delete().uri("/api/promotions/1")
                .exchange()
                .expectStatus().isNoContent();
    }
}
