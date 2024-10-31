package application.Ports.Drivers.IServices;

import java.util.List;

import infraestrucutre.Adapters.Drivens.DTOS.DtoDetailUserSent;
import infraestrucutre.Adapters.Drivens.DTOS.DtoInfoGeneralClient;
import infraestrucutre.Adapters.Drivens.DTOS.DtoSpecialtyRecived;
import infraestrucutre.Adapters.Drivens.DTOS.DtoSpecialtySent;
import infraestrucutre.Adapters.Drivens.Entities.AllClient;
import infraestrucutre.Adapters.Drivens.Entities.WorkClass;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ClientServiceInterface {

    Mono<List<String>> createClient(AllClient client);

    Mono<String> updateClientPassword(String username, String newPassword, String oldPassword);

    Mono<String> updateClientUsername(String username, String newUsername);
    
    Mono<String> deleteAccount(String username, String password);

    public Mono<String> updateClientTrainer(String username, String membership) ;
    Mono<String> updateClientEmail(String username, String newEmail);

     Mono<String> updateClientMembership(String username, String membership);

     Mono<String> updateClientAllDetailInformation(String username, DtoDetailUserSent newDatUser);
    
     Mono<String> removeMembership(String username, String membershipType);

     Mono<String> removeTrainer(String username, String usernameTrainer);

    Mono<List<WorkClass>> getWorkClassesByClientId(String username);

    public Mono<AllClient> getClient(String username);

}
