package infraestrucutre.Adapters.Drivens.ImpServices;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.password.CompromisedPasswordDecision;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiReactivePasswordChecker;
import org.springframework.stereotype.Service;

import application.Ports.Drivens.RepositoriesInterfaces.PerTrainerRepositoryInterface;
import application.Ports.Drivers.IServices.PerTrainerInterface;
import infraestrucutre.Adapters.Drivens.DTOS.DtoDetailUserReciving;
import infraestrucutre.Adapters.Drivens.DTOS.DtoSpecialtyRecived;
import infraestrucutre.Adapters.Drivens.DTOS.DtoTrainerData;
import infraestrucutre.Adapters.Drivens.Entities.AllTrainer;
import infraestrucutre.Adapters.Drivens.Entities.WorkClass;
import infraestrucutre.Adapters.Drivens.Repositories.ClientRepository;
import infraestrucutre.Adapters.Drivens.Repositories.PerTrainerRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import lombok.AllArgsConstructor;

import java.util.List;

@Service
@AllArgsConstructor
public class PerTrainerImpl implements PerTrainerInterface {


     private final PerTrainerRepositoryInterface clientRepository; // Inject your repository

    @Override
    public Mono<String> createPerTrainer(AllTrainer newTrainer) {
        return clientRepository.createPerTrainer(newTrainer);
    }

    @Override
    public Flux<DtoTrainerData> getAllTrainers(int page, int size) {
        return clientRepository.getAllTrainers(page, size);
    }

    @Override
    public Mono<String> deletePerTrainer(String username, String password) {
        return clientRepository.deletePerTrainer(username, password);
    }

    @Override
    public Flux<DtoDetailUserReciving> getAllClientsFromTrainer(String username, int page, int size) {
        return clientRepository.getAllClientsFromTrainer(username, page, size);
    }

    @Override
    public Mono<List<DtoSpecialtyRecived>> getAllSpecialtiesFromTrainer(String username) {
        return clientRepository.getAllSpecialtiesFromTrainer(username);
    }

    @Override
    public Mono<String> updateTrainerPassword(String username, String newPassword, String oldPassword) {
        return clientRepository.updateTrainerPassword(username, newPassword, oldPassword);
    }

    @Override
    public Mono<String> updateEmail(String username, String newEmail) {
        return clientRepository.updateEmail(username, newEmail);
    }

    @Override
    public Mono<String> updateTrainerUsername(String oldUsername, String newUsername) {
        return clientRepository.updateTrainerUsername(oldUsername, newUsername);
    }

    @Override
    public Mono<String> updateTrainerDetails(String username, DtoDetailUserReciving details) {
        return clientRepository.updateTrainerDetails(username, details);
    }

    @Override
    public Mono<String> addSpecialtyToTrainer(String username, String newSpecialty) {
        return clientRepository.addSpecialtyToTrainer(username, newSpecialty);
    }

    @Override
    public Mono<String> removeSpecialtyFromTrainer(String username, String specialty) {
        return clientRepository.removeSpecialtyFromTrainer(username, specialty);
    }
}
