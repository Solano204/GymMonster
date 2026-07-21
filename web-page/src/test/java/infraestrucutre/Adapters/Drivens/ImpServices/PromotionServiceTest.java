package infraestrucutre.Adapters.Drivens.ImpServices;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

import infraestrucutre.Adapters.Drivens.Entities.Promotion;
import infraestrucutre.Adapters.Drivens.Repositories.PromotionRepository;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"unchecked", "rawtypes"})
class PromotionServiceTest {

    @Mock
    private PromotionRepository promotionRepository;

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private RBucket<List<Promotion>> bucket;

    private PromotionService service;

    private Promotion sample() {
        return new Promotion(1, "Summer sale", "1 month", 20, LocalDate.now(), LocalDate.now().plusMonths(1), true);
    }

    @BeforeEach
    void setUp() {
        service = new PromotionService(promotionRepository, redissonClient);
    }

    @Test
    void getAllCurrentPromotions_returnsCachedValue_onCacheHit() {
        when(redissonClient.getBucket("AllPromotions:Current:2026-01-01")).thenReturn((RBucket) bucket);
        Promotion promotion = sample();
        when(bucket.get()).thenReturn(List.of(promotion));

        StepVerifier.create(service.getAllCurrentPromotions("2026-01-01")).expectNext(promotion).verifyComplete();

        verify(promotionRepository, never()).getPromotionsForCurrentDate(org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void getAllCurrentPromotions_fetchesFromRepositoryAndCaches_onCacheMiss() {
        when(redissonClient.getBucket("AllPromotions:Current:2026-01-01")).thenReturn((RBucket) bucket);
        when(bucket.get()).thenReturn(null);
        Promotion promotion = sample();
        when(promotionRepository.getPromotionsForCurrentDate("2026-01-01")).thenReturn(Flux.just(promotion));

        StepVerifier.create(service.getAllCurrentPromotions("2026-01-01")).expectNext(promotion).verifyComplete();

        verify(bucket).set(List.of(promotion), Duration.ofHours(1));
    }

    @Test
    void getAllPromotionsByDate_returnsCachedValue_onCacheHit() {
        when(redissonClient.getBucket("AllPromotions:ByDate:2026-02-01")).thenReturn((RBucket) bucket);
        Promotion promotion = sample();
        when(bucket.get()).thenReturn(List.of(promotion));

        StepVerifier.create(service.getAllPromotionsByDate("2026-02-01")).expectNext(promotion).verifyComplete();

        verify(promotionRepository, never()).getPromotionsForDate(org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void getAllPromotionsByDate_fetchesFromRepositoryAndCaches_onCacheMiss() {
        when(redissonClient.getBucket("AllPromotions:ByDate:2026-02-01")).thenReturn((RBucket) bucket);
        when(bucket.get()).thenReturn(null);
        Promotion promotion = sample();
        when(promotionRepository.getPromotionsForDate("2026-02-01")).thenReturn(Flux.just(promotion));

        StepVerifier.create(service.getAllPromotionsByDate("2026-02-01")).expectNext(promotion).verifyComplete();

        verify(bucket).set(List.of(promotion), Duration.ofHours(1));
    }
}
