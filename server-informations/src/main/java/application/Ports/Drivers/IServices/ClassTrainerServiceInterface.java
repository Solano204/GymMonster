package application.Ports.Drivers.IServices;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;

import infraestrucutre.Adapters.Drivens.DTOS.DtoInfoTrainer;
import infraestrucutre.Adapters.Drivens.Entities.AllTrainer;
import infraestrucutre.Adapters.Drivens.Entities.ClassTrainer;
import infraestrucutre.Adapters.Drivens.Entities.DetailsClassTrainer;
import infraestrucutre.Adapters.Drivens.Entities.Specialty;
    import infraestrucutre.Adapters.Drivens.Entities.WorkClass;

    public interface ClassTrainerServiceInterface {

        // Method to create a trainer
        Mono<List<String>> createPerTrainer(AllTrainer trainer);

        // Method to get all trainers with pagination
        Flux<ClassTrainer> getAllClassTrainers(int page, int size);

        // Method to get all trainers with detailed information with pagination
        Flux<DtoInfoTrainer> getAllPerTrainersAllInformation(int page, int size);

        // Method to get all specialties of a trainer by username
        Flux<Specialty> getAllSpecialtyOfTrainer(String username);

        // Method to get a specific trainer by ID
    Mono<ClassTrainer> getClassTrainerById(Long id);

    // Method to get a specific trainer by username
    Mono<ClassTrainer> getClassTrainerByUsername(String username);

    

    // Method to delete a trainer by ID
    Mono<Void> deleteClassTrainerId(Long id);

    // Method to delete a trainer by username
    Mono<Void> deleteClassTrainerUsername(String username);

    // Method to delete a trainer by username and password
    Mono<String> deleteClassTrainerUsername(String username, String password);

    // Method to get work classes by a trainer's username
    Flux<WorkClass> getWorkClassesByTrainer(String username);

    // Method to update detailed information of a trainer by username
    Mono<String> updateTrainerAllDetailInformation(String username, DetailsClassTrainer newDatUser);

    // Method to update a trainer's password
    Mono<String> updateTrainerPassword(String username, String newPassword, String oldPassword);

    // Method to update a trainer's username
    public Mono<String> updateTrainerUsername(String oldUsername, String newUsername, String password);

    // Method to update a trainer's email
    Mono<String> updateClientEmail(String username, String newEmail);

    // Method to add a specialty to a trainer
    Mono<String> addSpecialtyToTrainer(String username, String newSpecialty);
    Mono<String> dessociateSpecialty(String username, String newSpecialty);
    Mono<String> addClassToTrainer(String username, String newClass);
    Mono<String> dessociateClassToTrainer(String username, String newClass);

}
