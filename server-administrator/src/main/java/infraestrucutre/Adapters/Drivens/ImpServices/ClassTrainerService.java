package infraestrucutre.Adapters.Drivens.ImpServices;

import org.springframework.security.authentication.password.CompromisedPasswordDecision;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiReactivePasswordChecker;
import org.springframework.stereotype.Service;

import application.Ports.Drivens.RepositoriesInterfaces.WorkTrainerRepositoryInterface;
import application.Ports.Drivers.IServices.ClassTrainerServiceInterface;
import infraestrucutre.Adapters.Drivens.DTOS.DtoDetailUserReciving;
import infraestrucutre.Adapters.Drivens.DTOS.DtoSpecialtyRecived;
import infraestrucutre.Adapters.Drivens.DTOS.DtoTrainerData;
import infraestrucutre.Adapters.Drivens.Entities.AllTrainer;
import infraestrucutre.Adapters.Drivens.Entities.DetailsUser;
import infraestrucutre.Adapters.Drivens.Entities.WorkClass;
import lombok.AllArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@AllArgsConstructor
public class ClassTrainerService implements ClassTrainerServiceInterface {

    private final WorkTrainerRepositoryInterface classTrainerRepository;

    // Method to create a new trainer
    @Override
    public Mono<String> createClassTrainer(AllTrainer trainer) {
        return classTrainerRepository.createClassTrainer(trainer);
    }

    // Method to get all trainers with pagination
    @Override
    public Flux<DtoTrainerData> getAllTrainers(int page, int size) {
        return classTrainerRepository.getAllTrainers(page, size);
    }

    // Method to get all specialties of a specific trainer
    @Override
    public Mono<List<DtoSpecialtyRecived>> getAllSpecialtiesFromTrainer(String username) {
        return classTrainerRepository.getAllSpecialtiesFromTrainer(username);
    }

    // Method to update trainer's detailed information
    @Override
    public Mono<String> updateTrainerAllDetailInformation(String username, DtoDetailUserReciving details) {
        return classTrainerRepository.updateTrainerAllDetailInformation(username, details);
    }

    // Method to get work classes by trainer
    @Override
    public Mono<List<WorkClass>> getWorkClassesByTrainer(String username) {
        return classTrainerRepository.getWorkClassesByTrainer(username);
    }

    // Method to update trainer's password
    @Override
    public Mono<String> updateTrainerPassword(String username, String oldPassword, String newPassword) {
        return classTrainerRepository.updateTrainerPassword(username, oldPassword, newPassword);
    }

    // Method to update trainer's username
    @Override
    public Mono<String> updateTrainerUsername(String oldUsername, String newUsername) {
        return classTrainerRepository.updateTrainerUsername(oldUsername, newUsername);
    }

    // Method to update trainer's email
    @Override
    public Mono<String> updateEmail(String username, String newEmail) {
        return classTrainerRepository.updateEmail(username, newEmail);
    }

    // Method to add a specialty to a trainer
    @Override
    public Mono<String> addSpecialtyToTrainer(String username, String newSpecialty) {
        return classTrainerRepository.addSpecialtyToTrainer(username, newSpecialty);
    }

    // Method to dissociate a specialty from a trainer
    @Override
    public Mono<String> dessociateSpecialty(String username, String specialty) {
        return classTrainerRepository.dessociateSpecialty(username, specialty);
    }

    // Method to add a class to a trainer
    @Override
    public Mono<String> addClassToTrainer(String username, String newClass) {
        return classTrainerRepository.addClassToTrainer(username, newClass);
    }

    // Method to dissociate a class from a trainer
    @Override
    public Mono<String> dessociateClassToTrainer(String username, String className) {
        return classTrainerRepository.dessociateClassToTrainer(username, className);
    }

    // Method to delete a trainer by username with password
    @Override
    public Mono<String> deleteClassTrainerByUsernameWithPassword(String username, String password) {
        return classTrainerRepository.deleteClassTrainerByUsernameWithPassword(username, password);
    }

    // Validate if the password has been compromised
  
}
