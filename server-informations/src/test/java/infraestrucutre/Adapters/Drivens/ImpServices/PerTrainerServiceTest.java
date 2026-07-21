package infraestrucutre.Adapters.Drivens.ImpServices;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import infraestrucutre.Adapters.Drivens.DTOS.DtoKeyCloakUser;
import infraestrucutre.Adapters.Drivens.Entities.AllTrainer;
import infraestrucutre.Adapters.Drivens.Entities.DetailPerTrainer;
import infraestrucutre.Adapters.Drivens.Entities.PerTrainer;
import infraestrucutre.Adapters.Drivens.Repositories.DetailPerTrainerRepository;
import infraestrucutre.Adapters.Drivens.Repositories.PerTrainerRepository;
import infraestrucutre.Adapters.Drivens.Repositories.SpecialtyRepository;
import infraestrucutre.Adapters.Drivens.Validations.LogicInterfaces.ValidatePassword;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * Mirrors ClientServiceTest's approach: focuses on the password/username-verification branches
 * (highest auth-bypass risk) and the multi-check createPerTrainer validation zip, rather than
 * exhaustively re-testing every association method (addSpecialtyToTrainer's shape is already
 * proven once here; dessociateSpecialty/addClassToTrainer follow the identical pattern in
 * ClassTrainerService, covered there).
 */
class PerTrainerServiceTest {

    @Mock private PerTrainerRepository perTrainerRepository;
    @Mock private ValidatePassword validatePasswordRegister;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private DetailPerTrainerRepository detailTrainerRepository;
    @Mock private SpecialtyRepository specialtyRepository;
    @Mock private KeycloakServiceImpl keycloakService;
    @Mock private FillinKeycloakUser fillinKeycloakUser;

    private PerTrainerService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new PerTrainerService(perTrainerRepository, validatePasswordRegister, passwordEncoder,
                detailTrainerRepository, specialtyRepository, keycloakService, fillinKeycloakUser);
    }

    private AllTrainer validTrainer() {
        return new AllTrainer(null, "coach99", "S3cur3P@ssword!", "coach@test.com", "Ana", "M",
                "Lopez", "Diaz", "35", "170", "65", LocalDate.now());
    }

    private PerTrainer existingTrainer() {
        PerTrainer trainer = new PerTrainer();
        trainer.setId(1L);
        trainer.setId_detail(2L);
        trainer.setUsername("coach99");
        trainer.setPassword("encoded-old-password");
        trainer.setEmail("coach99@test.com");
        return trainer;
    }

    // ---- createPerTrainer ----

    @Test
    void createPerTrainer_reportsUsernameAlreadyExists() {
        when(perTrainerRepository.existsByUsername("coach99")).thenReturn(Mono.just(true));
        when(perTrainerRepository.existsByEmail(anyString())).thenReturn(Mono.just(false));
        when(validatePasswordRegister.validatePasswordRegister(anyString())).thenReturn(Mono.just(false));
        when(detailTrainerRepository.countByDetailInfo(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Mono.just(0));

        StepVerifier.create(service.createPerTrainer(validTrainer()))
                .assertNext(errors -> assertThat(errors).anyMatch(e -> e.contains("username already exists")))
                .verifyComplete();

        verify(perTrainerRepository, never()).save(any());
    }

    @Test
    void createPerTrainer_accumulatesMultipleErrors() {
        when(perTrainerRepository.existsByUsername("coach99")).thenReturn(Mono.just(true));
        when(perTrainerRepository.existsByEmail(anyString())).thenReturn(Mono.just(true));
        when(validatePasswordRegister.validatePasswordRegister(anyString())).thenReturn(Mono.just(true));
        when(detailTrainerRepository.countByDetailInfo(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Mono.just(1));

        StepVerifier.create(service.createPerTrainer(validTrainer()))
                .assertNext(errors -> assertThat(errors).hasSize(4))
                .verifyComplete();
    }

    @Test
    void createPerTrainer_savesDetailThenTrainerThenCreatesKeycloakUser_onSuccess() {
        when(perTrainerRepository.existsByUsername("coach99")).thenReturn(Mono.just(false));
        when(perTrainerRepository.existsByEmail(anyString())).thenReturn(Mono.just(false));
        when(validatePasswordRegister.validatePasswordRegister(anyString())).thenReturn(Mono.just(false));
        when(detailTrainerRepository.countByDetailInfo(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Mono.just(0));

        DetailPerTrainer savedDetail = new DetailPerTrainer();
        savedDetail.setId(2L);
        when(detailTrainerRepository.save(any(DetailPerTrainer.class))).thenReturn(Mono.just(savedDetail));
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-pw");
        when(perTrainerRepository.save(any(PerTrainer.class))).thenReturn(Mono.just(existingTrainer()));
        when(fillinKeycloakUser.fillinDataFromTrainer(any())).thenReturn(
                new infraestrucutre.Adapters.Drivens.DTOS.DtoDataReciving(
                        "coach99", "coach@test.com", "S3cur3P@ssword!", "Ana", "M", "Lopez", "Diaz", "35", "170", "65"));
        when(fillinKeycloakUser.fillinKeycloakUser(any())).thenReturn(new DtoKeyCloakUser());
        when(keycloakService.createUser(any())).thenReturn(Mono.just(new DtoKeyCloakUser()));

        StepVerifier.create(service.createPerTrainer(validTrainer()))
                .assertNext(result -> assertThat(result).containsExactly(
                        "Congratulations, your account has been created. You can now log in."))
                .verifyComplete();
    }

    // ---- updateTrainerPassword ----

    @Test
    void updateTrainerPassword_reportsWhenUsernameDoesNotExist() {
        when(perTrainerRepository.existsByUsername("ghost")).thenReturn(Mono.just(false));

        StepVerifier.create(service.updateTrainerPassword("ghost", "new", "old"))
                .expectNext("Username does not exist")
                .verifyComplete();
    }

    @Test
    void updateTrainerPassword_rejectsWrongOldPassword() {
        PerTrainer trainer = existingTrainer();
        when(perTrainerRepository.existsByUsername("coach99")).thenReturn(Mono.just(true));
        when(perTrainerRepository.findByUsername("coach99")).thenReturn(Mono.just(trainer));
        when(passwordEncoder.matches("wrong-old", trainer.getPassword())).thenReturn(false);

        StepVerifier.create(service.updateTrainerPassword("coach99", "new", "wrong-old"))
                .expectNext("Old password is not correct")
                .verifyComplete();

        verify(perTrainerRepository, never()).save(any());
    }

    @Test
    void updateTrainerPassword_savesEncodedPasswordAndSyncsKeycloak_onCorrectOldPassword() {
        PerTrainer trainer = existingTrainer();
        when(perTrainerRepository.existsByUsername("coach99")).thenReturn(Mono.just(true));
        when(perTrainerRepository.findByUsername("coach99")).thenReturn(Mono.just(trainer));
        when(passwordEncoder.matches("correct-old", trainer.getPassword())).thenReturn(true);
        when(passwordEncoder.encode("new-pw")).thenReturn("encoded-new-pw");
        when(perTrainerRepository.save(trainer)).thenReturn(Mono.just(trainer));
        when(keycloakService.changePassword("coach99", "new-pw")).thenReturn(Mono.just("Password changed successfully!"));

        StepVerifier.create(service.updateTrainerPassword("coach99", "new-pw", "correct-old"))
                .expectNext("Password updated successfully")
                .verifyComplete();

        assertThat(trainer.getPassword()).isEqualTo("encoded-new-pw");
    }

    // ---- updateTrainerUsername (this method uses Mono.error, not Mono.just, for its failure paths) ----

    @Test
    void updateTrainerUsername_errorsWhenOldUsernameDoesNotExist() {
        when(perTrainerRepository.existsByUsername("ghost")).thenReturn(Mono.just(false));

        StepVerifier.create(service.updateTrainerUsername("ghost", "newname", "pw"))
                .expectErrorMatches(e -> e.getMessage().equals("Old username does not exist"))
                .verify();
    }

    @Test
    void updateTrainerUsername_errorsWhenNewUsernameAlreadyTaken() {
        when(perTrainerRepository.existsByUsername("coach99")).thenReturn(Mono.just(true));
        when(perTrainerRepository.existsByUsername("taken")).thenReturn(Mono.just(true));

        StepVerifier.create(service.updateTrainerUsername("coach99", "taken", "pw"))
                .expectErrorMatches(e -> e.getMessage().equals("A user already exists with that new username"))
                .verify();
    }

    @Test
    void updateTrainerUsername_errorsWhenPasswordDoesNotMatch() {
        PerTrainer trainer = existingTrainer();
        when(perTrainerRepository.existsByUsername("coach99")).thenReturn(Mono.just(true));
        when(perTrainerRepository.existsByUsername("newname")).thenReturn(Mono.just(false));
        when(perTrainerRepository.findByUsername("coach99")).thenReturn(Mono.just(trainer));
        when(passwordEncoder.matches("wrong-pw", trainer.getPassword())).thenReturn(false);

        StepVerifier.create(service.updateTrainerUsername("coach99", "newname", "wrong-pw"))
                .expectErrorMatches(e -> e.getMessage().equals("Provided password does not match"))
                .verify();
    }

    @Test
    void updateTrainerUsername_succeedsAndSyncsKeycloak() {
        PerTrainer trainer = existingTrainer();
        when(perTrainerRepository.existsByUsername("coach99")).thenReturn(Mono.just(true));
        when(perTrainerRepository.existsByUsername("newname")).thenReturn(Mono.just(false));
        when(perTrainerRepository.findByUsername("coach99")).thenReturn(Mono.just(trainer));
        when(passwordEncoder.matches("correct-pw", trainer.getPassword())).thenReturn(true);
        when(perTrainerRepository.save(trainer)).thenReturn(Mono.just(trainer));
        when(keycloakService.updateUsername2("newname", "coach99", "correct-pw", "PerTrainer"))
                .thenReturn(Mono.just(new DtoKeyCloakUser()));

        StepVerifier.create(service.updateTrainerUsername("coach99", "newname", "correct-pw"))
                .expectNext("Username updated successfully")
                .verifyComplete();

        assertThat(trainer.getUsername()).isEqualTo("newname");
    }

    // ---- updateClientEmail ----

    @Test
    void updateClientEmail_reportsWhenUsernameDoesNotExist() {
        when(perTrainerRepository.existsByUsername("ghost")).thenReturn(Mono.just(false));

        StepVerifier.create(service.updateClientEmail("ghost", "new@test.com"))
                .expectNext("username does not exist")
                .verifyComplete();
    }

    @Test
    void updateClientEmail_reportsWhenNewEmailAlreadyTaken() {
        when(perTrainerRepository.existsByUsername("coach99")).thenReturn(Mono.just(true));
        when(perTrainerRepository.existsByEmail("taken@test.com")).thenReturn(Mono.just(true));

        StepVerifier.create(service.updateClientEmail("coach99", "taken@test.com"))
                .expectNext("a trainer already exists with that new email")
                .verifyComplete();
    }

    @Test
    void updateClientEmail_succeedsAndSyncsKeycloak() {
        PerTrainer trainer = existingTrainer();
        when(perTrainerRepository.existsByUsername("coach99")).thenReturn(Mono.just(true));
        when(perTrainerRepository.existsByEmail("new@test.com")).thenReturn(Mono.just(false));
        when(perTrainerRepository.findByUsername("coach99")).thenReturn(Mono.just(trainer));
        when(perTrainerRepository.save(trainer)).thenReturn(Mono.just(trainer));
        when(keycloakService.changeEmail("coach99", "new@test.com")).thenReturn(Mono.just("Email changed successfully!"));

        StepVerifier.create(service.updateClientEmail("coach99", "new@test.com"))
                .expectNext("Email updated successfully")
                .verifyComplete();

        assertThat(trainer.getEmail()).isEqualTo("new@test.com");
    }

    // ---- addSpecialtyToTrainer ----

    @Test
    void addSpecialtyToTrainer_reportsWhenUsernameDoesNotExist() {
        when(perTrainerRepository.existsByUsername("ghost")).thenReturn(Mono.just(false));

        StepVerifier.create(service.addSpecialtyToTrainer("ghost", "Yoga"))
                .expectNext("Username does not exist")
                .verifyComplete();
    }

    @Test
    void addSpecialtyToTrainer_reportsWhenAlreadyAssociated() {
        PerTrainer trainer = existingTrainer();
        when(perTrainerRepository.existsByUsername("coach99")).thenReturn(Mono.just(true));
        when(perTrainerRepository.findByUsername("coach99")).thenReturn(Mono.just(trainer));
        when(perTrainerRepository.existsSpecialtyForTrainer(1L, "Yoga")).thenReturn(Mono.just(1));

        StepVerifier.create(service.addSpecialtyToTrainer("coach99", "Yoga"))
                .expectNext("Specialty already exists")
                .verifyComplete();
    }

    @Test
    void addSpecialtyToTrainer_addsAssociation_onSuccess() {
        PerTrainer trainer = existingTrainer();
        infraestrucutre.Adapters.Drivens.Entities.Specialty specialty =
                new infraestrucutre.Adapters.Drivens.Entities.Specialty(5L, "Yoga", "desc");
        when(perTrainerRepository.existsByUsername("coach99")).thenReturn(Mono.just(true));
        when(perTrainerRepository.findByUsername("coach99")).thenReturn(Mono.just(trainer));
        when(perTrainerRepository.existsSpecialtyForTrainer(1L, "Yoga")).thenReturn(Mono.just(0));
        when(specialtyRepository.findByName("Yoga")).thenReturn(Mono.just(specialty));
        when(perTrainerRepository.addSpecialtyToTrainer(1L, 5L)).thenReturn(Mono.empty());

        StepVerifier.create(service.addSpecialtyToTrainer("coach99", "Yoga"))
                .expectNext("Specialty added successfully")
                .verifyComplete();
    }

    // ---- deletePerTrainerUsername ----

    @Test
    void deletePerTrainerUsername_rejectsWrongPassword() {
        PerTrainer trainer = existingTrainer();
        when(perTrainerRepository.existsByUsername("coach99")).thenReturn(Mono.just(true));
        when(perTrainerRepository.findByUsername("coach99")).thenReturn(Mono.just(trainer));
        when(passwordEncoder.matches("wrong", trainer.getPassword())).thenReturn(false);

        StepVerifier.create(service.deletePerTrainerUsername("coach99", "wrong"))
                .expectNext("Old password is not correct")
                .verifyComplete();

        verify(perTrainerRepository, never()).deleteByUsername(anyString());
    }

    @Test
    void deletePerTrainerUsername_deletesAndSyncsKeycloak_onCorrectPassword() {
        PerTrainer trainer = existingTrainer();
        when(perTrainerRepository.existsByUsername("coach99")).thenReturn(Mono.just(true));
        when(perTrainerRepository.findByUsername("coach99")).thenReturn(Mono.just(trainer));
        when(passwordEncoder.matches("correct", trainer.getPassword())).thenReturn(true);
        when(perTrainerRepository.deleteByUsername("coach99")).thenReturn(Mono.empty());
        when(keycloakService.deleteUser("coach99")).thenReturn(Mono.just("The user was deleted successfully!"));

        StepVerifier.create(service.deletePerTrainerUsername("coach99", "correct"))
                .expectNext("account deleted successfully")
                .verifyComplete();
    }
}
