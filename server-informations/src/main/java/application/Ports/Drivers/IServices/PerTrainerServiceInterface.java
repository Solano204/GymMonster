package application.Ports.Drivers.IServices;
import java.util.List;

import infraestrucutre.Adapters.Drivens.DTOS.DtoInfoTrainer;
import infraestrucutre.Adapters.Drivens.Entities.AllTrainer;
import infraestrucutre.Adapters.Drivens.Entities.DetailPerTrainer;
import infraestrucutre.Adapters.Drivens.Entities.DetailUser;
import infraestrucutre.Adapters.Drivens.Entities.PerTrainer;
import infraestrucutre.Adapters.Drivens.Entities.Specialty;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PerTrainerServiceInterface {

    Mono<List<String>> createPerTrainer(AllTrainer trainer);

    Flux<PerTrainer> getAllPerTrainers(int page, int size);

    Flux<DtoInfoTrainer> getAllPerTrainersAllInformation(int page, int size);

    Mono<PerTrainer> getPerTrainerById(Long id);

    Mono<PerTrainer> getPerTrainerByUsername(String username);

    Mono<DtoInfoTrainer> getAllInfotTrainer(String username);

    Mono<DetailUser> getInfotTrainer(String username);

    Mono<PerTrainer> getPerTrainerByName(String username);

    Mono<PerTrainer> updatePerTrainer(String username, PerTrainer perTrainer);

    Mono<String> deletePerTrainerUsername(String username, String password);

    Mono<Boolean> existsByUsernameTrainer(String username);

    Mono<Boolean> existsByEmailTrainer(String email);

    Flux<DetailUser> getAllClients(String username, int page, int size);

    Flux<Specialty> getAllSpecialties(String username);

    Mono<String> updateTrainerAllDetailInformation(String username, DetailPerTrainer newDatUser);

    Mono<String> updateTrainerPassword(String username, String newPassword, String oldPassword);

    public Mono<String> updateTrainerUsername(String oldUsername, String newUsername, String password);

    Mono<String> updateClientEmail(String username, String newEmail);

    Mono<String> addSpecialtyToTrainer(String username, String specialty);
    Mono<String> dessociateSpecialty(String username, String specialty);

}
