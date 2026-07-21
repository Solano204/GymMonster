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
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.security.crypto.password.PasswordEncoder;

import infraestrucutre.Adapters.Drivens.DTOS.DtoDataReciving;
import infraestrucutre.Adapters.Drivens.DTOS.DtoKeyCloakUser;
import infraestrucutre.Adapters.Drivens.Entities.AllTrainer;
import infraestrucutre.Adapters.Drivens.Entities.ClassTrainer;
import infraestrucutre.Adapters.Drivens.Entities.DetailsClassTrainer;
import infraestrucutre.Adapters.Drivens.Entities.Specialty;
import infraestrucutre.Adapters.Drivens.Entities.WorkClass;
import infraestrucutre.Adapters.Drivens.Repositories.ClassTrainerRepository;
import infraestrucutre.Adapters.Drivens.Repositories.DetailTrainerClassRepository;
import infraestrucutre.Adapters.Drivens.Repositories.SpecialtyRepository;
import infraestrucutre.Adapters.Drivens.Repositories.WorkClassRepository;
import infraestrucutre.Adapters.Drivens.Validations.LogicInterfaces.ValidatePassword;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * ClassTrainerService duplicates most of PerTrainerService's shape (see PerTrainerServiceTest for
 * the password/username/email verification branches, tested thoroughly there). This focuses on
 * what's actually different here: the createPerTrainer validation zip, the 2-arg delete-with-password
 * overload, and the work-class association methods PerTrainerService doesn't have at all.
 */
class ClassTrainerServiceTest {

    @Mock private ClassTrainerRepository classTrainerRepository;
    @Mock private SpecialtyRepository specialtyRepository;
    @Mock private WorkClassRepository workClassRepository;
    @Mock private DatabaseClient databaseClient;
    @Mock private R2dbcEntityTemplate r2dbcEntityTemplate;
    @Mock private ValidatePassword validatePasswordRegister;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private DetailTrainerClassRepository detailTrainerRepository;
    @Mock private KeycloakServiceImpl keycloakService;
    @Mock private FillinKeycloakUser fillinKeycloakUser;

    private ClassTrainerService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new ClassTrainerService(classTrainerRepository, specialtyRepository, workClassRepository,
                databaseClient, r2dbcEntityTemplate, validatePasswordRegister, passwordEncoder,
                detailTrainerRepository, keycloakService, fillinKeycloakUser);
    }

    private AllTrainer validTrainer() {
        return new AllTrainer(null, "ctcoach", "S3cur3P@ssword!", "ctcoach@test.com", "Ana", "M",
                "Lopez", "Diaz", "35", "170", "65", LocalDate.now());
    }

    private ClassTrainer existingTrainer() {
        ClassTrainer trainer = new ClassTrainer();
        trainer.setId(1L);
        trainer.setId_detail(2L);
        trainer.setUsername("ctcoach");
        trainer.setPassword("encoded-old-password");
        trainer.setEmail("ctcoach@test.com");
        return trainer;
    }

    @Test
    void createPerTrainer_reportsDuplicateDetailInfo() {
        when(classTrainerRepository.existsByUsername(anyString())).thenReturn(Mono.just(false));
        when(classTrainerRepository.existsByEmail(anyString())).thenReturn(Mono.just(false));
        when(validatePasswordRegister.validatePasswordRegister(anyString())).thenReturn(Mono.just(false));
        when(detailTrainerRepository.countByDetailInfo(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Mono.just(1));

        StepVerifier.create(service.createPerTrainer(validTrainer()))
                .assertNext(errors -> assertThat(errors).anyMatch(e -> e.contains("already exists with that data")))
                .verifyComplete();

        verify(classTrainerRepository, never()).save(any());
    }

    @Test
    void createPerTrainer_savesDetailThenTrainerThenCreatesKeycloakUser_onSuccess() {
        when(classTrainerRepository.existsByUsername(anyString())).thenReturn(Mono.just(false));
        when(classTrainerRepository.existsByEmail(anyString())).thenReturn(Mono.just(false));
        when(validatePasswordRegister.validatePasswordRegister(anyString())).thenReturn(Mono.just(false));
        when(detailTrainerRepository.countByDetailInfo(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Mono.just(0));

        DetailsClassTrainer savedDetail = new DetailsClassTrainer();
        savedDetail.setId(2L);
        when(detailTrainerRepository.save(any(DetailsClassTrainer.class))).thenReturn(Mono.just(savedDetail));
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-pw");
        when(classTrainerRepository.save(any(ClassTrainer.class))).thenReturn(Mono.just(existingTrainer()));
        when(fillinKeycloakUser.fillinDataFromTrainer(any())).thenReturn(
                new DtoDataReciving("ctcoach", "ctcoach@test.com", "S3cur3P@ssword!", "Ana", "M", "Lopez", "Diaz", "35", "170", "65"));
        when(fillinKeycloakUser.fillinKeycloakUser(any())).thenReturn(new DtoKeyCloakUser());
        when(keycloakService.createUser(any())).thenReturn(Mono.just(new DtoKeyCloakUser()));

        StepVerifier.create(service.createPerTrainer(validTrainer()))
                .assertNext(result -> assertThat(result).containsExactly(
                        "Congratulations, your account has been created. You can now log in."))
                .verifyComplete();
    }

    @Test
    void deleteClassTrainerUsername_withPassword_rejectsWrongPassword() {
        ClassTrainer trainer = existingTrainer();
        when(classTrainerRepository.existsByUsername("ctcoach")).thenReturn(Mono.just(true));
        when(classTrainerRepository.findByUsername("ctcoach")).thenReturn(Mono.just(trainer));
        when(passwordEncoder.matches("wrong", trainer.getPassword())).thenReturn(false);

        StepVerifier.create(service.deleteClassTrainerUsername("ctcoach", "wrong"))
                .expectNext("Old password is not correct")
                .verifyComplete();

        verify(classTrainerRepository, never()).deleteByUsername(anyString());
    }

    @Test
    void deleteClassTrainerUsername_withPassword_deletesAndSyncsKeycloak_onCorrectPassword() {
        ClassTrainer trainer = existingTrainer();
        when(classTrainerRepository.existsByUsername("ctcoach")).thenReturn(Mono.just(true));
        when(classTrainerRepository.findByUsername("ctcoach")).thenReturn(Mono.just(trainer));
        when(passwordEncoder.matches("correct", trainer.getPassword())).thenReturn(true);
        when(classTrainerRepository.deleteByUsername("ctcoach")).thenReturn(Mono.empty());
        when(keycloakService.deleteUser("ctcoach")).thenReturn(Mono.just("The user was deleted successfully!"));

        StepVerifier.create(service.deleteClassTrainerUsername("ctcoach", "correct"))
                .expectNext("account deleted successfully")
                .verifyComplete();
    }

    @Test
    void addClassToTrainer_reportsWhenClassAlreadyAssociated() {
        ClassTrainer trainer = existingTrainer();
        when(classTrainerRepository.existsByUsername("ctcoach")).thenReturn(Mono.just(true));
        when(classTrainerRepository.findByUsername("ctcoach")).thenReturn(Mono.just(trainer));
        when(classTrainerRepository.existsClassForTrainer(1L, "Yoga101")).thenReturn(Mono.just(1));

        StepVerifier.create(service.addClassToTrainer("ctcoach", "Yoga101"))
                .expectNext("The class already exist for the trainer")
                .verifyComplete();
    }

    @Test
    void addClassToTrainer_addsAssociation_onSuccess() {
        ClassTrainer trainer = existingTrainer();
        WorkClass workClass = new WorkClass();
        workClass.setId(9L);
        workClass.setName("Yoga101");
        when(classTrainerRepository.existsByUsername("ctcoach")).thenReturn(Mono.just(true));
        when(classTrainerRepository.findByUsername("ctcoach")).thenReturn(Mono.just(trainer));
        when(classTrainerRepository.existsClassForTrainer(1L, "Yoga101")).thenReturn(Mono.just(0));
        when(workClassRepository.findByName("Yoga101")).thenReturn(Mono.just(workClass));
        when(classTrainerRepository.addClassToTrainer(1L, 9L)).thenReturn(Mono.empty());

        StepVerifier.create(service.addClassToTrainer("ctcoach", "Yoga101"))
                .expectNext("Class removed successfully")
                .verifyComplete();
    }

    @Test
    void dessociateClassToTrainer_reportsWhenNotAssociated() {
        ClassTrainer trainer = existingTrainer();
        when(classTrainerRepository.existsByUsername("ctcoach")).thenReturn(Mono.just(true));
        when(classTrainerRepository.findByUsername("ctcoach")).thenReturn(Mono.just(trainer));
        when(classTrainerRepository.existsClassForTrainer(1L, "Yoga101")).thenReturn(Mono.just(0));

        StepVerifier.create(service.dessociateClassToTrainer("ctcoach", "Yoga101"))
                .expectNext("The class does not exist for the trainer")
                .verifyComplete();
    }

    @Test
    void getAllSpecialtyOfTrainer_looksUpTrainerThenDelegatesToRepository() {
        ClassTrainer trainer = existingTrainer();
        when(classTrainerRepository.findByUsername("ctcoach")).thenReturn(Mono.just(trainer));
        when(classTrainerRepository.findAllSpecialty(1L)).thenReturn(reactor.core.publisher.Flux.just(new Specialty(5L, "Yoga", "desc")));

        StepVerifier.create(service.getAllSpecialtyOfTrainer("ctcoach")).expectNextCount(1).verifyComplete();
    }
}
