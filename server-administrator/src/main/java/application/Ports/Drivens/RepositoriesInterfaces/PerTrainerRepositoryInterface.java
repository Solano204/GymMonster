package application.Ports.Drivens.RepositoriesInterfaces;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;

import infraestrucutre.Adapters.Drivens.DTOS.DtoDetailUserReciving;
import infraestrucutre.Adapters.Drivens.DTOS.DtoSpecialtyRecived;
import infraestrucutre.Adapters.Drivens.DTOS.DtoTrainerData;
import infraestrucutre.Adapters.Drivens.Entities.AllTrainer;

public interface PerTrainerRepositoryInterface {

    Mono<String> createPerTrainer(AllTrainer newTrainer);
    Flux<DtoTrainerData> getAllTrainers(int page, int size);
    Mono<String> deletePerTrainer(String username, String password);
    Flux<DtoDetailUserReciving> getAllClientsFromTrainer(String username, int page, int size);
    Mono<List<DtoSpecialtyRecived>> getAllSpecialtiesFromTrainer(String username);
    Mono<String> updateTrainerPassword(String username, String newPassword, String oldPassword);
    Mono<String> updateEmail(String username, String newEmail);
    Mono<String> updateTrainerUsername(String oldUsername, String newUsername);
    Mono<String> updateTrainerDetails(String username, DtoDetailUserReciving details);
    Mono<String> addSpecialtyToTrainer(String username, String newSpecialty);
    Mono<String> removeSpecialtyFromTrainer(String username, String specialty);
}