package infraestrucutre.Adapters.Drivens.ImpServices;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.relational.core.sql.In;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import application.Ports.Drivers.IServices.ClientServiceInterface;
import application.Ports.Drivers.IServices.IEmailService;
import infraestrucutre.Adapters.Drivens.DTOS.DtoDataReciving;
import infraestrucutre.Adapters.Drivens.DTOS.DtoInfoGeneralClient;
import infraestrucutre.Adapters.Drivens.DTOS.DtoKeyCloakUser;
import infraestrucutre.Adapters.Drivens.Entities.AllClient;
import infraestrucutre.Adapters.Drivens.Entities.Client;
import infraestrucutre.Adapters.Drivens.Entities.DetailPerTrainer;
import infraestrucutre.Adapters.Drivens.Entities.DetailUser;
import infraestrucutre.Adapters.Drivens.Entities.Inscription;
import infraestrucutre.Adapters.Drivens.Entities.WorkClass;
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
public class ClientService implements ClientServiceInterface {

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


    
     @Override
    public Mono<String> deleteClient(String username, String password) {
    return clientRepository.existsByUsername(username)
            .flatMap(existsOldUsername -> {
                if (!existsOldUsername) {
                    return Mono.just("Username does not exist");
                }

                // If the username exists, find the client by username
                return clientRepository.findByUsername(username)
                        .flatMap(clientExist -> {
                            // Check if the old password matches
                            if (passwordEncoder.matches(password, clientExist.getPassword())) {
                                // Delete inscriptions by client ID
                                return inscriptionRepository.deleteByClientId(clientExist.getId())
                                        .then(clientRepository.deleteById(clientExist.getId())) // Delete client by username
                                        .then(detailClientRepository.deleteById(clientExist.getId_detail()))
                                        .then(keycloakService.deleteUser(username)) // Delete detail by ID
                                        .then(Mono.just("Account deleted successfully")) // Return success message
                                        .onErrorResume(ex -> {
                                            // Handle any error during the deletion process
                                            return Mono.just("Error occurred during deletion: " + ex.getMessage());
                                        });
                            } else {
                                return Mono.just("Old password is not correct");
                            }
                        });
            });
}

     

    @Override
    public Mono<AllClient> getClientDetailMembershipByClientId(String username) {
        return getClientByUsername(username)
                .flatMap(client -> clientRepository.findClientDetailMembershipByClientId(client.getId()));
    }

    @Override
    public Flux<WorkClass> getWorkClassesByClientId(String username) {
        return getClientByUsername(username)
                .flatMapMany(client -> clientRepository.findWorkClassesByClientId(client.getId()));
    }


    @Override
    public Mono<Boolean> existsByUsernameClient(String username) {
        return clientRepository.existsByUsername(username);
    }

    @Override
    public Mono<Boolean> existsByEmailClient(String email) {
        return clientRepository.existsByEmail(email);
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
                                                                .then(Mono.just(List.of("Congratulations, your account has been created. You can now log in.")));
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

    

    private boolean isValidBasciFields(AllClient client, List<String> errorMessages) {
        DtoDataReciving dtoDataReciving =fillinKeycloakUser.fillinDataFromClient(client);
            new RegisterGeneralValidations().validateBasicFields(dtoDataReciving, errorMessages);
            return errorMessages.isEmpty();
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


private Inscription fillInscription(AllClient client, Long clientId) {
    Inscription inscription = new Inscription();
    inscription.setId_client(Integer.parseInt(clientId.toString()));
    inscription.setStartDate(client.startdate());
    inscription.setStartInscription(client.startinscription());
    inscription.setEndInscription(client.endinscription());
    inscription.setPrice(client.price());
    return inscription;
}

    
    

    @Override
public Flux<AllClient> getAllClientsAD(int page, int size) {
    int offset = page * size;
    System.out.println("Page: " + page + ", Size: " + size + ", Offset: " + offset);
    return clientRepository.findAllClientsAD(size, offset)
        .doOnNext(client -> System.out.println("Fetched client: " + client))
        .doOnError(error -> System.err.println("Error fetching clients: " + error.getMessage()));
}
 



    @Override
    public Mono<Client> getClientByUsername(String username) {
        return clientRepository.findByUsername(username);
    }

    @Override
    public Mono<Client> updateClient(String username, Client clients) {
        return clientRepository.findByUsername(username).flatMap(client -> {
            client.setEmail(clients.getEmail());
            client.setUsername(clients.getUsername());
            client.setPassword(clients.getPassword());
            return clientRepository.save(client);
        });
    }


@Override
public Mono<String> updateClientAllDetailInformation(String username, DetailUser newDatUser) {
    // Check if the username exists
    return clientRepository.existsByUsername(username)
        .flatMap(existsOldUsername -> {
            if (!existsOldUsername) {
                return Mono.just("Username does not exist");
            }

            // Get client detail by username
            return getClientDetailMembershipByClientId(username)
                .flatMap(dataExist -> {
                    if (dataExist != null) {
                        // Check if the combination of name, secondName, lastNameM, and lastNameP already exists
                        return detailClientRepository.countByDetailInfo(newDatUser.getName(), newDatUser.getSecondName(), newDatUser.getLastNameM(), newDatUser.getLastNameP())
                            .flatMap(count -> {
                                if (count > 0) {
                                    return Mono.just("A user already exists with that data (name, secondName, lastNameM, lastNameP)");
                                } else {
                                    // Find the client and update their details
                                    return clientRepository.findByUsername(username)
                                        .flatMap(existingClient -> detailClientRepository.findById(existingClient.getId_detail())
                                            .flatMap(detailExist -> {
                                                fillDetailClient(newDatUser, detailExist);
                                                return detailClientRepository.save(detailExist)
                                                .then(keycloakService.updateUserAllInformation(username,newDatUser))
                                                    .then(Mono.just("Data updated successfully"));
                                            })
                                        );
                                }
                            });
                    } else {
                        return Mono.just("Client details not found");
                    }
                });
        });
}

    public DetailUser fillDetailClient(DetailUser newDatUser, DetailUser detailExist) {
    detailExist.setName(newDatUser.getName());
                                                detailExist.setSecondName(newDatUser.getSecondName());
                                                detailExist.setLastNameM(newDatUser.getLastNameM());
                                                detailExist.setLastNameP(newDatUser.getLastNameP());
                                                detailExist.setAge(newDatUser.getAge());
                                                detailExist.setHeight(newDatUser.getHeight());
                                                detailExist.setWeight(newDatUser.getWeight());
                                                return detailExist;
}



    @Override
    public Mono<String> updateClientPassword(String username, String newPassword, String oldPassword) {
        String pass = newPassword;
        System.out.println("pass" + passwordEncoder.encode(oldPassword));
        // First, check if the username exists
        return clientRepository.existsByUsername(username)
                .flatMap(existsOldUsername -> {
                    if (!existsOldUsername) {
                        return Mono.just("Username does not exist");
                    }
                    // If the username exists, find the client by username
                    return clientRepository.findByUsername(username)
                            .flatMap(clientExist -> {
                                // Check if the old password matches
                                if (passwordEncoder.matches(oldPassword, clientExist.getPassword())) {
                                    
                                    // Update to the new password
                                    
                                    clientExist.setPassword(passwordEncoder.encode(newPassword));
                                    return clientRepository.save(clientExist)
                                            .then(keycloakService.changePassword(username, pass))
                                            .then(Mono.just("Password updated successfully"));
                                } else {
                                    return Mono.just("Old password is not correct");
                                }
                            });
                }).onErrorResume(ex -> {
                    String errores = ("An error occurred: " + ex.getMessage());
                    return Mono.just(errores);
                });
    }

    @Override
    public Mono<String> updateClientUsername(String oldUsername, String newUsername, String password) {
        // Check if old username exists in client repository
        return clientRepository.existsByUsername(oldUsername)
                .flatMap(existsOldUsername -> {
                    if (!existsOldUsername) {
                        return Mono.just("Old username does not exist");
                    }
    
                    // Check if new username already exists
                    return clientRepository.existsByUsername(newUsername)
                            .flatMap(existsNewUsername -> {
                                if (existsNewUsername) {
                                    return Mono.just("A user already exists with that new username");
                                }
    
                                // Fetch existing client details by old username
                                return clientRepository.findByUsername(oldUsername)
                                        .flatMap(existingClient -> {
                                            // Verify if the provided password matches
                                            if (!passwordEncoder.matches(password, existingClient.getPassword())) {
                                                return Mono.just("Password does not match");
                                            }
    
                                            // Update the username if password is correct
                                            existingClient.setUsername(newUsername);
                                            return clientRepository.save(existingClient)
                                                    .then(keycloakService.updateUsername2(newUsername, oldUsername, password,"Client"))
                                                    .then(Mono.just("Username updated successfully"));
                                        });
                            });
                })
                .onErrorResume(e -> {
                    return Mono.just("Error updating username");
                });
    }
    

    @Override
    public Mono<String> updateClientEmail(String username, String newEmail) {
        return clientRepository.existsByUsername(username)
                .flatMap(existsOldUsername -> {
                    if (!existsOldUsername) {
                        return Mono.just("username does not exist");
                    }

                    // Check if new email already exists
                    return clientRepository.existsByEmail(newEmail)
                            .flatMap(existsNewUsername -> {
                                if (existsNewUsername) {
                                    return Mono.just("a User already exists with that new email");
                                }

                                return clientRepository.findByUsername(username)
                                        .flatMap(existingClient -> {
                                            existingClient.setEmail(newEmail);
                                            return clientRepository.save(existingClient)
                                            .then(keycloakService.changeEmail(username, newEmail))
                                                    .then(Mono.just("Email updated successfully"));
                                        });
                            });
                });
    }

    @Override
    public Mono<String> updateClientMembership(String username, String newMembership) {
        // First, check if the username exists
        return clientRepository.existsByUsername(username)
                .flatMap(existsOldUsername -> {
                    if (!existsOldUsername) {
                        return Mono.just("Username does not exist");
                    }

                    // Check if the new membership type exists
                    return membershipRepository.existsByMembershipType(newMembership)
                            .flatMap(membershipExists -> {
                                if (!membershipExists) {
                                    return Mono.just("The type of membership does not exist");
                                }

                                // Find the client by username
                                return clientRepository.findByUsername(username)
                                        .flatMap(existingClient -> {
                                            // Fetch the membership details
                                            return membershipRepository.findByMembershipType(newMembership)
                                                    .flatMap(membershipExist -> {
                                                        // Update the client's membership
                                                        existingClient.setId_membership(membershipExist.getId());
                                                        return clientRepository.save(existingClient)
                                                                .then(Mono.just("Membership updated successfully"));
                                                    });
                                        });
                            });
                });
    }

    @Override
    public Mono<String> updateTrainer(String username, String usernameTrainer) {
        // First, check if the username exists
        return clientRepository.existsByUsername(username)
                .flatMap(existsOldUsername -> {
                    if (!existsOldUsername) {
                        return Mono.just("Username does not exist");
                    }

                    // Check if the new membership type exists
                    return perTrainerRepository.existsByUsername(usernameTrainer)
                            .flatMap(Exists -> {
                                if (!Exists) {
                                    return Mono.just("The trainer does not exist");
                                }

                                // Find the client by username
                                return clientRepository.findByUsername(username)
                                        .flatMap(existingClient -> {
                                            // Fetch the membership details
                                            return perTrainerRepository.findByUsername(usernameTrainer)
                                                    .flatMap(trainerExist -> {
                                                        // Update the client's membership
                                                        existingClient.setId_trainer(trainerExist.getId());
                                                        return clientRepository.save(existingClient)
                                                                .then(Mono.just("We'll send a email to the trainer about your inscription"));
                                                    });
                                        });
                            });
                });
    }



    @Override
    public Mono<String> dessignTrainer(String username, String usernameTrainer) {
        // First, check if the username exists
        return clientRepository.existsByUsername(username)
                .flatMap(existsOldUsername -> {
                    if (!existsOldUsername) {
                        return Mono.just("Username does not exist");
                    }

                    // Check if the new membership type exists
                    return perTrainerRepository.existsByUsername(usernameTrainer)
                            .flatMap(Exists -> {
                                if (!Exists) {
                                    return Mono.just("The trainer does not exist");
                                }

                                // Find the client by username
                                return clientRepository.findByUsername(username)
                                        .flatMap(existingClient -> {
                                            // Fetch the membership details
                                            return perTrainerRepository.findByUsername(usernameTrainer)
                                                    .flatMap(trainerExist -> {
                                                        // Update the client's membership
                                                        existingClient.setId_trainer(null);
                                                        return clientRepository.save(existingClient)
                                                                .then(Mono.just("We'll send a email to the trainer about your designation with him "));
                                                    });
                                        });
                            });
                });
    }


    @Override
    public Mono<String> dessignClientMembership(String username, String newMembership) {
        // First, check if the username exists
        return clientRepository.existsByUsername(username)
                .flatMap(existsOldUsername -> {
                    if (!existsOldUsername) {
                        return Mono.just("Username does not exist");
                    }

                    // Check if the new membership type exists
                    return membershipRepository.existsByMembershipType(newMembership)
                            .flatMap(membershipExists -> {
                                if (!membershipExists) {
                                    return Mono.just("The type of membership does not exist");
                                }

                                // Find the client by username
                                return clientRepository.findByUsername(username)
                                        .flatMap(existingClient -> {
                                            // Fetch the membership details
                                            return membershipRepository.findByMembershipType(newMembership)
                                                    .flatMap(membershipExist -> {
                                                        // Update the client's membership
                                                        existingClient.setId_membership(null);
                                                        return clientRepository.save(existingClient)
                                                                .then(Mono.just(" We'll send a email to the gym about your designation with that membership"));
                                                    });
                                        });
                            });
                });
    }

}
