package infraestrucutre.Adapters.Drivens.ImpServices;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.security.authentication.password.CompromisedPasswordDecision;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiReactivePasswordChecker;
import org.springframework.stereotype.Service;

import domain.Entities.DetailsUser;
import infraestrucutre.Adapters.Drivens.DTOS.DtoDataReciving;
import infraestrucutre.Adapters.Drivens.DTOS.DtoDetailUserSent;
import infraestrucutre.Adapters.Drivens.DTOS.DtoKeyCloakUser;
import infraestrucutre.Adapters.Drivens.Entities.AllClient;
import infraestrucutre.Adapters.Drivens.Entities.Client;
import infraestrucutre.Adapters.Drivens.Entities.DetailUser;
import infraestrucutre.Adapters.Drivens.Entities.Inscription;
import infraestrucutre.Adapters.Drivens.Entities.WorkClass;
import infraestrucutre.Adapters.Drivens.IServices.ClientServiceInterface;
import infraestrucutre.Adapters.Drivens.IServices.IEmailService;
import infraestrucutre.Adapters.Drivens.Repositories.ClientRepository;
import infraestrucutre.Adapters.Drivens.Repositories.DetailClientRepository;
import infraestrucutre.Adapters.Drivens.Repositories.InscriptionRepository;
import infraestrucutre.Adapters.Drivens.Repositories.MembershipRepository;
import infraestrucutre.Adapters.Drivens.Repositories.PerTrainerRepository;
import infraestrucutre.Adapters.Drivens.Validations.LogicInterfaces.RegisterGeneralValidations;
import infraestrucutre.Adapters.Drivens.Validations.LogicInterfaces.ValidatePassword;
import lombok.AllArgsConstructor;
import lombok.Data;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Data
@AllArgsConstructor
public class RegisterService implements ClientServiceInterface {

    private final PasswordEncoder passwordEncoder;
    private final ClientRepository clientRepository;
    private final MembershipRepository membershipRepository;
    private final DetailClientRepository detailClientRepository;
    private final PerTrainerRepository perTrainerRepository;
    private final ValidatePassword validatePasswordRegister;
    private final InscriptionRepository inscriptionRepository;
    private final KeycloakServiceImpl keycloakService;
    private final FillinKeycloakUser fillinKeycloakUser;
    private final IEmailService emailService;

    @KafkaListener(topics = "flow", containerFactory = "kafkaListenerContainerFactory", groupId = "grupo1")
    public void consumer(AllClient newClient) {
        System.out.println("Received Client created event "+ newClient.age());
        createClient(newClient).flatMap(response -> {
            for (String error : response) {
                System.out.println(error);
            }
            return Mono.just(response);
        }).subscribe();
    }

      @Override
public Mono<List<String>> createClient(AllClient client) {
    List<String> errores = new ArrayList<>();
    
    System.out.println("createClient");

    // Validate all conditions in parallel using Mono.zip
    Mono<Boolean> usernameValidation = clientRepository.existsByUsername(client.username())
            .doOnNext(usernameExists -> {
                if (usernameExists) {
                    errores.add("I'm sorry, the username already exists. You need to choose another one.");
                }
            });
    
    Mono<Boolean> emailValidation = clientRepository.existsByEmail(client.email())
            .doOnNext(emailExists -> {
                if (emailExists) {
                    errores.add("I'm sorry, that email already exists. Please choose another one.");
                }
            });
   

    // Perform basic synchronous field validation
    boolean basicFieldValidation = isValidBasciFields(client, errores);

    // Combine all validations
    return Mono.zip(usernameValidation, emailValidation)
            .flatMap(results -> {
                if (!basicFieldValidation || !errores.isEmpty()) {
                    return Mono.just(errores); // Return errors if fields are invalid or errors exist
                }

                // Check if client data already exists based on detail info
                return detailClientRepository.countByDetailInfo(
                        client.name(),
                        client.secondname(),
                        client.lastnamem(),
                        client.lastnamep()
                )
                .flatMap(count -> {
                    if (count > 0) {
                        errores.add("A user already exists with that data (name, secondName, lastNameM, lastNameP).");
                    }
                    
                    // If there are errors at this point, return them
                    if (!errores.isEmpty()) {
                        return Mono.just(errores);
                    }

                    // No errors, proceed to create a new client
                    DetailUser detailUser = fillDetailUser(client);
                    
                    // Save the new client details
                    return detailClientRepository.save(detailUser)
                            .flatMap(savedDetailUser -> {
                                // Fill and save the Client with detail user ID
                                return fillClient(client, savedDetailUser.getId())
                                        .flatMap(savedClient -> {
                                            return clientRepository.save(savedClient)
                                                    .flatMap(savedClient2 -> {
                                                        Inscription inscription = fillInscription(client, savedClient2.getId());
                                                        DtoDataReciving dtoDataReciving =fillinKeycloakUser.fillinDataFromClient(client);
                                                        DtoKeyCloakUser newKeyCloakUser = fillinKeycloakUser.fillinKeycloakUser(dtoDataReciving);
                                                        newKeyCloakUser.setRoles(Set.of("Client"));
                                                       
                                                        return inscriptionRepository.save(inscription)
                                                        .then( keycloakService.createUser(newKeyCloakUser))
                                                        .then(emailService.sendEmail(new String[]{client.email()}, client.username(), client.password()))
                                                                .then(Mono.just(List.of("Congratulations, we send a confirmation email to " + client.email() + "and you already can login with its account Thanks.")));
                                                    });
                                        });
                            });
                });
            })
            // Catch unexpected errors and add to the error list
            .onErrorResume(ex -> {
                errores.add("An error occurred: " + ex.getMessage());
                return Mono.just(errores);
            });
}
    

    
    private Inscription fillInscription(AllClient client, Long clientId) {
    Inscription inscription = new Inscription();
    inscription.setId_client(Integer.parseInt(clientId.toString()));
    inscription.setStartDate(client.startdate());
    inscription.setStartInscription(client.startinscription());
    inscription.setEndInscription(client.endinscription());
    inscription.setPrice(client.price());
    return inscription;
}
    
private Mono<Client> fillClient(AllClient client, Long detailUserId) {
    Client newClient = new Client();

    return membershipRepository.findByMembershipType(client.membershiptype())
        .flatMap(membership -> {
            newClient.setId_membership(membership.getId());

            // Now fetch trainer info
            return perTrainerRepository.findByUsername(client.trainername());
        })
        .flatMap(trainer -> {
            newClient.setId_trainer(trainer.getId());

            // Now set other fields
            newClient.setId_detail(detailUserId); // The detailUserId passed from `createClient` method
            newClient.setUsername(client.username());
            newClient.setPassword(passwordEncoder.encode(client.password()));
            newClient.setEmail(client.email());

            // Finally, return the populated client
            // Finally, return the populated client
            return Mono.just(newClient);
        });
}

private DetailUser fillDetailUser(AllClient client) {
    DetailUser detailUser = new DetailUser();
    detailUser.setName(client.name());
    detailUser.setSecondName(client.secondname());
    detailUser.setLastNameM(client.lastnamem());
    detailUser.setLastNameP(client.lastnamep());
    detailUser.setAge(client.age());
    detailUser.setWeight(client.weight());
    detailUser.setHeight(client.height());

    return detailUser;
}


private boolean isValidBasciFields(AllClient client, List<String> errorMessages) {
    DtoDataReciving dtoDataReciving =fillinKeycloakUser.fillinDataFromClient(client);
        new RegisterGeneralValidations().validateBasicFields(dtoDataReciving, errorMessages);
        return errorMessages.isEmpty();
    }

}
