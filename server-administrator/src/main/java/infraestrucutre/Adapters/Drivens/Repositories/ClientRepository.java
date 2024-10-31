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
                // private final KafkaTemplate<String, AllClient> postmanRegistration;


                public Mono<List<AllClient>> getAllClients() {
                return webClientBuilder.build()
                        .get()
                        .uri("http://localhost:8111/api/clients/AD/allClients") // Assuming Service B has this endpoint
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .bodyToFlux(AllClient.class)
                        .collectList(); // Returning a list of AllClient
        }

        // Fetch all clients

    // Update client by username
    @Override
    public Mono<String> updateClient(String username, DtoDetailUserSent client) {
        return webClientBuilder.build()
                .put()
                .uri("http://localhost:8111/api/clients/{username}/updateBasicInformation", username)
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
                        .host("localhost")
                        .port(8111)
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
                        .host("localhost")
                        .port(8111)
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
                        .host("localhost")
                        .port(8111)
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
                        .host("localhost")
                        .port(8111)
                        .path("/api/clients/{username}/{membershipType}/assignMembership")
                        .build(username, membershipType))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class);
    }


    @Override
    // Unassign membership
        public Mono<String> unassignMembership(String username, String membershipType) {
                return webClientBuilder.build()
                        .delete()
                        .uri(uriBuilder -> uriBuilder
                                .scheme("http")
                                .host("localhost")
                                .port(8111)
                                .path("/api/clients/{username}/{membershipType}/dessignMembership")
                                .build(username, membershipType))
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .bodyToMono(String.class);
        }


        @Override
    // Change trainer
    public Mono<String> changeTrainer(String username, String usernameTrainer) {
        return webClientBuilder.build()
                .put()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("localhost")
                        .port(8111)
                        .path("/api/clients/{username}/{usernameTrainer}/assignTrainer")
                        .build(username, usernameTrainer))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class);
    }


    @Override
    // Unassign trainer
    public Mono<String> unassignTrainer(String username, String usernameTrainer) {
        return webClientBuilder.build()
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("localhost")
                        .port(8111)
                        .path("/api/clients/{username}/{usernameTrainer}/dessignTrainer")
                        .build(username, usernameTrainer))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class);
    }


    @Override
    // Get work classes by client ID
    public Mono<List<WorkClass>> getWorkClassesByClientId(String username) {
        return webClientBuilder.build()
                .get()
                .uri("http://localhost:8111/api/clients/{username}/workClasses", username)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(WorkClass.class)
                .collectList();
    }


    @Override
    // Check if username exists
    public Mono<Boolean> validateIfUserNameExistsClient(String username) {
        return webClientBuilder.build()
                .get()
                .uri("http://localhost:8111/api/clients/{username}/usernameExist", username)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Boolean.class);
    }


    @Override
    // Check if email exists
    public Mono<Boolean> validateIfEmailExistsClient(String email) {
        return webClientBuilder.build()
                .get()
                .uri("http://localhost:8111/api/clients/{email}/emailExist", email)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Boolean.class);
    }


    @Override
    // Delete client by username and password
    public Mono<String> deleteClient(String username, String password) {
        return webClientBuilder.build()
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("localhost")
                        .port(8111)
                        .path("/api/clients/{username}/{password}/deleteAccount")
                        .build(username, password))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class);
    }


    @Override
    // Create a new client
    public Mono<String> createClient(AllClient client) {
        return webClientBuilder.build()
                .post()
                .uri("http://localhost:8111/api/clients/AD/create")
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
