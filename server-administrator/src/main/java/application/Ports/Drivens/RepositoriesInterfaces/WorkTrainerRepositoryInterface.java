package application.Ports.Drivens.RepositoriesInterfaces;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import infraestrucutre.Adapters.Drivens.DTOS.DtoDetailUserReciving;
import infraestrucutre.Adapters.Drivens.DTOS.DtoSpecialtyRecived;
import infraestrucutre.Adapters.Drivens.DTOS.DtoTrainerData;
import infraestrucutre.Adapters.Drivens.Entities.AllTrainer;
import infraestrucutre.Adapters.Drivens.Entities.WorkClass;

public interface WorkTrainerRepositoryInterface {

    Mono<String> createClassTrainer(AllTrainer trainer);

    Flux<DtoTrainerData> getAllTrainers(int page, int size);

    Mono<List<DtoSpecialtyRecived>> getAllSpecialtiesFromTrainer(String username);

    Mono<String> updateTrainerAllDetailInformation(String username, DtoDetailUserReciving details);

    Mono<List<WorkClass>> getWorkClassesByTrainer(String username);

    Mono<String> updateTrainerPassword(String username, String oldPassword, String newPassword);

    Mono<String> updateTrainerUsername(String oldUsername, String newUsername);

    Mono<String> updateEmail(String username, String newEmail);

    Mono<String> addSpecialtyToTrainer(String username, String newSpecialty);

    Mono<String> dessociateSpecialty(String username, String specialty);

    Mono<String> addClassToTrainer(String username, String newClass);

    Mono<String> dessociateClassToTrainer(String username, String className);

    Mono<String> deleteClassTrainerByUsernameWithPassword(String username, String password);
}
