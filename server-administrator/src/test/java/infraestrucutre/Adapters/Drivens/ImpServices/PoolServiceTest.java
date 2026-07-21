package infraestrucutre.Adapters.Drivens.ImpServices;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import infraestrucutre.Adapters.Drivens.DTOS.DtoPoolSent;
import infraestrucutre.Adapters.Drivens.Repositories.PoolInformationClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class PoolServiceTest {

    @Mock
    private PoolInformationClient poolRepository;

    private PoolService service;

    @BeforeEach
    void setUp() {
        service = new PoolService(poolRepository);
    }

    private DtoPoolSent sample() {
        return new DtoPoolSent(1, "Olympic Pool", "50m lap pool", LocalDate.now(), LocalDate.now(), LocalDate.now().plusMonths(1));
    }

    @Test
    void getAllPools_delegatesToRepository() {
        DtoPoolSent pool = sample();
        when(poolRepository.getAllPools()).thenReturn(Flux.just(pool));

        StepVerifier.create(service.getAllPools()).expectNext(pool).verifyComplete();
    }

    @Test
    void createPool_delegatesToRepository() {
        DtoPoolSent pool = sample();
        when(poolRepository.createPool(pool)).thenReturn(Mono.just(pool));

        StepVerifier.create(service.createPool(pool)).expectNext(pool).verifyComplete();
    }

    @Test
    void updatePool_delegatesToRepositoryWithNameAndPayload() {
        DtoPoolSent pool = sample();
        when(poolRepository.updatePool("Olympic Pool", pool)).thenReturn(Mono.just("Updated"));

        StepVerifier.create(service.updatePool("Olympic Pool", pool)).expectNext("Updated").verifyComplete();
        verify(poolRepository).updatePool("Olympic Pool", pool);
    }

    @Test
    void deletePoolByName_delegatesToRepository() {
        when(poolRepository.deletePoolByName("Olympic Pool")).thenReturn(Mono.just("Deleted"));

        StepVerifier.create(service.deletePoolByName("Olympic Pool")).expectNext("Deleted").verifyComplete();
    }
}
