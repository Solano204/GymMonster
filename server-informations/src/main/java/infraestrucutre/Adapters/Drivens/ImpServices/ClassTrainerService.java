package infraestrucutre.Adapters.Drivens.ImpServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import application.Ports.Drivers.IServices.ClassTrainerServiceInterface;
import infraestrucutre.Adapters.Drivens.DTOS.DtoDataReciving;
import infraestrucutre.Adapters.Drivens.DTOS.DtoInfoTrainer;
import infraestrucutre.Adapters.Drivens.DTOS.DtoKeyCloakUser;
import infraestrucutre.Adapters.Drivens.Entities.AllTrainer;
import infraestrucutre.Adapters.Drivens.Entities.ClassTrainer;
import infraestrucutre.Adapters.Drivens.Entities.DetailPerTrainer;
import infraestrucutre.Adapters.Drivens.Entities.DetailsClassTrainer;
import infraestrucutre.Adapters.Drivens.Entities.PerTrainer;
import infraestrucutre.Adapters.Drivens.Entities.Specialty;
import infraestrucutre.Adapters.Drivens.Entities.WorkClass;
import infraestrucutre.Adapters.Drivens.Repositories.ClassTrainerRepository;
import infraestrucutre.Adapters.Drivens.Repositories.DetailPerTrainerRepository;
import infraestrucutre.Adapters.Drivens.Repositories.DetailTrainerClassRepository;
import infraestrucutre.Adapters.Drivens.Repositories.SpecialtyRepository;
import infraestrucutre.Adapters.Drivens.Repositories.WorkClassRepository;
import infraestrucutre.Adapters.Drivens.Validations.LogicInterfaces.RegisterGeneralValidations;
import infraestrucutre.Adapters.Drivens.Validations.LogicInterfaces.ValidatePassword;
import lombok.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.*;

@Service
@AllArgsConstructor
@Data
public class ClassTrainerService implements ClassTrainerServiceInterface {

    private final ClassTrainerRepository classTrainerRepository;
    private final SpecialtyRepository specialtyRepository;
    private final WorkClassRepository workClassRepository;
    private final DatabaseClient databaseClient;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final ValidatePassword validatePasswordRegister;
    private final PasswordEncoder passwordEncoder;
    private final DetailTrainerClassRepository detailTrainerRepository;
    private final KeycloakServiceImpl keycloakService;
    private final FillinKeycloakUser fillinKeycloakUser;


     @Override
    public Mono<List<String>> createPerTrainer(AllTrainer trainer) {
    List<String> errores = new ArrayList<>();
    
    System.out.println("createTrainer");

    // Reactive validation of username, email, and password
    Mono<Boolean> usernameValidation = classTrainerRepository.existsByUsername(trainer.username())
        .doOnNext(usernameExists -> {
            if (usernameExists) {
                errores.add("I'm sorry, the username already exists. You need to choose another one.");
            }
        });

    Mono<Boolean> emailValidation = classTrainerRepository.existsByEmail(trainer.email())
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
            DetailsClassTrainer detailUser = fillDetailUser(trainer);

            return detailTrainerRepository.save(detailUser)
                .flatMap(savedDetailUser -> {
                    // Fill and save the trainer with the saved detail user ID
                     ClassTrainer newTrainer = fillClient(trainer, savedDetailUser.getId());
                    
                    DtoDataReciving dtoDataReciving = fillinKeycloakUser.fillinDataFromTrainer(trainer);
                    DtoKeyCloakUser newUserKey = fillinKeycloakUser.fillinKeycloakUser(dtoDataReciving);
                    newUserKey.setRoles(Set.of("ClassTrainer"));
                     
                    return classTrainerRepository.save(newTrainer).
                        then(keycloakService.createUser(newUserKey))
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
    

private ClassTrainer fillClient(AllTrainer client, Long detailUserId) {

    ClassTrainer newTrainer = new ClassTrainer();
    newTrainer.setUsername(client.username());
    newTrainer.setEmail(client.email());
    newTrainer.setId_detail(detailUserId);
    newTrainer.setPassword(passwordEncoder.encode(client.password()));
    newTrainer.setStartDate(client.startDate());
    return newTrainer;
}

private DetailsClassTrainer fillDetailUser(AllTrainer client) {
    DetailsClassTrainer detailUser = new DetailsClassTrainer();
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
    public Flux<ClassTrainer> getAllClassTrainers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        int offset = page * size;
        return classTrainerRepository.findAllTrainers(size, offset);
    }

    @Override
public Flux<DtoInfoTrainer> getAllPerTrainersAllInformation(int page, int size) {
    int offset = page * size;
    return classTrainerRepository.findAllTrainersAllInformation(size, offset)
        .doOnError(e -> System.err.println("Error fetching trainers information: " + e.getMessage()));
}

    @Override
    public Flux<Specialty> getAllSpecialtyOfTrainer(String username) {
        return getClassTrainerByUsername(username)
                .flatMapMany(classTrainer -> classTrainerRepository.findAllSpecialty(classTrainer.getId()));
    }

    @Override
    public Mono<ClassTrainer> getClassTrainerById(Long id) {
        return classTrainerRepository.findById(id);
    }

    

    @Override
    public Mono<ClassTrainer> getClassTrainerByUsername(String username) {
        return classTrainerRepository.findByUsername(username);
    }


    @Override
    public Mono<Void> deleteClassTrainerId(Long id) {
        return classTrainerRepository.deleteById(id);
    }

    @Override
    public Mono<Void> deleteClassTrainerUsername(String username) {
        return classTrainerRepository.deleteByUsername(username);
    }

    @Override
    public Flux<WorkClass> getWorkClassesByTrainer(String username) {
        return getClassTrainerByUsername(username).flatMapMany(
                trainer -> {
                    trainer.getId();
                    return classTrainerRepository.findAllClassByTrainer(trainer.getId());
                });
    }


    @Override
    public Mono<String> deleteClassTrainerUsername(String username,String password) {
        return classTrainerRepository.existsByUsername(username)
                .flatMap(existsOldUsername -> {
                    if (!existsOldUsername) {
                        return Mono.just("trainer does not exist");
                    }

                    // If the username exists, find the client by username
                    return classTrainerRepository.findByUsername(username)
                            .flatMap(clientExist -> {
                                // Check if the old password matches
                                if (passwordEncoder.matches(password, clientExist.getPassword())) {

                                                 
                                    return classTrainerRepository.deleteByUsername(username)
                                            .then(keycloakService.deleteUser(username))
                                            .then(Mono.just("account deleted successfully"));
                                } else {
                                    return Mono.just("Old password is not correct");
                                }
                            });
                });
    }




    @Override
    public Mono<String> updateTrainerAllDetailInformation(String username, DetailsClassTrainer newDatUser) {
        // Check if the username exists
        return classTrainerRepository.existsByUsername(username)
            .flatMap(existsOldUsername -> {
                if (!existsOldUsername) {
                    return Mono.just("trainer does not exist");
                }
    
                // Get client detail by username
                return classTrainerRepository.findByUsername(username)
                    .flatMap(dataExist -> {
                        if (dataExist != null) {
                            // Check if the combination of name, secondName, lastNameM, and lastNameP already exists
                            return detailTrainerRepository.countByDetailInfo(newDatUser.getName(), newDatUser.getSecondName(), newDatUser.getLastNameM(), newDatUser.getLastNameP())
                                .flatMap(count -> {
                                    if (count > 0) {
                                        return Mono.just("A user already exists with that data (name, secondName, lastNameM, lastNameP)");
                                    } else {
                                        // Find the client and update their details
                                        return classTrainerRepository.findByUsername(username)
                                            .flatMap(existingClient -> detailTrainerRepository.findById(existingClient.getId_detail())
                                                .flatMap(detailExist -> {
                                                    // Update the detail f
                                                    fillDetailTrainer(newDatUser, detailExist);
                                                    return detailTrainerRepository.save(detailExist)
                                                        .then(keycloakService.updateClassTrainerAllInformation(username,newDatUser))
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
    
    public DetailsClassTrainer fillDetailTrainer(DetailsClassTrainer newDatUser, DetailsClassTrainer detailExist) {
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
            // First, check if the username exists
                return classTrainerRepository.existsByUsername(username)
                    .flatMap(existsOldUsername -> {
                        if (!existsOldUsername) {
                            return Mono.just("Username does not exist");
                        }
                        // If the username exists,  find the client by username
                        return classTrainerRepository.findByUsername(username)
                                .flatMap(clientExist -> {
                                    // Check if the old password matches
                                    if (passwordEncoder.matches(oldPassword, clientExist.getPassword())) {
                                        
                                        // Update to the new password+



                                        // Save the updated client
                                        keycloakService.changePassword(username, newPassword);
                                        return classTrainerRepository.save(clientExist)
                                        .then( keycloakService.changePassword(username, pass))
                                                .then(Mono.just("Password updated successfully"));
                                    } else {
                                        return Mono.just("Old password is not correct");
                                    }
                                });
                    });
        }
    
        @Override
        public Mono<String> updateTrainerUsername(String oldUsername, String newUsername, String password) {
            // Check if old username exists
            return classTrainerRepository.existsByUsername(oldUsername)
                    .flatMap(existsOldUsername -> {
                        if (!existsOldUsername) {
                            return Mono.just("Old username does not exist");
                        }
    
                        // Check if new username already exists
                        return classTrainerRepository.existsByUsername(newUsername)
                                .flatMap(existsNewUsername -> {
                                    if (existsNewUsername) {
                                        return Mono.just("a user already exists with that new username");
                                    }
    
                               
                                    return classTrainerRepository.findByUsername(oldUsername)
                                            .flatMap(existingClient -> {


                                                if (!passwordEncoder.matches(password, existingClient.getPassword())) {
                                                    return Mono.just("Password does not match");
                                                } 
                                                existingClient.setUsername(newUsername);
                                                return classTrainerRepository.save(existingClient)
                                                        .then(keycloakService.updateUsername2(newUsername, oldUsername, password,"ClassTrainer"))
                                                        .then(Mono.just("Username updated successfully"));
                                            });
                                });
                    });
        }
    
        @Override
        public Mono<String> updateClientEmail(String username, String newEmail) {
            return  classTrainerRepository.existsByUsername(username)
                    .flatMap(existsOldUsername -> {
                        if (!existsOldUsername) {
                            return Mono.just("username does not exist");
                        }
    
                        // Check if new email already exists
                        return classTrainerRepository.existsByEmail(newEmail)
                                .flatMap(existsNewUsername -> {
                                    if (existsNewUsername) {
                                        return Mono.just("a trainer already exists with that new email");
                                    }
    
                                    
                                    return classTrainerRepository.findByUsername(username)
                                            .flatMap(existingClient -> {
                                                existingClient.setEmail(newEmail);
                                                return classTrainerRepository.save(existingClient)
                                                        .then(keycloakService.changeEmail(username, newEmail))
                                                        .then(Mono.just("Email updated successfully"));
                                            });
                                });
                    });
        }
    
    
    @Override
public Mono<String> addSpecialtyToTrainer(String username, String newSpecialty) {
    System.out.println("Adding specialty '" + newSpecialty + "' to trainer '" + username + "'");

    // First, check if the username exists
    return classTrainerRepository.existsByUsername(username)
            .flatMap(existsOldUsername -> {
                if (!existsOldUsername) {
                    System.out.println("Username does not exist: " + username);
                    return Mono.just("Username does not exist");
                }

                // Fetch the trainer if username exists
                return classTrainerRepository.findByUsername(username)
                        .flatMap(existingClient -> {
                            System.out.println("Trainer found: " + existingClient.getUsername());

                            // Check if the specialty already exists for this trainer
                            return classTrainerRepository.existsSpecialtyForTrainer(existingClient.getId(), newSpecialty)
                                    .flatMap(existsSpecialty -> {
                                        if (existsSpecialty > 0) {
                                            System.out.println("Specialty already exists for trainer: " + newSpecialty);
                                            return Mono.just("Specialty already exists");
                                        }

                                        // Find the specialty by its name
                                        return specialtyRepository.findByName(newSpecialty)
                                                .flatMap(specialty -> {
                                                    if (specialty == null) {
                                                        System.out.println("Specialty does not exist: " + newSpecialty);
                                                        return Mono.just("Specialty does not exist");
                                                    }

                                                    // Add the specialty to the trainer
                                                    return classTrainerRepository.addSpecialtyToTrainer(existingClient.getId(), specialty.getId())
                                                            .doOnSuccess(aVoid -> {
                                                                System.out.println("Specialty added successfully: " + newSpecialty);
                                                            })
                                                            .then(Mono.just("Specialty added successfully"));
                                                });
                                    });
                        });
            })
            .doOnError(error -> {
                // Handle and log any unexpected errors
                System.err.println("Error occurred: " + error.getMessage());
            });
}

    
    @Override
    public Mono<String> dessociateSpecialty(String username, String newSpecialty) {
        // First, check if the username exists
        return classTrainerRepository.existsByUsername(username)
                .flatMap(existsOldUsername -> {
                    if (!existsOldUsername) {
                        return Mono.just("Username does not exist");
                    }
                    return classTrainerRepository.findByUsername(username)
                            .flatMap(existingClient -> {
                                return classTrainerRepository.existsSpecialtyForTrainer(existingClient.getId(), newSpecialty)
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
                                                        return classTrainerRepository.
                                                        removeSpecialtyFromTrainer(existingClient.getId(), specialty.getId())
                                                                .then(Mono.just("Specialty removed successfully"));
                                                    });
                                        });
                            });
                });
    }
    @Override
    public Mono<String> addClassToTrainer(String username, String newClass) {
        // First, check if the username exists
        return classTrainerRepository.existsByUsername(username)
                .flatMap(existsOldUsername -> {
                    if (!existsOldUsername) {
                        return Mono.just("Username does not exist");
                    }
                    return classTrainerRepository.findByUsername(username)
                            .flatMap(existingClient -> {
                                return classTrainerRepository.existsClassForTrainer(existingClient.getId(), newClass)
                                        .flatMap(existsSpecialty -> {
                                            if (existsSpecialty > 0) {
                                                return Mono.just("The class already exist for the trainer");
                                            }
                                            // Find the specialty by name
                                            return workClassRepository.findByName(newClass)
                                                    .flatMap(specialty -> {
                                                        if (specialty == null) {
                                                            return Mono.just("Class does not exist");
                                                        }
                                                        // Remove the specialty from the trainer
                                                        return classTrainerRepository.
                                                        addClassToTrainer(existingClient.getId(), specialty.getId())
                                                                .then(Mono.just("Class removed successfully"));
                                                    });
                                        });
                            });
                });
    }

    @Override
    public Mono<String> dessociateClassToTrainer(String username, String newClass) {
        // First, check if the username exists
        return classTrainerRepository.existsByUsername(username)
                .flatMap(existsOldUsername -> {
                    if (!existsOldUsername) {
                        return Mono.just("Username does not exist");
                    }
                    return classTrainerRepository.findByUsername(username)
                            .flatMap(existingClient -> {
                                return classTrainerRepository.existsClassForTrainer(existingClient.getId(), newClass)
                                        .flatMap(existsSpecialty -> {
                                            if (existsSpecialty == 0) {
                                                return Mono.just("The class does not exist for the trainer");
                                            }
                                            // Find the specialty by name
                                            return workClassRepository.findByName(newClass)
                                                    .flatMap(specialty -> {
                                                        if (specialty == null) {
                                                            return Mono.just("Class does not exist");
                                                        }
                                                        // Remove the specialty from the trainer
                                                        return classTrainerRepository.
                                                        removeClassFromTrainer(existingClient.getId(), specialty.getId())
                                                                .then(Mono.just("Class removed successfully"));
                                                    });
                                        });
                            });
                });
    }



}