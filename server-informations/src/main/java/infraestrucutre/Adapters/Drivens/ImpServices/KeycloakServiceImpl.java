package infraestrucutre.Adapters.Drivens.ImpServices;

import infraestrucutre.Adapters.Drivens.Configurations.KeycloakProvider;
import infraestrucutre.Adapters.Drivens.DTOS.DtoKeyCloakUser;
import infraestrucutre.Adapters.Drivens.Entities.DetailPerTrainer;
import infraestrucutre.Adapters.Drivens.Entities.DetailUser;
import infraestrucutre.Adapters.Drivens.Entities.DetailsClassTrainer;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import application.Ports.Drivers.IServices.IKeycloakServiceInterface;
import reactor.core.publisher.Flux;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@Data
public class KeycloakServiceImpl implements IKeycloakServiceInterface {

    private final KeycloakProvider keycloakProvider;

    @Override
    public Flux<UserRepresentation> findAllUsers() {
        return keycloakProvider.getRealmResource()
                .flatMapMany(realmResource -> Mono.fromCallable(() -> realmResource.users().list()))
                .flatMap(Flux::fromIterable)
                .onErrorResume(e -> {
                    log.error("Error fetching all users: {}", e.getMessage());
                    return Flux.empty(); // Return an empty Flux in case of an error
                });
    }

    @Override
    public Flux<UserRepresentation> searchUserByUsername(String username) {
        return keycloakProvider.getRealmResource()
                .flatMapMany(realmResource -> Mono.fromCallable(() -> realmResource.users()
                        .searchByUsername(username, true)))
                .flatMap(Flux::fromIterable)
                .onErrorResume(e -> {
                    log.error("Error searching user by username '{}': {}", username, e.getMessage());
                    return Flux.empty(); // Return an empty Flux in case of an error
                });
    }

    @Override
    public Mono<DtoKeyCloakUser> createUser(@NonNull DtoKeyCloakUser DtoKeyCloakUser) {
        return keycloakProvider.getUserResource() // Asynchronously get UsersResource
                .flatMap(usersResource -> {
                    // Prepare the user representation from DtoKeyCloakUser
                    UserRepresentation userRepresentation = new UserRepresentation();
                    userRepresentation.setFirstName(DtoKeyCloakUser.getFirstName());
                    userRepresentation.setLastName(DtoKeyCloakUser.getLastName());
                    userRepresentation.setEmail(DtoKeyCloakUser.getEmail());
                    userRepresentation.setUsername(DtoKeyCloakUser.getUsername());
                    userRepresentation.setEnabled(true);
                    userRepresentation.setEmailVerified(true);

                    // Create the user in Keycloak
                    Response response = usersResource.create(userRepresentation);
                    int status = response.getStatus();

                    if (status == 201) {
                        // Extract user ID from the response location header
                        String path = response.getLocation().getPath();
                        String userId = path.substring(path.lastIndexOf("/") + 1);

                        // Set the user's password
                        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
                        credentialRepresentation.setTemporary(false);
                        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
                        credentialRepresentation.setValue(DtoKeyCloakUser.getPassword());
                        usersResource.get(userId).resetPassword(credentialRepresentation);

                        return assignRolesToUser(DtoKeyCloakUser, userId);

                    } else if (status == 409) {
                        DtoKeyCloakUser userDT = new DtoKeyCloakUser();
                        userDT.setUsername("ALREADY EXISTS");

                        return Mono.just(userDT);
                    } else {
                        DtoKeyCloakUser userDT = new DtoKeyCloakUser();
                        userDT.setUsername("ALREADY EXISTS");
                        return Mono.just(userDT);
                    }
                })
                // Catch unexpected errors and add to the error list
                .onErrorResume(e -> {
                    return Mono.error(
                            new RuntimeException("Error assigning roles to user, please contact the administrator."));
                });
    }

    // Helper method to assign roles
    private Mono<DtoKeyCloakUser> assignRolesToUser(DtoKeyCloakUser DtoKeyCloakUser, String userId) {
        return keycloakProvider.getRealmResource()
                .flatMap(realmResource -> {
                    List<RoleRepresentation> rolesRepresentation;
                    if (DtoKeyCloakUser.getRoles() == null || DtoKeyCloakUser.getRoles().isEmpty()) {
                        rolesRepresentation = List.of(realmResource.roles().get("client").toRepresentation());
                    } else {
                        rolesRepresentation = realmResource.roles()
                                .list()
                                .stream()
                                .filter(role -> DtoKeyCloakUser.getRoles()
                                        .stream()
                                        .anyMatch(roleName -> roleName.equalsIgnoreCase(role.getName())))
                                .collect(Collectors.toList());
                    }
                    // Assign the roles to the user
                    realmResource.users().get(userId).roles().realmLevel().add(rolesRepresentation);
                    return Mono.just(DtoKeyCloakUser);
                }).onErrorResume(e -> {
                    return Mono.error(
                            new RuntimeException("Error assigning roles to user, please contact the administrator."));
                });
    }

    @Override
    public Mono<String> deleteUser(String username) {
        return searchUserID(username).flatMap(userId -> {
            if (userId == null) {
                return Mono.just("User not found");
            } else {
                return keycloakProvider.getUserResource()
                        .flatMap(usersResource -> Mono.fromRunnable(() -> usersResource.get(userId).remove()))
                        .thenReturn("The user was deleted successfully!")
                        .onErrorResume(e -> Mono.just("DELETION UNSUCCESSFUL: " + e.getMessage()));
            }
        });
    }

@Override
public Mono<String> searchUserID(String username) {
    return keycloakProvider.getUserResource()
            .flatMap(usersResource -> Mono.fromCallable(() -> {
                try {
                    // Call the Keycloak search API
                    return usersResource.search(username, true);
                } catch (Exception e) {
                    throw new RuntimeException("Error during Keycloak search", e);
                }
            })
            .subscribeOn(Schedulers.boundedElastic())) // Handle blocking I/O
            .flatMap(users -> {
                if (users != null && !users.isEmpty()) {
                    return Mono.just(users.get(0).getId()); // Return the first user’s ID if found
                } else {
                    return Mono.error(new RuntimeException("User '" + username + "' not found in Keycloak"));
                }
            })
            .onErrorResume(e -> {
                log.error("Error searching for user by username '{}': {}", username, e.getMessage(), e);
                return Mono.empty(); // Return an empty Mono on error
            });
}


    @Override
    public Mono<String> changePassword(String username, String newPassword) {
        return searchUserID(username).flatMap(userId -> {
            if (userId == null) {
                return Mono.just("User not found");
            } else {
                CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
                credentialRepresentation.setTemporary(false);
                credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
                credentialRepresentation.setValue(newPassword);

                // Reset the password for the user
                return keycloakProvider.getUserResource()
                        .flatMap(usersResource -> {
                            // Use Mono.fromRunnable to execute the password reset
                            return Mono.fromRunnable(
                                    () -> usersResource.get(userId).resetPassword(credentialRepresentation));
                        })
                        .thenReturn("Password changed successfully!") // Return success message
                        .onErrorResume(e -> Mono.just("PASSWORD CHANGE UNSUCCESSFUL: " + e.getMessage())); // Handle
                                                                                                           // errors
            }
        });
    }

    @Override
    public Mono<String> changeEmail(String username, String newEmail) {
        return searchUserID(username).flatMap(userId -> {
            if (userId == null) {
                return Mono.just("User not found");
            } else {
                // Create a UserRepresentation to update the email
                UserRepresentation userRepresentation = new UserRepresentation();
                userRepresentation.setUsername(username);
                userRepresentation.setEmail(newEmail);
                userRepresentation.setEnabled(true); // Optional: Enable user if needed

                // Get the users resource and update the user's email
                return keycloakProvider.getUserResource()
                        .flatMap(usersResource -> {
                            // Use Mono.fromRunnable to execute the email update
                            return Mono.fromRunnable(() -> usersResource.get(userId).update(userRepresentation));
                        })
                        .thenReturn("Email changed successfully!") // Return success message
                        .onErrorResume(e -> Mono.just("EMAIL CHANGE UNSUCCESSFUL: " + e.getMessage())); // Handle errors
            }
        });
    }

    @Override
    public Mono<String> changeUsername(String username, String newUsename) {
        return searchUserID(username).flatMap(userId -> {
            if (userId == null) {
                return Mono.just("User not found");
            } else {
                // Create a UserRepresentation to update the username
                UserRepresentation userRepresentation = new UserRepresentation();
                userRepresentation.setUsername(newUsename);
                userRepresentation.setEnabled(true); // Optional: Enable user if needed

                // Get the users resource and update the user's username
                return keycloakProvider.getUserResource()
                        .flatMap(usersResource -> {
                            // Use Mono.fromRunnable to execute the username update
                            return Mono.fromRunnable(() -> usersResource.get(userId).update(userRepresentation));
                        })
                        .thenReturn("Username changed successfully!") // Return success message
                        .onErrorResume(e -> Mono.just("USERNAME CHANGE UNSUCCESSFUL: " + e.getMessage())); // Handle
                                                                                                           // errors
            }
        });
    }

    @Override
    public Mono<DtoKeyCloakUser> updateUsername2(String newUsername, String oldUsername, String newPassword,String role) {
        return searchUserID(oldUsername)
                .flatMap(userId -> {
                    if (userId == null) {
                        return Mono.error(new RuntimeException("Old user not found"));
                    }
                    return searchUserByUsername(oldUsername)
        .next() // Get the first user from the Flux
        .flatMap(user -> {
            

            DtoKeyCloakUser userModified = new DtoKeyCloakUser();
            userModified.setUsername(newUsername);
            userModified.setFirstName(user.getFirstName());
            userModified.setLastName(user.getLastName());
            userModified.setEmail(user.getEmail());
            userModified.setRoles(Set.of(role));
            userModified.setPassword(newPassword);

            // Create the new user
            return deleteUser(oldUsername)
                .then(createUser(userModified)); 
        })
        .switchIfEmpty(Mono.error(new RuntimeException("User not found in search result")));
                }); 
    }

    @Override
    public Mono<String> updateTrainerAllInformation(String username, @NonNull DetailPerTrainer dtoKeyCloakUser) {
        return searchUserID(username)
                .flatMap(userId -> {
                    if (userId == null) {
                        // Return a message if user ID is not found
                        return Mono.just("User not found");
                    } else {
                        return keycloakProvider.getUserResource()
                                .flatMap(usersResource -> {
                                    UserRepresentation user = new UserRepresentation();
                                    user.setFirstName(dtoKeyCloakUser.getName());
                                    user.setLastName(dtoKeyCloakUser.getLastNameM());
                                    user.setUsername(username);
                                    return Mono.fromRunnable(() -> usersResource.get(userId).update(user))
                                            .then(Mono.just("The user was updated successfully!"));
                                })
                                .onErrorResume(e -> {
                                    log.error("Error updating user: {}", e.getMessage());
                                    return Mono.just("Error updating user: " + e.getMessage());
                                });
                    }
                });
    }

    @Override
    public Mono<String> updateClassTrainerAllInformation(String username,
            @NonNull DetailsClassTrainer dtoKeyCloakUser) {
        return searchUserID(username)
                .flatMap(userId -> {
                    if (userId == null) {
                        // Return a message if user ID is not found
                        return Mono.just("User not found");
                    } else {
                        return keycloakProvider.getUserResource()
                                .flatMap(usersResource -> {
                                    UserRepresentation user = new UserRepresentation();
                                    user.setFirstName(dtoKeyCloakUser.getName());
                                    user.setLastName(dtoKeyCloakUser.getLastNameM());
                                    user.setUsername(username);
                                    return Mono.fromRunnable(() -> usersResource.get(userId).update(user))
                                            .then(Mono.just("The user was updated successfully!"));
                                })
                                .onErrorResume(e -> {
                                    log.error("Error updating user: {}", e.getMessage());
                                    return Mono.just("Error updating user: " + e.getMessage());
                                });
                    }
                });
    }

    @Override
    public Mono<String> updateUserAllInformation(String username, @NonNull DetailUser dtoKeyCloakUser) {
        return searchUserID(username)
                .flatMap(userId -> {
                    if (userId == null) {
                        // Return a message if user ID is not found
                        return Mono.just("User not found");
                    } else {
                        return keycloakProvider.getUserResource()
                                .flatMap(usersResource -> {
                                    UserRepresentation user = new UserRepresentation();
                                    user.setFirstName(dtoKeyCloakUser.getName());
                                    user.setLastName(dtoKeyCloakUser.getLastNameM());
                                    user.setUsername(username);
                                    return Mono.fromRunnable(() -> usersResource.get(userId).update(user))
                                            .then(Mono.just("The user was updated successfully!"));
                                })
                                .onErrorResume(e -> {
                                    log.error("Error updating user: {}", e.getMessage());
                                    return Mono.just("Error updating user: " + e.getMessage());
                                });
                    }
                });
    }

}