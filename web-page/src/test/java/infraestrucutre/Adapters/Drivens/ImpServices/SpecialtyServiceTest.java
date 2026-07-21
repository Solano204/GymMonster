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

import application.Ports.Drivens.InterfaceRepositories.SpecialtyRepositoryInterface;
import infraestrucutre.Adapters.Drivens.DTOS.DtoSpecialtyRecived;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"unchecked", "rawtypes"})
class SpecialtyServiceTest {

    @Mock
    private SpecialtyRepositoryInterface specialtyRepository;

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private RBucket<List<DtoSpecialtyRecived>> bucket;

    private SpecialtyService service;

    @BeforeEach
    void setUp() {
        service = new SpecialtyService(specialtyRepository, redissonClient);
        when(redissonClient.getBucket("All:Specialties")).thenReturn((RBucket) bucket);
    }

    @Test
    void getAllSpecialties_returnsCachedValue_withoutHittingTheRepository_onCacheHit() {
        DtoSpecialtyRecived specialty = new DtoSpecialtyRecived("Yoga", "desc");
        when(bucket.get()).thenReturn(List.of(specialty));

        StepVerifier.create(service.getAllSpecialties()).expectNext(specialty).verifyComplete();

        verify(specialtyRepository, never()).getAllSpecialties();
    }

    @Test
    void getAllSpecialties_fetchesFromRepositoryAndCaches_onCacheMiss() {
        when(bucket.get()).thenReturn(null);
        DtoSpecialtyRecived specialty = new DtoSpecialtyRecived("Yoga", "desc");
        when(specialtyRepository.getAllSpecialties()).thenReturn(Flux.just(specialty));

        StepVerifier.create(service.getAllSpecialties()).expectNext(specialty).verifyComplete();

        verify(bucket).set(List.of(specialty), Duration.ofHours(24));
    }
}
