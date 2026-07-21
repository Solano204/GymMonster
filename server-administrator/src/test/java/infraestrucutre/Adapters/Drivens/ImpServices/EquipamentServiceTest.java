package infraestrucutre.Adapters.Drivens.ImpServices;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import infraestrucutre.Adapters.Drivens.Entities.Equipament;
import infraestrucutre.Adapters.Drivens.Repositories.EquipamentInformationClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class EquipamentServiceTest {

    @Mock
    private EquipamentInformationClient equipamentRepository;

    private EquipamentService service;

    @BeforeEach
    void setUp() {
        service = new EquipamentService(equipamentRepository);
    }

    private Equipament sampleEquipament() {
        return new Equipament(1L, "Treadmill", "Cardio machine", LocalDate.now(), LocalDate.now().plusYears(5), "NEW");
    }

    @Test
    void createEquipament_delegatesToRepository() {
        Equipament equipament = sampleEquipament();
        when(equipamentRepository.createEquipament(equipament)).thenReturn(Mono.just(equipament));

        StepVerifier.create(service.createEquipament(equipament)).expectNext(equipament).verifyComplete();
    }

    @Test
    void getAllEquipaments_delegatesToRepository() {
        Equipament equipament = sampleEquipament();
        when(equipamentRepository.getAllEquipaments()).thenReturn(Flux.just(equipament));

        StepVerifier.create(service.getAllEquipaments()).expectNext(equipament).verifyComplete();
    }

    @Test
    void getEquipamentByName_delegatesToRepository() {
        Equipament equipament = sampleEquipament();
        when(equipamentRepository.getEquipamentByName("Treadmill")).thenReturn(Mono.just(equipament));

        StepVerifier.create(service.getEquipamentByName("Treadmill")).expectNext(equipament).verifyComplete();
    }

    @Test
    void updateEquipament_delegatesToRepositoryWithIdAndPayload() {
        Equipament equipament = sampleEquipament();
        when(equipamentRepository.updateEquipament(1L, equipament)).thenReturn(Mono.just(equipament));

        StepVerifier.create(service.updateEquipament(1L, equipament)).expectNext(equipament).verifyComplete();
        verify(equipamentRepository).updateEquipament(1L, equipament);
    }

    @Test
    void deleteEquipamentByName_delegatesToRepository() {
        when(equipamentRepository.deleteEquipamentByName("Treadmill")).thenReturn(Mono.just("Deleted"));

        StepVerifier.create(service.deleteEquipamentByName("Treadmill")).expectNext("Deleted").verifyComplete();
    }
}
