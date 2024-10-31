package infraestrucutre.Adapters.Drivens.ImpServices;

import org.springframework.stereotype.Component;

import infraestrucutre.Adapters.Drivens.DTOS.DtoDataReciving;
import infraestrucutre.Adapters.Drivens.DTOS.DtoKeyCloakUser;
import infraestrucutre.Adapters.Drivens.Entities.AllClient;
import lombok.Data;

@Component
public class FillinKeycloakUser {

    public DtoKeyCloakUser  fillinKeycloakUser(DtoDataReciving dtoDataReciving) {

        DtoKeyCloakUser dtoKeyCloakUser = new DtoKeyCloakUser();
        dtoKeyCloakUser.setUsername(dtoDataReciving.username());
        dtoKeyCloakUser.setEmail(dtoDataReciving.email());
        dtoKeyCloakUser.setFirstName(dtoDataReciving.name());
        dtoKeyCloakUser.setLastName(dtoDataReciving.lastNameM());
        dtoKeyCloakUser.setPassword(dtoDataReciving.password());
        return dtoKeyCloakUser;
    }

    public DtoDataReciving fillinDataFromClient(AllClient client) {
        DtoDataReciving dtoDataReciving = new DtoDataReciving(client.username(), client.email(), client.password(),
                client.name(), client.secondname(), client.lastnamep(), client.lastnamem(), client.age(),
                client.height(), client.weight());
        return dtoDataReciving;
    }
}