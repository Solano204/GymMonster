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

import application.Ports.Drivens.InterfaceRepositories.MembershipRepositoryInterface;
import infraestrucutre.Adapters.Drivens.DTOS.DtoMembershipReciving;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"unchecked", "rawtypes"})
class MembershipServiceTest {

    @Mock
    private MembershipRepositoryInterface membershipRepository;

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private RBucket<List<DtoMembershipReciving>> bucket;

    private MembershipService service;

    @BeforeEach
    void setUp() {
        service = new MembershipService(membershipRepository, redissonClient);
        when(redissonClient.getBucket("All:Memberships")).thenReturn((RBucket) bucket);
    }

    @Test
    void getAllMemberships_returnsCachedValue_withoutHittingTheRepository_onCacheHit() {
        DtoMembershipReciving membership = new DtoMembershipReciving("GOLD", "desc", true, true, true);
        when(bucket.get()).thenReturn(List.of(membership));

        StepVerifier.create(service.getAllMemberships()).expectNext(membership).verifyComplete();

        verify(membershipRepository, never()).getAllMemberships();
    }

    @Test
    void getAllMemberships_fetchesFromRepositoryAndCaches_onCacheMiss() {
        when(bucket.get()).thenReturn(null);
        DtoMembershipReciving membership = new DtoMembershipReciving("GOLD", "desc", true, true, true);
        when(membershipRepository.getAllMemberships()).thenReturn(Flux.just(membership));

        StepVerifier.create(service.getAllMemberships()).expectNext(membership).verifyComplete();

        verify(bucket).set(List.of(membership), Duration.ofMinutes(3));
    }
}
