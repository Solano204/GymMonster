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

import application.Ports.Drivens.InterfaceRepositories.PerTrainerRepositoryInteface;
import infraestrucutre.Adapters.Drivens.DTOS.DtoSpecialtyRecived;
import infraestrucutre.Adapters.Drivens.DTOS.DtoTrainerData;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"unchecked", "rawtypes"})
class TrainerServiceTest {

    @Mock
    private PerTrainerRepositoryInteface trainerRepository;

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private RBucket<List<DtoTrainerData>> bucket;

    private TrainerService service;

    @BeforeEach
    void setUp() {
        service = new TrainerService(trainerRepository, redissonClient);
    }

    private DtoTrainerData sample() {
        return new DtoTrainerData(1L, "coach99", "coach@test.com", "Ana", "M", "Lopez", "Diaz", "35", "170", "65", null, 5);
    }

    @Test
    void getAllPerTrainers_returnsCachedValue_withoutHittingTheRepository_onCacheHit() {
        when(redissonClient.getBucket("AllTrainers:page:0:size:10")).thenReturn((RBucket) bucket);
        DtoTrainerData trainer = sample();
        when(bucket.get()).thenReturn(List.of(trainer));

        StepVerifier.create(service.getAllPerTrainers(0, 10)).expectNext(trainer).verifyComplete();

        verify(trainerRepository, never()).getAllTrainers(org.mockito.ArgumentMatchers.anyInt(), org.mockito.ArgumentMatchers.anyInt());
    }

    @Test
    void getAllPerTrainers_treatsEmptyCachedList_asACacheMiss() {
        // Unlike every sibling cache-aside method in this service layer (bucket.get() != null
        // is the only check), this one also excludes an EMPTY cached list from counting as a
        // hit - so an empty page result is never "permanently" cached as empty.
        when(redissonClient.getBucket("AllTrainers:page:0:size:10")).thenReturn((RBucket) bucket);
        when(bucket.get()).thenReturn(List.of());
        DtoTrainerData trainer = sample();
        when(trainerRepository.getAllTrainers(0, 10)).thenReturn(Flux.just(trainer));

        StepVerifier.create(service.getAllPerTrainers(0, 10)).expectNext(trainer).verifyComplete();

        verify(trainerRepository).getAllTrainers(0, 10);
    }

    @Test
    void getAllPerTrainers_fetchesFromRepositoryAndCaches_onCacheMiss() {
        when(redissonClient.getBucket("AllTrainers:page:1:size:5")).thenReturn((RBucket) bucket);
        when(bucket.get()).thenReturn(null);
        DtoTrainerData trainer = sample();
        when(trainerRepository.getAllTrainers(1, 5)).thenReturn(Flux.just(trainer));

        StepVerifier.create(service.getAllPerTrainers(1, 5)).expectNext(trainer).verifyComplete();

        verify(bucket).set(List.of(trainer), Duration.ofMinutes(1));
    }

    @Test
    void getAllSpecialtiesFromTrainer_delegatesDirectlyToRepository_withNoCaching() {
        List<DtoSpecialtyRecived> specialties = List.of(new DtoSpecialtyRecived("Yoga", "desc"));
        when(trainerRepository.getAllSpecialtiesFromTrainer("coach99")).thenReturn(Mono.just(specialties));

        StepVerifier.create(service.getAllSpecialtiesFromTrainer("coach99"))
                .expectNext(specialties)
                .verifyComplete();

        org.mockito.Mockito.verifyNoInteractions(redissonClient);
    }
}
