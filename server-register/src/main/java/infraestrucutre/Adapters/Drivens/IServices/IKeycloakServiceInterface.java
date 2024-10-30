package infraestrucutre.Adapters.Drivens.IServices;
 
 import org.keycloak.jose.jwk.JWK.Use;
 import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.lang.NonNull;
import infraestrucutre.Adapters.Drivens.DTOS.DtoKeyCloakUser;
import infraestrucutre.Adapters.Drivens.Entities.DetailUser;

import java.util.List;
 
 import reactor.core.publisher.Flux;
 import reactor.core.publisher.Mono;
 
 public interface IKeycloakServiceInterface {
    Mono<DtoKeyCloakUser> createUser(DtoKeyCloakUser DtoKeyCloakUser);

}



