package infraestrucutre.Adapters.Drivens.ImpServices;

import infraestrucutre.Adapters.Drivens.Configurations.KeycloakProvider;
import infraestrucutre.Adapters.Drivens.DTOS.DtoKeyCloakUser;
import infraestrucutre.Adapters.Drivens.Entities.DetailUser;
import infraestrucutre.Adapters.Drivens.IServices.IKeycloakServiceInterface;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Data
public class KeycloakServiceImpl implements IKeycloakServiceInterface {

    private final KeycloakProvider keycloakProvider;

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



}