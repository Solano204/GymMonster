package infraestrucutre.Adapters.Drivens.ImpServices;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import application.Ports.Drivers.IServices.PerTrainerServiceInterface;
import infraestrucutre.Adapters.Drivens.DTOS.DtoDataReciving;
import infraestrucutre.Adapters.Drivens.DTOS.DtoInfoTrainer;
import infraestrucutre.Adapters.Drivens.DTOS.DtoKeyCloakUser;
import infraestrucutre.Adapters.Drivens.Entities.AllTrainer;
import infraestrucutre.Adapters.Drivens.Entities.DetailPerTrainer;
import infraestrucutre.Adapters.Drivens.Entities.DetailUser;
import infraestrucutre.Adapters.Drivens.Entities.PerTrainer;
import infraestrucutre.Adapters.Drivens.Entities.Specialty;
import infraestrucutre.Adapters.Drivens.Repositories.DetailPerTrainerRepository;
import infraestrucutre.Adapters.Drivens.Repositories.PerTrainerRepository;
import infraestrucutre.Adapters.Drivens.Repositories.SpecialtyRepository;
import infraestrucutre.Adapters.Drivens.Validations.LogicInterfaces.RegisterGeneralValidations;
import infraestrucutre.Adapters.Drivens.Validations.LogicInterfaces.ValidatePassword;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@AllArgsConstructor
public class PerTrainerService implements PerTrainerServiceInterface {

    private final PerTrainerRepository perTrainerRepository;
    private final ValidatePassword validatePasswordRegister;
    private final PasswordEncoder passwordEncoder;
    private final DetailPerTrainerRepository detailTrainerRepository;
    private final SpecialtyRepository specialtyRepository;
    private final KeycloakServiceImpl keycloakService;
    private final FillinKeycloakUser fillinKeycloakUser;



    @Override
    public Mono<List<String>> createPerTrainer(AllTrainer trainer) {
    List<String> errores = new ArrayList<>();
    
    System.out.println("createTrainer");

    // Reactive validation of username, email, and password
    Mono<Boolean> usernameValidation = perTrainerRepository.existsByUsername(trainer.username())
        .doOnNext(usernameExists -> {
            if (usernameExists) {
                errores.add("I'm sorry, the username already exists. You need to choose another one.");
            }
        });

    Mono<Boolean> emailValidation = perTrainerRepository.existsByEmail(trainer.email())
        .doOnNext(emailExists -> {
            if (emailExists) {
                errores.add("I'm sorry, that email already exists. Please choose another one.");
            }
        });

    Mono<Boolean> passwordValidation = validatePasswordRegister.validatePasswordRegister(trainer.password())
        .doOnNext(isPasswordInsecure -> {
            if (isPasswordInsecure) {
                errores.add("Your password is insecure, please choose another one.");
            }
        });

    // Reactive field validation for the detail info uniqueness
    Mono<Integer> detailInfoValidation = detailTrainerRepository.countByDetailInfo(
        trainer.name(), trainer.secondName(), trainer.lastNameM(), trainer.lastNameP()
    ).doOnNext(count -> {
        if (count > 0) {
            errores.add("A trainer already exists with that data (name, secondName, lastNameM, lastNameP).");
        }
    });

    // Perform reactive validation and check errors after all checks
    return Mono.zip(usernameValidation, emailValidation, passwordValidation, detailInfoValidation)
        .flatMap(results -> {
            // If there are any errors, return them
            if (!errores.isEmpty()) {
                return Mono.just(errores);
            }

            // Check basic field validation synchronously
            boolean basicFieldValidation = isValidBasciFields(trainer, errores);
            if (!basicFieldValidation) {
                return Mono.just(errores); // Return errors if basic fields are invalid
            }

            // If no errors, proceed with creating the trainer
            DetailPerTrainer detailUser = fillDetailUser(trainer);

            return detailTrainerRepository.save(detailUser)
                .flatMap(savedDetailUser -> {
                    // Fill and save the trainer with the saved detail user ID
                    PerTrainer newTrainer = fillClient(trainer, savedDetailUser.getId());

                   DtoDataReciving dtoDataReciving = fillinKeycloakUser.fillinDataFromTrainer(trainer);
                    DtoKeyCloakUser newUserKey = fillinKeycloakUser.fillinKeycloakUser(dtoDataReciving);
                    newUserKey.setRoles(Set.of("PerTrainer"));
                    return perTrainerRepository.save(newTrainer)
                        .then(keycloakService.createUser(newUserKey))
                        .then(Mono.just(List.of("Congratulations, your account has been created. You can now log in.")));
                });
        })
        // Catch unexpected errors and add to the error list
        .onErrorResume(ex -> {
            errores.add("An error occurred: " + ex.getMessage());
            return Mono.just(errores);
        });
}



    private boolean isValidBasciFields(AllTrainer client, List<String> errorMessages) {
        DtoDataReciving dtoDataReciving = new DtoDataReciving(client.username(), client.email(), client.password(), client.name(), client.secondName(), client.lastNameP(), client.lastNameM(), client.age(), client.height(), client.weight());
            new RegisterGeneralValidations().validateBasicFields(dtoDataReciving, errorMessages);
            return errorMessages.isEmpty();
        }
    

private PerTrainer fillClient(AllTrainer client, Long detailUserId) {

    PerTrainer newTrainer = new PerTrainer();
    newTrainer.setUsername(client.username());
    newTrainer.setEmail(client.email());
    newTrainer.setId_detail(detailUserId);
    newTrainer.setPassword(passwordEncoder.encode(client.password()));
    newTrainer.setStartDate(client.startDate());
    return newTrainer;
}

private DetailPerTrainer fillDetailUser(AllTrainer client) {
    DetailPerTrainer detailUser = new DetailPerTrainer();
    detailUser.setName(client.name());
    detailUser.setSecondName(client.secondName());
    detailUser.setLastNameM(client.lastNameM());
    detailUser.setLastNameP(client.lastNameP());
    detailUser.setAge(client.age());
    detailUser.setWeight(client.weight());
    detailUser.setHeight(client.height());
    return detailUser;
}
    @Override
    public Flux<PerTrainer> getAllPerTrainers(int page, int size) {
        int offset = page * size;
        return perTrainerRepository.findAllTrainers(size, offset);
    }

    @Override
    public Flux<DtoInfoTrainer> getAllPerTrainersAllInformation(int page, int size) {
        int offset = page * size;
        return perTrainerRepository.findAllTrainersAllInformation(size, offset);
    }

    @Override
    public Mono<PerTrainer> getPerTrainerById(Long id) {
        return perTrainerRepository.findById(id);
    }

    @Override
    public Mono<PerTrainer> getPerTrainerByUsername(String username) {
        return perTrainerRepository.findByUsername(username);
    }

    @Override
    public Mono<DtoInfoTrainer> getAllInfotTrainer(String username) {
        return getPerTrainerByUsername(username)
                .flatMap(trainer -> perTrainerRepository.findAllInfoTrainer(trainer.getId()));
    }

    @Override
    public Mono<DetailUser> getInfotTrainer(String username) {
        return getPerTrainerByUsername(username)
                .flatMap(trainer -> perTrainerRepository.findInfoTrainer(trainer.getId()));
    }

    @Override
    public Mono<PerTrainer> getPerTrainerByName(String username) {
        return perTrainerRepository.findByUsername(username);
    }

    @Override
    public Mono<PerTrainer> updatePerTrainer(String username, PerTrainer perTrainer) {
            return
             perTrainerRepository.findByUsername(username).flatMap( existingPerTrainer -> {
                existingPerTrainer.setUsername(perTrainer.getUsername());
                existingPerTrainer.setEmail(perTrainer.getEmail());
                existingPerTrainer.setPassword(perTrainer.getPassword());
                return perTrainerRepository.save(existingPerTrainer);
            });
    }

    @Override
    public Mono<String> deletePerTrainerUsername(String username,String password) {
        return perTrainerRepository.existsByUsername(username)
                .flatMap(existsOldUsername -> {
                    if (!existsOldUsername) {
                        return Mono.just("trainer does not exist");
                    }

                    // If the username exists, find the client by username
                    return perTrainerRepository.findByUsername(username)
                            .flatMap(clientExist -> {
                                // Check if the old password matches
                                if (passwordEncoder.matches(password, clientExist.getPassword())) {                     
                                    return perTrainerRepository.deleteByUsername(username)
                                            .then(keycloakService.deleteUser(username))
                                            .then(Mono.just("account deleted successfully"));  
                                } else {
                                    return Mono.just("Old password is not correct");
                                }
                            });
                });
    }


    @Override
    public Mono<Boolean> existsByUsernameTrainer(String username) {
        return perTrainerRepository.existsByUsername(username);
    }

    @Override
    public Mono<Boolean> existsByEmailTrainer(String email) {
        return perTrainerRepository.existsByEmail(email);
    }
   
    

    @Override
    public Flux<DetailUser> getAllClients(String username, int page, int size) {
        int offset = page * size;
        return getPerTrainerByUsername(username)
                .flatMapMany(
                        perTrainer -> perTrainerRepository.findClientsByTrainerId(perTrainer.getId(), size, offset));
    }

    @Override
    public Flux<Specialty> getAllSpecialties(String username) {
        return getPerTrainerByUsername(username)
                .flatMapMany(perTrainer -> perTrainerRepository.findAllSpecialtyByTrainerId(perTrainer.getId()));
    }


    @Override
public Mono<String> updateTrainerAllDetailInformation(String username, DetailPerTrainer newDatUser) {
    // Check if the username exists
    return perTrainerRepository.existsByUsername(username)
        .flatMap(existsOldUsername -> {
            if (!existsOldUsername) {
                return Mono.just("trainer does not exist");
            }

            // Get client detail by username
            return perTrainerRepository.findByUsername(username)
                .flatMap(dataExist -> {
                    if (dataExist != null) {
                        // Check if the combination of name, secondName, lastNameM, and lastNameP already exists
                        return detailTrainerRepository.countByDetailInfo(newDatUser.getName(), newDatUser.getSecondName(), newDatUser.getLastNameM(), newDatUser.getLastNameP())
                            .flatMap(count -> {
                                if (count > 0) {
                                    return Mono.just("A user already exists with that data (name, secondName, lastNameM, lastNameP)");
                                } else {
                                    // Find the client and update their details
                                    return perTrainerRepository.findByUsername(username)
                                        .flatMap(existingClient -> detailTrainerRepository.findById(existingClient.getId_detail())
                                            .flatMap(detailExist -> {
                                                // Update the detail information
                                                fillDetailTrainer(newDatUser, detailExist);
                                                
                                                return detailTrainerRepository.save(detailExist)
                                                    .then(keycloakService.updateTrainerAllInformation(username,newDatUser))
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


public DetailPerTrainer fillDetailTrainer(DetailPerTrainer newDatUser, DetailPerTrainer detailExist) {
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
public Mono<String> updateTrainerPassword(String username, String newPassword, String oldPassword) {
    String pass = newPassword;
    System.out.println("Encoded old password: " + passwordEncoder.encode(oldPassword));
    return perTrainerRepository.existsByUsername(username)
            .flatMap(existsOldUsername -> {
                if (!existsOldUsername) {
                    return Mono.just("Username does not exist");
                }
                return perTrainerRepository.findByUsername(username)
                        .flatMap(trainer -> {
                            if (passwordEncoder.matches(oldPassword, trainer.   getPassword())) {
                                trainer.setPassword(passwordEncoder.encode(newPassword));
                                perTrainerRepository.save(trainer);
                                return perTrainerRepository.save(trainer)
                                        .then(keycloakService.changePassword(username, pass))
                                        .then(Mono.just("Password updated successfully"));
                            } else {
                                return Mono.just("Old password is not correct");
                            }
                        });
            })
            .doOnError(error -> System.err.println("Error occurred: " + error.getMessage())) // Log the error
            .onErrorReturn("An error occurred while updating the password"); // Default error message
}



@Override
public Mono<String> updateTrainerUsername(String oldUsername, String newUsername, String password) {
    // Check if old username exists
    return perTrainerRepository.existsByUsername(oldUsername)
            .flatMap(existsOldUsername -> {
                if (!existsOldUsername) {
                    return Mono.error(new RuntimeException("Old username does not exist"));
                }

                // Check if new username already exists
                return perTrainerRepository.existsByUsername(newUsername)
                        .flatMap(existsNewUsername -> {
                            if (existsNewUsername) {
                                return Mono.error(new RuntimeException("A user already exists with that new username"));
                            }

                            // If old username exists and new username does not exist, update the username
                            return perTrainerRepository.findByUsername(oldUsername)
                                    .flatMap(existingTrainer -> {
                                        // Check if the provided password matches the stored password
                                        if (passwordEncoder.matches(password, existingTrainer.getPassword())) {
                                            existingTrainer.setUsername(newUsername);
                                            return perTrainerRepository.save(existingTrainer)
                                                    .then(keycloakService.updateUsername2(newUsername, oldUsername, password, "PerTrainer"))
                                                    .then(Mono.just("Username updated successfully"));
                                        } else {
                                            return Mono.error(new RuntimeException("Provided password does not match"));
                                        }
                                    });
                        });
            });
}


    @Override
    public Mono<String> updateClientEmail(String username, String newEmail) {
        return  perTrainerRepository.existsByUsername(username)
                .flatMap(existsOldUsername -> {
                    if (!existsOldUsername) {
                        return Mono.just("username does not exist");
                    }

                    // Check if new email already exists
                    return perTrainerRepository.existsByEmail(newEmail)
                            .flatMap(existsNewUsername -> {
                                if (existsNewUsername) {
                                    return Mono.just("a trainer already exists with that new email");
                                }

                                
                                return perTrainerRepository.findByUsername(username)
                                        .flatMap(existingClient -> {
                                            existingClient.setEmail(newEmail);
                                            return perTrainerRepository.save(existingClient)
                                                    .then(keycloakService.changeEmail(username, newEmail))
                                                    .then(Mono.just("Email updated successfully"));
                                        });
                            });
                });
    }


    @Override
    public Mono<String> addSpecialtyToTrainer(String username, String newSpecialty) {
        // First, check if the username exists
        return perTrainerRepository.existsByUsername(username)
                .flatMap(existsOldUsername -> {
                    if (!existsOldUsername) {
                        return Mono.just("Username does not exist");
                    }
                    // Fetch the trainer if username exists
                    return perTrainerRepository.findByUsername(username)
                            .flatMap(existingClient -> {
                                // Check if the specialty already exists for this trainer
                                return perTrainerRepository.existsSpecialtyForTrainer(existingClient.getId(), newSpecialty)
                                        .flatMap(existsSpecialty -> {
                                            if (existsSpecialty > 0) {
                                                return Mono.just("Specialty already exists");
                                            }
                                            // Find the specialty by its name
                                            return specialtyRepository.findByName(newSpecialty)
                                                    .flatMap(specialty -> {
                                                        if (specialty == null) {
                                                            return Mono.just("Specialty does not exist");
                                                        }
                                                        // Add the specialty to the trainer
                                                        return perTrainerRepository.addSpecialtyToTrainer(existingClient.getId(), specialty.getId())
                                                                .then(Mono.just("Specialty added successfully"));
                                                    });
                                        });
                            });
                });
    }
    

@Override
public Mono<String> dessociateSpecialty(String username, String newSpecialty) {
    // First, check if the username exists
    return perTrainerRepository.existsByUsername(username)
            .flatMap(existsOldUsername -> {
                if (!existsOldUsername) {
                    return Mono.just("Username does not exist");
                }
                return perTrainerRepository.findByUsername(username)
                        .flatMap(existingClient -> {
                            return perTrainerRepository.existsSpecialtyForTrainer(existingClient.getId(), newSpecialty)
                                    .flatMap(existsSpecialty -> {
                                        if (existsSpecialty == 0) {
                                            return Mono.just("The specialty does not exist for the trainer");
                                        }
                                        // Find the specialty by name
                                        return specialtyRepository.findByName(newSpecialty)
                                                .flatMap(specialty -> {
                                                    if (specialty == null) {
                                                        return Mono.just("Specialty does not exist");
                                                    }
                                                    // Remove the specialty from the trainer
                                                    return perTrainerRepository.
                                                    removeSpecialtyFromTrainer(existingClient.getId(), specialty.getId())
                                                            .then(Mono.just("Specialty removed successfully"));
                                                });
                                    });
                        });
            });
}

}
