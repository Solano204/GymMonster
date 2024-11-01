package application.Ports.Drivers.IServices;

import java.util.List;

import infraestrucutre.Adapters.Drivens.DTOS.DtoInfoGeneralClient;
import infraestrucutre.Adapters.Drivens.Entities.AllClient;
import infraestrucutre.Adapters.Drivens.Entities.Client;
import infraestrucutre.Adapters.Drivens.Entities.DetailUser;
import infraestrucutre.Adapters.Drivens.Entities.WorkClass;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


    
    public interface ClientServiceInterface {
    
        Mono<String> deleteClient(String username, String password);
    
        Mono<AllClient> getClientDetailMembershipByClientId(String username);
    
        Flux<WorkClass> getWorkClassesByClientId(String username);
    
        Mono<Boolean> existsByUsernameClient(String username);
    
        Mono<Boolean> existsByEmailClient(String email);
    
        Mono<List<String>> createClient(AllClient client);
    
        Flux<AllClient> getAllClientsAD(int page, int size);
    
        Mono<Client> getClientByUsername(String username);
    
        Mono<Client> updateClient(String username, Client clients);
    
        Mono<String> updateClientAllDetailInformation(String username, DetailUser newDatUser);
    
        Mono<String> updateClientPassword(String username, String newPassword, String oldPassword);
    
        Mono<String> updateClientUsername(String oldUsername, String newUsername,String password);
    
        Mono<String> updateClientEmail(String username, String newEmail);
    
        Mono<String> updateClientMembership(String username, String newMembership);

        Mono<String> dessignClientMembership(String username, String newMembership);

        Mono<String> dessignTrainer(String username, String usernameTrainer);
        Mono<String> updateTrainer(String username, String usernameTrainer);
    }
    
