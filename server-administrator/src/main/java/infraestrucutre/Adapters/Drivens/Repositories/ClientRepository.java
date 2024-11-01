package infraestrucutre.Adapters.Drivens.Repositories;

import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.authentication.password.CompromisedPasswordDecision;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiReactivePasswordChecker;

import static org.springframework.http.MediaType.*;

import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
// import static org.springframework.web.reactive.function.BodyInserters.*;
import org.springframework.web.reactive.function.client.WebClient;

import application.Ports.Drivens.RepositoriesInterfaces.ClientRepositoryInterface;
import infraestrucutre.Adapters.Drivens.DTOS.DtoDetailUserReciving;
import infraestrucutre.Adapters.Drivens.DTOS.DtoDetailUserSent;
import infraestrucutre.Adapters.Drivens.DTOS.DtoInfoGeneralClient;
import infraestrucutre.Adapters.Drivens.DTOS.DtoInfoTrainerSent;
import infraestrucutre.Adapters.Drivens.Entities.AllClient;
import infraestrucutre.Adapters.Drivens.Entities.WorkClass;
import infraestrucutre.Adapters.Drivens.Properties.ServicesUrl;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import lombok.AllArgsConstructor;
import lombok.Data;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

        @Data
        @Repository
        @AllArgsConstructor
        public class ClientRepository implements ClientRepositoryInterface {

                private final HaveIBeenPwnedRestApiReactivePasswordChecker passwordChecker;
                private final WebClient.Builder webClientBuilder;
                private final ServicesUrl servicesUrl;
                // private final KafkaTemplate<String, AllClient> postmanRegistration;

            
                // Fetch all clients
               // Fetch all clients
@Override
public Mono<List<AllClient>> getAllClients() {
    return webClientBuilder.build()
            .get()
            .uri(servicesUrl.getInfo().getUrl() + "/api/clients/AD/allClients") // Dynamic URL
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToFlux(AllClient.class)
            .collectList(); // Returning a list of AllClient
}

// Update client by username
@Override
public Mono<String> updateClient(String username, DtoDetailUserSent client) {
    return webClientBuilder.build()
            .put()
            .uri(servicesUrl.getInfo().getUrl() + "/api/clients/{username}/updateBasicInformation", username) // Use dynamic URL
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(client)
            .retrieve()
            .bodyToMono(String.class);
}

// Change password
@Override
public Mono<String> changePassword(String username, String oldPassword, String newPassword) {
    return webClientBuilder.build()
            .put()
            .uri(uriBuilder -> uriBuilder
                    .scheme("http")
                    .host(servicesUrl.getInfo().getUrl()) // Use dynamic URL
                    .path("/api/clients/{username}/{oldPassword}/{newPassword}/changePassword")
                    .build(username, oldPassword, newPassword))
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(String.class);
}

// Change email
@Override
public Mono<String> changeEmail(String username, String email) {
    return webClientBuilder.build()
            .put()
            .uri(uriBuilder -> uriBuilder
                    .scheme("http")
                    .host(servicesUrl.getInfo().getUrl()) // Use dynamic URL
                    .path("/api/clients/{username}/{email}/changeEmail")
                    .build(username, email))
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(String.class);
}

// Change username
@Override
public Mono<String> changeUsername(String username, String newUsername) {
    return webClientBuilder.build()
            .put()
            .uri(uriBuilder -> uriBuilder
                    .scheme("http")
                    .host(servicesUrl.getInfo().getUrl()) // Use dynamic URL
                    .path("/api/clients/{username}/{newUsername}/changeUsername")
                    .build(username, newUsername))
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(String.class);
}

// Change membership
@Override
public Mono<String> changeMembership(String username, String membershipType) {
    return webClientBuilder.build()
            .put()
            .uri(uriBuilder -> uriBuilder
                    .scheme("http")
                    .host(servicesUrl.getInfo().getUrl()) // Use dynamic URL
                    .path("/api/clients/{username}/{membershipType}/assignMembership")
                    .build(username, membershipType))
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(String.class);
}

// Unassign membership
@Override
public Mono<String> unassignMembership(String username, String membershipType) {
    return webClientBuilder.build()
            .delete()
            .uri(uriBuilder -> uriBuilder
                    .scheme("http")
                    .host(servicesUrl.getInfo().getUrl()) // Use dynamic URL
                    .path("/api/clients/{username}/{membershipType}/dessignMembership")
                    .build(username, membershipType))
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(String.class);
}

// Change trainer
@Override
public Mono<String> changeTrainer(String username, String usernameTrainer) {
    return webClientBuilder.build()
            .put()
            .uri(uriBuilder -> uriBuilder
                    .scheme("http")
                    .host(servicesUrl.getInfo().getUrl()) // Use dynamic URL
                    .path("/api/clients/{username}/{usernameTrainer}/assignTrainer")
                    .build(username, usernameTrainer))
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(String.class);
}

// Unassign trainer
@Override
public Mono<String> unassignTrainer(String username, String usernameTrainer) {
    return webClientBuilder.build()
            .delete()
            .uri(uriBuilder -> uriBuilder
                    .scheme("http")
                    .host(servicesUrl.getInfo().getUrl()) // Use dynamic URL
                    .path("/api/clients/{username}/{usernameTrainer}/dessignTrainer")
                    .build(username, usernameTrainer))
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(String.class);
}

// Get work classes by client ID
@Override
public Mono<List<WorkClass>> getWorkClassesByClientId(String username) {
    return webClientBuilder.build()
            .get()
            .uri(servicesUrl.getInfo().getUrl() + "/api/clients/{username}/workClasses", username) // Use dynamic URL
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToFlux(WorkClass.class)
            .collectList();
}

// Check if username exists
@Override
public Mono<Boolean> validateIfUserNameExistsClient(String username) {
    return webClientBuilder.build()
            .get()
            .uri(servicesUrl.getInfo().getUrl() + "/api/clients/{username}/usernameExist", username) // Use dynamic URL
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(Boolean.class);
}

// Check if email exists
@Override
public Mono<Boolean> validateIfEmailExistsClient(String email) {
    return webClientBuilder.build()
            .get()
            .uri(servicesUrl.getInfo().getUrl() + "/api/clients/{email}/emailExist", email) // Use dynamic URL
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(Boolean.class);
}

// Delete client by username and password
@Override
public Mono<String> deleteClient(String username, String password) {
    return webClientBuilder.build()
            .delete()
            .uri(uriBuilder -> uriBuilder
                    .scheme("http")
                    .host(servicesUrl.getInfo().getUrl()) // Use dynamic URL
                    .path("/api/clients/{username}/{password}/deleteAccount")
                    .build(username, password))
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(String.class);
}

// Create a new client
@Override
public Mono<String> createClient(AllClient client) {
    return webClientBuilder.build()
            .post()
            .uri(servicesUrl.getInfo().getUrl() + "/api/clients/AD/create") // Use dynamic URL
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(client)
            .retrieve()
            .bodyToMono(String.class);
}


    @Override
    // Validate password registration
    public Mono<Boolean> validatePasswordRegister(String password) {
        return passwordChecker.check(password)
                .map(decision -> decision.isCompromised());
    }

        }
