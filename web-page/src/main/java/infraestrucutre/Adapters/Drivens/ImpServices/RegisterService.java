package infraestrucutre.Adapters.Drivens.ImpServices;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import application.Ports.Drivens.InterfaceRepositories.ClientRepositoryInterface;
import application.Ports.Drivers.IServices.ClientServiceInterface;
import infraestrucutre.Adapters.Drivens.DTOS.DtoDetailUserSent;
import infraestrucutre.Adapters.Drivens.DTOS.DtoInfoGeneralClient;
import infraestrucutre.Adapters.Drivens.DTOS.DtoSpecialtySent;
import infraestrucutre.Adapters.Drivens.Entities.AllClient;
import infraestrucutre.Adapters.Drivens.Entities.WorkClass;
import infraestrucutre.Adapters.Drivens.Repositories.ClientRepository;
import infraestrucutre.Adapters.Drivens.Validations.LogicInterfaces.RegisterGeneralValidations;
import lombok.AllArgsConstructor;
import lombok.Data;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Data
public class RegisterService implements ClientServiceInterface {

    private final ClientRepositoryInterface clientRepository;

    @Override
public Mono<List<String>> createClient(AllClient client) {
    List<String> errores = new ArrayList<>();

    System.out.println("createClient");

    // Validate if username exists
    return clientRepository.validateIfUserNameExistsClient(client.username())
            .flatMap(usernameExists -> {
                if (usernameExists) {
                    errores.add("The username already exists");
                }

                // Validate if email exists
                return clientRepository.validateIfEmailExistsClient(client.email())
                        .flatMap(emailExists -> {
                            if (emailExists) {
                                errores.add("The email already exists");
                            }

                            // Validate if password is secure
                            return clientRepository.validatePasswordRegister(client.password())
                                    .flatMap(isPasswordInsecure -> {
                                        if (isPasswordInsecure) {
                                            errores.add("The password is insecure");
                                        }

                                        // Perform basic synchronous field validation
                                        if (!isValidBasciFields(client, errores)) {
                                            return Mono.just(errores);
                                        }

                                        if (!errores.isEmpty()) {
                                            return Mono.just(errores);
                                        }

                                        // All validations passed, save the client
                                        return clientRepository.registerNewClient(client)
                                                .flatMap(messageSuccessful -> {
                                                    return Mono.just(List.of(messageSuccessful));
                                                });
                                    });
                        });
            })
            .switchIfEmpty(Mono.just(errores)) // Return errors if no username exists
            .onErrorResume(e -> {
                // Handle any errors in the chain and return a Mono with the error message
                errores.add("An error occurred: " + e.getMessage());
                return Mono.just(errores);
            });
}


    private boolean isValidBasciFields(AllClient client, List<String> errorMessages) {
        new RegisterGeneralValidations(clientRepository).validateBasicFields(client, errorMessages);
        return errorMessages.isEmpty();
    }

    @Override
    public Mono<String> updateClientPassword(String username, String newPassword, String oldPassword) {
        return clientRepository.validatePasswordRegister(newPassword)
                .flatMap(isPasswordInsecure -> {

                    if (isPasswordInsecure) {
                        return Mono.just("Password is insecure");
                    }

                    return clientRepository.changePassword(username, oldPassword, newPassword);
                });
    }

    @Override
    public Mono<String> updateClientUsername(String username, String newUsername) {
        return clientRepository.changeUsername(username, newUsername);
    }

    @Override
    public Mono<String> updateClientEmail(String username, String newEmail) {
        return clientRepository.changeEmail(username, newEmail);
    }

    @Override
    public Mono<String> updateClientMembership(String username, String membership) {
        return clientRepository.changeMembership(username, membership);
    }
    @Override
    public Mono<String> updateClientTrainer(String username, String membership) {
        return clientRepository.changeTrainer(username, membership);
    }

    @Override
    public Mono<String> updateClientAllDetailInformation(String username, DtoDetailUserSent newDatUser) {
        return clientRepository.updateAllInformation(newDatUser, username);
    }

    @Override
    public Mono<String> deleteAccount(String username, String password) {
        return clientRepository.deleteClient(username, password);
    }
    @Override
    public Mono<AllClient> getClient(String username) {
        return clientRepository.getClientData(username);
    }



    @Override
    public Mono<String> removeMembership(String username, String membershipType) {
        return clientRepository.unassignMembership(username, membershipType);
    }

    @Override
    public Mono<String> removeTrainer(String username, String usernameTrainer) {
        return clientRepository.unassignTrainer(username, usernameTrainer);
    }

    @Override
    public Mono<List<WorkClass>> getWorkClassesByClientId(String username) {
        return clientRepository.getWorkClassesByClient(username);
    }


}
