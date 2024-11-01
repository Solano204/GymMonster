package application.Ports.Drivers.IServices;
 
 import org.keycloak.jose.jwk.JWK.Use;
 import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.lang.NonNull;

import infraestrucutre.Adapters.Drivens.DTOS.DtoKeyCloakUser;
import infraestrucutre.Adapters.Drivens.Entities.DetailPerTrainer;
import infraestrucutre.Adapters.Drivens.Entities.DetailUser;
import infraestrucutre.Adapters.Drivens.Entities.DetailsClassTrainer;

import java.util.List;
 
 import reactor.core.publisher.Flux;
 import reactor.core.publisher.Mono;
 
 public interface IKeycloakServiceInterface {
    Flux<UserRepresentation> findAllUsers();
    Flux<UserRepresentation> searchUserByUsername(String username);
    Mono<DtoKeyCloakUser> createUser(DtoKeyCloakUser DtoKeyCloakUser);
    Mono<String> deleteUser(String userId);

    public Mono<String> updateTrainerAllInformation(String username, @NonNull DetailPerTrainer dtoKeyCloakUser);
    public Mono<String> updateUserAllInformation(String username, @NonNull DetailUser dtoKeyCloakUser);
     public Mono<DtoKeyCloakUser> updateUsername2(String newUsername, String oldUsername, String newPassword,String role); ;
    
     public Mono<String> changeUsername(String username, String newUsename);
     public Mono<String> changeEmail(String username, String newEmail);
     public Mono<String> changePassword(String username, String newPassword);
     public Mono<String> searchUserID(String username);
     public Mono<String> updateClassTrainerAllInformation(String username, @NonNull DetailsClassTrainer dtoKeyCloakUser);
}



