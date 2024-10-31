package application.Ports.Drivers.IServices;

import java.util.List;

import infraestrucutre.Adapters.Drivens.DTOS.DtoDetailUserSent;
import infraestrucutre.Adapters.Drivens.DTOS.DtoInfoGeneralClient;
import infraestrucutre.Adapters.Drivens.DTOS.DtoSpecialtyRecived;
import infraestrucutre.Adapters.Drivens.Entities.AllClient;
import infraestrucutre.Adapters.Drivens.Entities.WorkClass;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import reactor.core.publisher.Mono;
import java.util.List;

import reactor.core.publisher.Mono;
import java.util.List;

public interface ClientServiceInterface {

    Mono<List<AllClient>> getAllClients();


    Mono<String> deleteClient(String username, String password);

    Mono<String> createClient(AllClient client);

    Mono<Boolean> validateIfUserNameExistsClient(String username);

    Mono<Boolean> validateIfEmailExistsClient(String email);


    Mono<String> changePassword(String username, String oldPassword, String newPassword);

    Mono<String> changeEmail(String username, String email);

    Mono<String> changeUsername(String username, String newUsername);

    Mono<String> updateAllInformation(DtoDetailUserSent newInformation, String username);

    Mono<Boolean> validatePasswordRegister(String password);

    Mono<String> changeMembership(String username, String membershipType);

    Mono<String> changeTrainer(String username, String usernameTrainer);

    Mono<String> removeMembership(String username, String membershipType);

    Mono<String> removeTrainer(String username, String usernameTrainer);

    Mono<List<WorkClass>> getWorkClassesByClientId(String username);
}


