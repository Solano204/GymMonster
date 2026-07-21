package infraestrucutre.Adapters.Drivens.ImpServices;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import infraestrucutre.Adapters.Drivens.Entities.Pool;
import infraestrucutre.Adapters.Drivens.Repositories.PoolRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class PoolServiceTest {

    @Mock
    private PoolRepository poolRepository;

    private PoolService service;

    @BeforeEach
    void setUp() {
        service = new PoolService(poolRepository);
    }

    private Pool sample() {
        Pool pool = new Pool();
        pool.setId(1);
        pool.setName("Olympic");
        pool.setDescription("desc");
        pool.setDateClean(LocalDate.now());
        pool.setStartDate(LocalDate.now());
        pool.setEndDate(LocalDate.now().plusMonths(1));
        return pool;
    }

    @Test
    void createPool_delegatesToSave() {
        Pool pool = sample();
        when(poolRepository.save(pool)).thenReturn(Mono.just(pool));

        StepVerifier.create(service.createPool(pool)).expectNext(pool).verifyComplete();
    }

    @Test
    void getAllPools_delegatesToFindAll() {
        when(poolRepository.findAll()).thenReturn(Flux.just(sample()));

        StepVerifier.create(service.getAllPools()).expectNextCount(1).verifyComplete();
    }

    @Test
    void getPoolByName_delegatesToFindByName() {
        Pool pool = sample();
        when(poolRepository.findByName("Olympic")).thenReturn(Mono.just(pool));

        StepVerifier.create(service.getPoolByName("Olympic")).expectNext(pool).verifyComplete();
    }

    @Test
    void deletePoolByName_delegatesToDeleteByName() {
        when(poolRepository.deleteByName("Olympic")).thenReturn(Mono.empty());

        StepVerifier.create(service.deletePoolByName("Olympic")).verifyComplete();
    }

    @Test
    void updatePoolById_mutatesTheExistingRowFieldsThenSaves() {
        Pool existing = sample();
        Pool incoming = new Pool();
        incoming.setName("Kids");
        incoming.setDescription("shallow");
        incoming.setDateClean(LocalDate.now());
        incoming.setStartDate(LocalDate.now());
        incoming.setEndDate(LocalDate.now().plusDays(10));
        when(poolRepository.findById(1L)).thenReturn(Mono.just(existing));
        when(poolRepository.save(existing)).thenReturn(Mono.just(existing));

        StepVerifier.create(service.updatePoolById(1L, incoming))
                .assertNext(saved -> assertThat(saved.getName()).isEqualTo("Kids"))
                .verifyComplete();
    }
}
