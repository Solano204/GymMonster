package application.Ports.Drivens.InterfaceRepositories;

import reactor.core.publisher.Mono;
import java.util.List;

import infraestrucutre.Adapters.Drivens.DTOS.DtoDetailUserSent;
import infraestrucutre.Adapters.Drivens.Entities.AllClient;
import infraestrucutre.Adapters.Drivens.Entities.WorkClass;

public interface ClientRepositoryInterface  {

    Mono<String> registerNewClient(AllClient newClient);

    Mono<String> unassignTrainer(String username, String usernameTrainer);

    Mono<List<WorkClass>> getWorkClassesByClient(String username);

    Mono<AllClient> getClientData(String username);

    Mono<String> unassignMembership(String username, String membershipType);

    Mono<String> updateClient(String username, AllClient client);

    Mono<String> deleteClient(String username, String password);

    Mono<String> createClient(AllClient client);

    Mono<Boolean> validateIfUserNameExistsClient(String username);

    Mono<Boolean> validateIfEmailExistsClient(String email);

    Mono<Boolean> validateOldPasswordMatchers(String username, String password);

    Mono<String> updateClient(String username, DtoDetailUserSent client);

    Mono<String> changePassword(String username, String oldPassword, String newPassword);

    Mono<String> changeEmail(String username, String email);

    Mono<String> updateAllInformation(DtoDetailUserSent newInformation, String username);

    Mono<Boolean> validatePasswordRegister(String password);

    Mono<String> changeUsername(String username, String newUsername);

    Mono<String> changeMembership(String username, String membershipType);

    Mono<String> changeTrainer(String username, String usernameTrainer);
}
