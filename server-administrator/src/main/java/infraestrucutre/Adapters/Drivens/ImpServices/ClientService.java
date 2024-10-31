package infraestrucutre.Adapters.Drivens.ImpServices;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import application.Ports.Drivens.RepositoriesInterfaces.ClientRepositoryInterface;
import application.Ports.Drivers.IServices.ClientServiceInterface;
import ch.qos.logback.core.net.server.Client;
import infraestrucutre.Adapters.Drivens.DTOS.DtoDetailUserSent;
import infraestrucutre.Adapters.Drivens.DTOS.DtoInfoGeneralClient;
import infraestrucutre.Adapters.Drivens.Entities.AllClient;
import infraestrucutre.Adapters.Drivens.Entities.WorkClass;
import infraestrucutre.Adapters.Drivens.Repositories.ClientRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.stereotype.Service;
import lombok.Data;
import reactor.core.publisher.Mono;
import java.util.ArrayList;
import java.util.List;

@Service
@Data
    public class ClientService implements ClientServiceInterface {
    private final ClientRepositoryInterface clientRepository;

    @Override
    public Mono<List<AllClient>> getAllClients() {
        return clientRepository.getAllClients();
    }

    @Override
    public Mono<List<WorkClass>> getWorkClassesByClientId(String username) {
        return clientRepository.getWorkClassesByClientId(username);
    }

    @Override
    public Mono<String> deleteClient(String username, String password) {
        return clientRepository.deleteClient(username, password);
    }

    @Override
    public Mono<String> createClient(AllClient client) {
        return clientRepository.createClient(client);
    }

    @Override
    public Mono<Boolean> validateIfUserNameExistsClient(String username) {
        return clientRepository.validateIfUserNameExistsClient(username);
    }

    @Override
    public Mono<Boolean> validateIfEmailExistsClient(String email) {
        return clientRepository.validateIfEmailExistsClient(email);
    }

    

    @Override
    public Mono<String> changePassword(String username, String oldPassword, String newPassword) {
        return clientRepository.changePassword(username, oldPassword, newPassword);
    }

    @Override
    public Mono<String> changeEmail(String username, String email) {
        return clientRepository.changeEmail(username, email);
    }

    @Override
    public Mono<String> changeUsername(String username, String newUsername) {
        return clientRepository.changeUsername(username, newUsername);
    }

    @Override
    public Mono<String> updateAllInformation(DtoDetailUserSent newInformation, String username) {
        return clientRepository.updateClient( username,newInformation);
    }

    @Override
    public Mono<Boolean> validatePasswordRegister(String password) {
        return clientRepository.validatePasswordRegister(password);
    }

    @Override
    public Mono<String> changeMembership(String username, String membershipType) {
        return clientRepository.changeMembership(username, membershipType);
    }

    @Override
    public Mono<String> changeTrainer(String username, String usernameTrainer) {
        return clientRepository.changeTrainer(username, usernameTrainer);
    }

    @Override
    public Mono<String> removeMembership(String username, String membershipType) {
        return clientRepository.unassignMembership(username, membershipType);
    }

    @Override
    public Mono<String> removeTrainer(String username, String usernameTrainer) {
        return clientRepository.unassignTrainer(username, usernameTrainer);
    }
}
