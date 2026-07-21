package infraestrucutre.Adapters.Drivens.ImpServices;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import infraestrucutre.Adapters.Drivens.Entities.Equipament;
import infraestrucutre.Adapters.Drivens.Repositories.EquipamentRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class EquipamentServiceTest {

    @Mock
    private EquipamentRepository equipamentRepository;

    private EquipamentService service;

    @BeforeEach
    void setUp() {
        service = new EquipamentService(equipamentRepository);
    }

    private Equipament sample() {
        return new Equipament(1L, "Treadmill", "desc", LocalDate.now(), LocalDate.now().plusYears(5), "NEW");
    }

    @Test
    void createEquipament_delegatesToSave() {
        Equipament equipament = sample();
        when(equipamentRepository.save(equipament)).thenReturn(Mono.just(equipament));

        StepVerifier.create(service.createEquipament(equipament)).expectNext(equipament).verifyComplete();
    }

    @Test
    void getAllEquipaments_delegatesToFindAll() {
        when(equipamentRepository.findAll()).thenReturn(Flux.just(sample()));

        StepVerifier.create(service.getAllEquipaments()).expectNextCount(1).verifyComplete();
    }

    @Test
    void getEquipamentByName_delegatesToFindByName() {
        Equipament equipament = sample();
        when(equipamentRepository.findByName("Treadmill")).thenReturn(Mono.just(equipament));

        StepVerifier.create(service.getEquipamentByName("Treadmill")).expectNext(equipament).verifyComplete();
    }

    @Test
    void updateEquipament_mutatesTheExistingRowFieldsThenSaves() {
        Equipament existing = new Equipament(1L, "Old", "old-desc", LocalDate.now(), LocalDate.now(), "OLD");
        Equipament incoming = new Equipament(null, "New", "new-desc", LocalDate.now().plusDays(1), LocalDate.now().plusYears(1), "NEW");
        when(equipamentRepository.findById(1L)).thenReturn(Mono.just(existing));
        when(equipamentRepository.save(existing)).thenReturn(Mono.just(existing));

        StepVerifier.create(service.updateEquipament(1L, incoming))
                .assertNext(saved -> {
                    assertThat(saved.getName()).isEqualTo("New");
                    assertThat(saved.getDescription()).isEqualTo("new-desc");
                    assertThat(saved.getAgeStatus()).isEqualTo("NEW");
                })
                .verifyComplete();
    }

    @Test
    void updateEquipament_completesEmpty_whenIdDoesNotExist() {
        when(equipamentRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(service.updateEquipament(99L, sample())).verifyComplete();
    }

    @Test
    void deleteEquipament_delegatesToDeleteByName() {
        when(equipamentRepository.deleteByName("Treadmill")).thenReturn(Mono.empty());

        StepVerifier.create(service.deleteEquipament("Treadmill")).verifyComplete();
    }
}
