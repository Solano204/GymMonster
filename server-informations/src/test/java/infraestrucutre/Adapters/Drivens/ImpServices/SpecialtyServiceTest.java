package infraestrucutre.Adapters.Drivens.ImpServices;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import infraestrucutre.Adapters.Drivens.Entities.Specialty;
import infraestrucutre.Adapters.Drivens.Repositories.SpecialtyRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class SpecialtyServiceTest {

    @Mock
    private SpecialtyRepository specialtyRepository;

    private SpecialtyService service;

    @BeforeEach
    void setUp() {
        service = new SpecialtyService(specialtyRepository);
    }

    private Specialty sample() {
        return new Specialty(1L, "Yoga", "desc");
    }

    @Test
    void createSpecialty_delegatesToSave() {
        Specialty specialty = sample();
        when(specialtyRepository.save(specialty)).thenReturn(Mono.just(specialty));

        StepVerifier.create(service.createSpecialty(specialty)).expectNext(specialty).verifyComplete();
    }

    @Test
    void getAllSpecialties_delegatesToFindAll() {
        when(specialtyRepository.findAll()).thenReturn(Flux.just(sample()));

        StepVerifier.create(service.getAllSpecialties()).expectNextCount(1).verifyComplete();
    }

    @Test
    void getSpecialtyById_delegatesToFindById() {
        Specialty specialty = sample();
        when(specialtyRepository.findById(1)).thenReturn(Mono.just(specialty));

        StepVerifier.create(service.getSpecialtyById(1)).expectNext(specialty).verifyComplete();
    }

    @Test
    void getSpecialtyByName_delegatesToFindByName() {
        Specialty specialty = sample();
        when(specialtyRepository.findByName("Yoga")).thenReturn(Mono.just(specialty));

        StepVerifier.create(service.getSpecialtyByName("Yoga")).expectNext(specialty).verifyComplete();
    }

    @Test
    void updateSpecialty_mutatesTheExistingRowFieldsThenSaves_lookingUpById() {
        Specialty existing = sample();
        Specialty incoming = new Specialty(null, "Pilates", "new-desc");
        when(specialtyRepository.findById(1)).thenReturn(Mono.just(existing));
        when(specialtyRepository.save(existing)).thenReturn(Mono.just(existing));

        StepVerifier.create(service.updateSpecialty(1, incoming))
                .assertNext(saved -> assertThat(saved.getName()).isEqualTo("Pilates"))
                .verifyComplete();
    }

    @Test
    void updateSpecialtyName_mutatesTheExistingRowFieldsThenSaves_lookingUpByName() {
        Specialty existing = sample();
        Specialty incoming = new Specialty(null, "Crossfit", "new-desc");
        when(specialtyRepository.findByName("Yoga")).thenReturn(Mono.just(existing));
        when(specialtyRepository.save(existing)).thenReturn(Mono.just(existing));

        StepVerifier.create(service.updateSpecialtyName("Yoga", incoming))
                .assertNext(saved -> assertThat(saved.getName()).isEqualTo("Crossfit"))
                .verifyComplete();
    }

    @Test
    void deleteSpecialty_delegatesToDeleteById() {
        when(specialtyRepository.deleteById(1)).thenReturn(Mono.empty());

        StepVerifier.create(service.deleteSpecialty(1)).verifyComplete();
    }

    @Test
    void deleteSpecialtyByName_delegatesToDeleteByName() {
        when(specialtyRepository.deleteByName("Yoga")).thenReturn(Mono.empty());

        StepVerifier.create(service.deleteSpecialtyByName("Yoga")).verifyComplete();
    }
}
