package application.Ports.Drivens.RepositoriesInterfaces;

import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import java.util.List;

import infraestrucutre.Adapters.Drivens.DTOS.DtoDetailUserSent;
import infraestrucutre.Adapters.Drivens.Entities.AllClient;
import infraestrucutre.Adapters.Drivens.Entities.WorkClass;

public interface ClientRepositoryInterface {

    Mono<List<AllClient>> getAllClients();

    Mono<String> updateClient(String username, DtoDetailUserSent client);

    Mono<String> changePassword(String username, String oldPassword, String newPassword);

    Mono<String> changeEmail(String username, String email);

    Mono<String> changeUsername(String username, String newUsername);

    Mono<String> changeMembership(String username, String membershipType);

    public Mono<String> unassignMembership(String username, String membershipType);

    Mono<String> changeTrainer(String username, String usernameTrainer);

    Mono<String> unassignTrainer(String username, String usernameTrainer);

    Mono<List<WorkClass>> getWorkClassesByClientId(String username);

    Mono<Boolean> validateIfUserNameExistsClient(String username);

    Mono<Boolean> validateIfEmailExistsClient(String email);

    Mono<String> deleteClient(String username, String password);

    Mono<String> createClient(AllClient client);

    Mono<Boolean> validatePasswordRegister(String password);
}