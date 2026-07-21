package infraestrucutre.Adapters.Drivens.ImpServices;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import infraestrucutre.Adapters.Drivens.Entities.Promotion;
import infraestrucutre.Adapters.Drivens.Repositories.PromotionRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class PromotionServiceTest {

    @Mock
    private PromotionRepository promotionRepository;

    private PromotionService service;

    @BeforeEach
    void setUp() {
        service = new PromotionService(promotionRepository);
    }

    private Promotion sample() {
        Promotion promotion = new Promotion();
        promotion.setId(1);
        promotion.setDescription("Summer sale");
        promotion.setDuration("1 month");
        promotion.setPercentageDiscount(20);
        promotion.setStartDate(LocalDate.now());
        promotion.setEndDate(LocalDate.now().plusMonths(1));
        promotion.setActive(true);
        return promotion;
    }

    @Test
    void createPromotion_delegatesToSave() {
        Promotion promotion = sample();
        when(promotionRepository.save(promotion)).thenReturn(Mono.just(promotion));

        StepVerifier.create(service.createPromotion(promotion)).expectNext(promotion).verifyComplete();
    }

    @Test
    void getAllPromotions_delegatesToFindAll() {
        when(promotionRepository.findAll()).thenReturn(Flux.just(sample()));

        StepVerifier.create(service.getAllPromotions()).expectNextCount(1).verifyComplete();
    }

    @Test
    void getPromotionByStartDate_delegatesToFindByStartDate() {
        LocalDate date = LocalDate.now();
        when(promotionRepository.findByStartDate(date)).thenReturn(Flux.just(sample()));

        StepVerifier.create(service.getPromotionByStartDate(date)).expectNextCount(1).verifyComplete();
    }

    @Test
    void getPromotionByEndDate_delegatesToFindByEndDate() {
        LocalDate date = LocalDate.now();
        when(promotionRepository.findByEndDate(date)).thenReturn(Flux.just(sample()));

        StepVerifier.create(service.getPromotionByEndDate(date)).expectNextCount(1).verifyComplete();
    }

    @Test
    void deletePromotionByStartDate_delegatesToDeleteByStartDate() {
        LocalDate date = LocalDate.now();
        when(promotionRepository.deleteByStartDate(date)).thenReturn(Mono.empty());

        StepVerifier.create(service.deletePromotionByStartDate(date)).verifyComplete();
    }

    @Test
    void deletePromotionByEndDate_delegatesToDeleteByEndDate() {
        LocalDate date = LocalDate.now();
        when(promotionRepository.deleteByEndDate(date)).thenReturn(Mono.empty());

        StepVerifier.create(service.deletePromotionByEndDate(date)).verifyComplete();
    }

    @Test
    void deletePromotion_delegatesToDeleteById() {
        when(promotionRepository.deleteById(1)).thenReturn(Mono.empty());

        StepVerifier.create(service.deletePromotion(1)).verifyComplete();
    }

    @Test
    void updatePromotion_mutatesTheExistingRowFieldsThenSaves() {
        Promotion existing = sample();
        Promotion incoming = new Promotion();
        incoming.setDescription("Winter sale");
        incoming.setDuration("2 months");
        incoming.setPercentageDiscount(30);
        incoming.setStartDate(LocalDate.now());
        incoming.setEndDate(LocalDate.now().plusMonths(2));
        incoming.setActive(false);
        when(promotionRepository.findById(1)).thenReturn(Mono.just(existing));
        when(promotionRepository.save(existing)).thenReturn(Mono.just(existing));

        StepVerifier.create(service.updatePromotion(1, incoming))
                .assertNext(saved -> {
                    assertThat(saved.getDescription()).isEqualTo("Winter sale");
                    assertThat(saved.isActive()).isFalse();
                })
                .verifyComplete();
    }
}
