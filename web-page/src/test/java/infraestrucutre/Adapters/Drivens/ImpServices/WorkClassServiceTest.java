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

import application.Ports.Drivens.InterfaceRepositories.WorkClassRepositoryInterface;
import infraestrucutre.Adapters.Drivens.DTOS.DtoDetailUserReciving;
import infraestrucutre.Adapters.Drivens.Entities.Schedule;
import infraestrucutre.Adapters.Drivens.Entities.WorkClass;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"unchecked", "rawtypes"})
class WorkClassServiceTest {

    @Mock
    private WorkClassRepositoryInterface workClassRepository;

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private RBucket bucket;

    private WorkClassService service;

    @BeforeEach
    void setUp() {
        service = new WorkClassService(workClassRepository, redissonClient);
    }

    @Test
    void getAllWorkClasses_returnsCachedValue_onCacheHit() {
        when(redissonClient.getBucket("All:Workclasses")).thenReturn(bucket);
        WorkClass workClass = new WorkClass(1L, "Yoga", "desc", "60min");
        when(bucket.get()).thenReturn(List.of(workClass));

        StepVerifier.create(service.getAllWorkClasses()).expectNext(workClass).verifyComplete();

        verify(workClassRepository, never()).getAllWorkClasses();
    }

    @Test
    void getAllWorkClasses_fetchesFromRepositoryAndCaches_onCacheMiss() {
        when(redissonClient.getBucket("All:Workclasses")).thenReturn(bucket);
        when(bucket.get()).thenReturn(null);
        WorkClass workClass = new WorkClass(1L, "Yoga", "desc", "60min");
        when(workClassRepository.getAllWorkClasses()).thenReturn(Flux.just(workClass));

        StepVerifier.create(service.getAllWorkClasses()).expectNext(workClass).verifyComplete();

        verify(bucket).set(List.of(workClass), Duration.ofHours(24));
    }

    @Test
    void getWorkClassSchedules_returnsCachedValue_onCacheHit() {
        when(redissonClient.getBucket("AllSchedules:ByWorkclass:Yoga")).thenReturn(bucket);
        Schedule schedule = new Schedule(1, "MONDAY", "08:00", "09:00");
        when(bucket.get()).thenReturn(List.of(schedule));

        StepVerifier.create(service.getWorkClassSchedules("Yoga")).expectNext(schedule).verifyComplete();

        verify(workClassRepository, never()).getCSchedulesByWorkClassWithPagination(org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void getWorkClassSchedules_fetchesFromRepositoryAndCaches_onCacheMiss() {
        when(redissonClient.getBucket("AllSchedules:ByWorkclass:Yoga")).thenReturn(bucket);
        when(bucket.get()).thenReturn(null);
        Schedule schedule = new Schedule(1, "MONDAY", "08:00", "09:00");
        when(workClassRepository.getCSchedulesByWorkClassWithPagination("Yoga")).thenReturn(Flux.just(schedule));

        StepVerifier.create(service.getWorkClassSchedules("Yoga")).expectNext(schedule).verifyComplete();

        verify(bucket).set(List.of(schedule), Duration.ofHours(6));
    }

    @Test
    void getClientsByWorkClassWithPagination_returnsCachedValue_onCacheHit() {
        when(redissonClient.getBucket("AllClients:ByWorkclass:Yoga:page:0:size:10")).thenReturn(bucket);
        DtoDetailUserReciving client = new DtoDetailUserReciving("John", "Q", "Doe", "Public", "30");
        when(bucket.get()).thenReturn(List.of(client));

        StepVerifier.create(service.getClientsByWorkClassWithPagination("Yoga", 0, 10))
                .expectNext(client)
                .verifyComplete();

        verify(workClassRepository, never()).getClientsByWorkClassWithPagination(
                org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyInt(), org.mockito.ArgumentMatchers.anyInt());
    }

    @Test
    void getClientsByWorkClassWithPagination_fetchesFromRepositoryAndCaches_onCacheMiss() {
        when(redissonClient.getBucket("AllClients:ByWorkclass:Yoga:page:0:size:10")).thenReturn(bucket);
        when(bucket.get()).thenReturn(null);
        DtoDetailUserReciving client = new DtoDetailUserReciving("John", "Q", "Doe", "Public", "30");
        when(workClassRepository.getClientsByWorkClassWithPagination("Yoga", 0, 10)).thenReturn(Flux.just(client));

        StepVerifier.create(service.getClientsByWorkClassWithPagination("Yoga", 0, 10))
                .expectNext(client)
                .verifyComplete();

        verify(bucket).set(List.of(client), Duration.ofHours(1));
    }

    @Test
    void getTrainersByWorkClassWithPagination_returnsCachedValue_onCacheHit() {
        when(redissonClient.getBucket("AllTrainers:ByWorkclass:Yoga:page:0:size:10")).thenReturn(bucket);
        DtoDetailUserReciving trainer = new DtoDetailUserReciving("Coach", "Q", "Doe", "Public", "30");
        when(bucket.get()).thenReturn(List.of(trainer));

        StepVerifier.create(service.getTrainersByWorkClassWithPagination("Yoga", 0, 10))
                .expectNext(trainer)
                .verifyComplete();

        verify(workClassRepository, never()).getTrainersByWorkClassWithPagination(
                org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyInt(), org.mockito.ArgumentMatchers.anyInt());
    }

    @Test
    void getTrainersByWorkClassWithPagination_fetchesFromRepositoryAndCaches_onCacheMiss() {
        when(redissonClient.getBucket("AllTrainers:ByWorkclass:Yoga:page:0:size:10")).thenReturn(bucket);
        when(bucket.get()).thenReturn(null);
        DtoDetailUserReciving trainer = new DtoDetailUserReciving("Coach", "Q", "Doe", "Public", "30");
        when(workClassRepository.getTrainersByWorkClassWithPagination("Yoga", 0, 10)).thenReturn(Flux.just(trainer));

        StepVerifier.create(service.getTrainersByWorkClassWithPagination("Yoga", 0, 10))
                .expectNext(trainer)
                .verifyComplete();

        verify(bucket).set(List.of(trainer), Duration.ofHours(1));
    }
}
