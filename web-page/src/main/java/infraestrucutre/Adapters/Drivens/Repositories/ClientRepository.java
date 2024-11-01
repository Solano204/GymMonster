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
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.authentication.password.CompromisedPasswordDecision;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiReactivePasswordChecker;

import static org.springframework.http.MediaType.*;

import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
// import static org.springframework.web.reactive.function.BodyInserters.*;
import org.springframework.web.reactive.function.client.WebClient;

import application.Ports.Drivens.InterfaceRepositories.ClientRepositoryInterface;
import infraestrucutre.Adapters.Drivens.DTOS.DtoDetailUserReciving;
import infraestrucutre.Adapters.Drivens.DTOS.DtoDetailUserSent;
import infraestrucutre.Adapters.Drivens.DTOS.DtoInfoGeneralClient;
import infraestrucutre.Adapters.Drivens.DTOS.DtoInfoTrainerSent;
import infraestrucutre.Adapters.Drivens.DTOS.DtoTrainerReciving;
import infraestrucutre.Adapters.Drivens.DTOS.DtoTrainerSent;
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
	private final KafkaTemplate<String, AllClient> postmanRegistration;

    private final ServicesUrl servicesUrl;

	public Mono<String> registerNewClient(AllClient newClient) {
		try {
			postmanRegistration.send("flow", newClient);
			System.out.println("registerNewClient");
			return Mono.just(
					"Its request to be a new client from GYM MASTER is being processed, as soon as possible we will contact you (By email)");
		} catch (Exception e) {
			// Handle any errors that may occur when sending the message
			return Mono.error(new RuntimeException("Failed to register new client: " + e.getMessage()));
		}
	}
		
	

	@Override
public Mono<String> unassignTrainer(String username, String usernameTrainer) {
    return webClientBuilder.build()
            .delete()
            .uri(servicesUrl.getInfo().getUrl() + "/api/clients/{username}/{usernameTrainer}/dessignTrainer", username, usernameTrainer)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(String.class);
}

@Override
public Mono<List<WorkClass>> getWorkClassesByClient(String username) {
    return webClientBuilder.build()
            .get()
            .uri(servicesUrl.getInfo().getUrl() + "/api/clients/{username}/workClasses", username)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToFlux(WorkClass.class)
            .collectList();
}

@Override
public Mono<AllClient> getClientData(String username) {
    return webClientBuilder.build()
            .get()
            .uri(servicesUrl.getInfo().getUrl() + "/api/clients/{username}/allInformation", username)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(AllClient.class);
}

@Override
public Mono<String> unassignMembership(String username, String membershipType) {
    return webClientBuilder.build()
            .delete()
            .uri(servicesUrl.getInfo().getUrl() + "/api/clients/{username}/{membershipType}/dessignMembership", username, membershipType)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(String.class);
}

@Override
public Mono<String> deleteClient(String username, String password) {
    return webClientBuilder.build()
            .delete()
            .uri(servicesUrl.getInfo().getUrl() + "/api/clients/{username}/{password}/deleteAccount", username, password)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(String.class);
}

@Override
public Mono<String> createClient(AllClient client) {
    return webClientBuilder.build()
            .post()
            .uri(servicesUrl.getInfo().getUrl() + "/api/clients/AD/create")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(client)
            .retrieve()
            .bodyToMono(String.class);
}

@Override
public Mono<Boolean> validateIfUserNameExistsClient(String username) {
    return webClientBuilder.build()
            .get()
            .uri(servicesUrl.getInfo().getUrl() + "/api/clients/{username}/usernameExist", username)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(Boolean.class);
}

@Override
public Mono<Boolean> validateIfEmailExistsClient(String email) {
    return webClientBuilder.build()
            .get()
            .uri(servicesUrl.getInfo().getUrl() + "/api/clients/{email}/emailExist", email)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(Boolean.class);
}

@Override
public Mono<Boolean> validateOldPasswordMatchers(String username, String password) {
    return webClientBuilder.build()
            .put()
            .uri(servicesUrl.getInfo().getUrl() + "/api/clients/validatePasswordMatcher/{username}/{oldPassword}/{password}", username, password)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(Boolean.class);
}

@Override
public Mono<String> changePassword(String username, String oldPassword, String newPassword) {
    return webClientBuilder.build()
            .put()
            .uri(servicesUrl.getInfo().getUrl() + "/api/clients/{username}/{oldPassword}/{newPassword}/changePassword", username, oldPassword, newPassword)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(String.class);
}

@Override
public Mono<String> changeEmail(String username, String email) {
    return webClientBuilder.build()
            .put()
            .uri(servicesUrl.getInfo().getUrl() + "/api/clients/{username}/{email}/changeEmail", username, email)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(String.class);
}

@Override
public Mono<String> updateAllInformation(DtoDetailUserSent newInformation, String username) {
    return webClientBuilder.build()
            .put()
            .uri(servicesUrl.getInfo().getUrl() + "/api/clients/{username}/updateBasicInformation", username)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(newInformation)
            .retrieve()
            .bodyToMono(String.class);
}

@Override
public Mono<Boolean> validatePasswordRegister(String password) {
    Mono<CompromisedPasswordDecision> validator = passwordChecker.check(password);
    return validator.flatMap(decision -> Mono.just(decision.isCompromised()));
}

@Override
public Mono<String> changeUsername(String username, String newUsername) {
    return webClientBuilder.build()
            .put()
            .uri(servicesUrl.getInfo().getUrl() + "/api/clients/{username}/{newUsername}/changeUsername", username, newUsername)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(String.class);
}

@Override
public Mono<String> changeMembership(String username, String membershipType) {
    return webClientBuilder.build()
            .put()
            .uri(servicesUrl.getInfo().getUrl() + "/api/clients/{username}/{membershipType}/assignMembership", username, membershipType)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(String.class);
}

@Override
public Mono<String> changeTrainer(String username, String usernameTrainer) {
    return webClientBuilder.build()
            .put()
            .uri(servicesUrl.getInfo().getUrl() + "/api/clients/{username}/{usernameTrainer}/assignTrainer", username, usernameTrainer)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(String.class);
}

@Override
public Mono<String> updateClient(String username, AllClient client) {
    return webClientBuilder.build()
            .put()
            .uri(servicesUrl.getInfo().getUrl() + "/api/clients/{username}/updateBasicInformation", username)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(client)
            .retrieve()
            .bodyToMono(String.class);
}

@Override
public Mono<String> updateClient(String username, DtoDetailUserSent client) {
    return webClientBuilder.build()
            .put()
            .uri(servicesUrl.getInfo().getUrl() + "/api/clients/{username}/updateBasicInformation", username)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(client)
            .retrieve()
            .bodyToMono(String.class);
}


}
