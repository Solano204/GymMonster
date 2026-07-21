package infraestrucutre.Adapters.Drivens.ImpServices;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import application.Ports.Drivens.RepositoriesInterfaces.PromotionInformationClientInterface;
import infraestrucutre.Adapters.Drivens.Entities.Promotion;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class PromotionServiceTest {

    @Mock
    private PromotionInformationClientInterface promotionRepository;

    private PromotionService service;

    @BeforeEach
    void setUp() {
        service = new PromotionService(promotionRepository);
    }

    private Promotion sample() {
        return new Promotion(1, "Summer sale", "1 month", 20, LocalDate.now(), LocalDate.now().plusMonths(1), true);
    }

    @Test
    void getAllPromotions_delegatesToRepository() {
        Promotion promotion = sample();
        when(promotionRepository.getAllPromotions()).thenReturn(Flux.just(promotion));

        StepVerifier.create(service.getAllPromotions()).expectNext(promotion).verifyComplete();
    }

    @Test
    void getPromotionsByCurrentDate_delegatesToRepository() {
        LocalDate date = LocalDate.of(2026, 1, 1);
        Promotion promotion = sample();
        when(promotionRepository.getPromotionsByCurrentDate(date)).thenReturn(Flux.just(promotion));

        StepVerifier.create(service.getPromotionsByCurrentDate(date)).expectNext(promotion).verifyComplete();
    }

    @Test
    void getPromotionsByStartDate_delegatesToRepository() {
        LocalDate date = LocalDate.of(2026, 1, 1);
        Promotion promotion = sample();
        when(promotionRepository.getPromotionsByStartDate(date)).thenReturn(Flux.just(promotion));

        StepVerifier.create(service.getPromotionsByStartDate(date)).expectNext(promotion).verifyComplete();
    }

    @Test
    void getPromotionsByEndDate_delegatesToRepository() {
        LocalDate date = LocalDate.of(2026, 1, 1);
        Promotion promotion = sample();
        when(promotionRepository.getPromotionsByEndDate(date)).thenReturn(Flux.just(promotion));

        StepVerifier.create(service.getPromotionsByEndDate(date)).expectNext(promotion).verifyComplete();
    }

    @Test
    void createPromotion_delegatesToRepository() {
        Promotion promotion = sample();
        when(promotionRepository.createPromotion(promotion)).thenReturn(Mono.just(promotion));

        StepVerifier.create(service.createPromotion(promotion)).expectNext(promotion).verifyComplete();
    }

    @Test
    void updatePromotion_delegatesToRepositoryWithIdAndPayload() {
        Promotion promotion = sample();
        when(promotionRepository.updatePromotion(1, promotion)).thenReturn(Mono.just("Updated"));

        StepVerifier.create(service.updatePromotion(1, promotion)).expectNext("Updated").verifyComplete();
        verify(promotionRepository).updatePromotion(1, promotion);
    }

    @Test
    void deletePromotionById_delegatesToRepository() {
        when(promotionRepository.deletePromotionById(1)).thenReturn(Mono.just("Deleted"));

        StepVerifier.create(service.deletePromotionById(1)).expectNext("Deleted").verifyComplete();
    }
}
