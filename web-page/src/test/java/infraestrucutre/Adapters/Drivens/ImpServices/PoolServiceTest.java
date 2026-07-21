package infraestrucutre.Adapters.Drivens.ImpServices;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

import application.Ports.Drivens.InterfaceRepositories.PoolRepositoryInterface;
import infraestrucutre.Adapters.Drivens.DTOS.DtoPoolReciving;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"unchecked", "rawtypes"})
class PoolServiceTest {

    @Mock
    private PoolRepositoryInterface poolRepository;

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private RBucket<List<DtoPoolReciving>> bucket;

    private PoolService service;

    @BeforeEach
    void setUp() {
        service = new PoolService(poolRepository, redissonClient);
        when(redissonClient.getBucket("All:Pools")).thenReturn((RBucket) bucket);
    }

    @Test
    void getAllPools_returnsCachedValue_withoutHittingTheRepository_onCacheHit() {
        DtoPoolReciving pool = new DtoPoolReciving(1, "Olympic", "desc");
        when(bucket.get()).thenReturn(List.of(pool));

        StepVerifier.create(service.getAllPools()).expectNext(pool).verifyComplete();

        verify(poolRepository, never()).getAllPools();
    }

    @Test
    void getAllPools_fetchesFromRepositoryAndCaches_onCacheMiss() {
        when(bucket.get()).thenReturn(null);
        DtoPoolReciving pool = new DtoPoolReciving(1, "Olympic", "desc");
        when(poolRepository.getAllPools()).thenReturn(Flux.just(pool));

        StepVerifier.create(service.getAllPools()).expectNext(pool).verifyComplete();

        verify(bucket).set(List.of(pool), Duration.ofHours(1));
    }
}
