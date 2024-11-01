package infraestrucutre.Adapters.Drivens.Repositories;

import java.util.Collections;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiReactivePasswordChecker;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;

import application.Ports.Drivens.RepositoriesInterfaces.WorkTrainerRepositoryInterface;
import infraestrucutre.Adapters.Drivens.DTOS.DtoDetailUserReciving;
import infraestrucutre.Adapters.Drivens.DTOS.DtoDetailUserSent;
import infraestrucutre.Adapters.Drivens.DTOS.DtoTrainerData;
import infraestrucutre.Adapters.Drivens.Entities.AllTrainer;
import infraestrucutre.Adapters.Drivens.Entities.Schedule;
import infraestrucutre.Adapters.Drivens.Entities.WorkClass;
import infraestrucutre.Adapters.Drivens.Properties.ServicesUrl;
import lombok.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import infraestrucutre.Adapters.Drivens.DTOS.DtoDetailUserReciving;
import infraestrucutre.Adapters.Drivens.DTOS.DtoSpecialtyRecived;
import infraestrucutre.Adapters.Drivens.DTOS.DtoTrainerData;
import infraestrucutre.Adapters.Drivens.Entities.AllTrainer;
import infraestrucutre.Adapters.Drivens.Entities.WorkClass;
@Data
@Repository
@AllArgsConstructor
public class WorkTrainerRepository implements WorkTrainerRepositoryInterface {

    private final HaveIBeenPwnedRestApiReactivePasswordChecker passwordChecker;
    private final WebClient.Builder webClientBuilder;
                       private final ServicesUrl servicesUrl;


                       @Override
                       public Mono<String> createClassTrainer(AllTrainer trainer) {
                           return webClientBuilder.build()
                                   .post()
                                   .uri(servicesUrl.getInfo().getUrl() + "/trainers/AD/create")
                                   .contentType(MediaType.APPLICATION_JSON)
                                   .bodyValue(trainer)
                                   .retrieve()
                                   .bodyToMono(String.class);
                       }
                       
                       @Override
                       public Flux<DtoTrainerData> getAllTrainers(int page, int size) {
                           return webClientBuilder.build()
                                   .get()
                                   .uri(uriBuilder -> uriBuilder
                                           .scheme("http")
                                           .host("localhost")
                                           .port(8111)
                                           .path("/trainers/allInformation")
                                           .queryParam("page", page)
                                           .queryParam("size", size)
                                           .build())
                                   .accept(MediaType.APPLICATION_JSON)
                                   .retrieve()
                                   .bodyToFlux(DtoTrainerData.class);
                       }
                       
                       @Override
                       public Mono<List<DtoSpecialtyRecived>> getAllSpecialtiesFromTrainer(String username) {
                           return webClientBuilder.build()
                                   .get()
                                   .uri(servicesUrl.getInfo().getUrl() + "/trainers/specialties/{username}", username)
                                   .accept(MediaType.APPLICATION_JSON)
                                   .retrieve()
                                   .bodyToMono(new ParameterizedTypeReference<List<DtoSpecialtyRecived>>() {});
                       }
                       
                       @Override
                       public Mono<String> updateTrainerAllDetailInformation(String username, DtoDetailUserReciving details) {
                           return webClientBuilder.build()
                                   .put()
                                   .uri(servicesUrl.getInfo().getUrl() + "/trainers/AD/{username}/updateBasicInformation", username)
                                   .contentType(MediaType.APPLICATION_JSON)
                                   .bodyValue(details)
                                   .retrieve()
                                   .bodyToMono(String.class);
                       }
                       
                       @Override
                       public Mono<List<WorkClass>> getWorkClassesByTrainer(String username) {
                           return webClientBuilder.build()
                                   .get()
                                   .uri(servicesUrl.getInfo().getUrl() + "/trainers/classes/{username}", username)
                                   .accept(MediaType.APPLICATION_JSON)
                                   .retrieve()
                                   .bodyToMono(new ParameterizedTypeReference<List<WorkClass>>() {});
                       }
                       
                       @Override
                       public Mono<String> updateTrainerPassword(String username, String oldPassword, String newPassword) {
                           return webClientBuilder.build()
                                   .put()
                                   .uri(servicesUrl.getInfo().getUrl() + "/trainers/AD/{username}/{oldPassword}/{newPassword}/updatePassword", username, oldPassword, newPassword)
                                   .retrieve()
                                   .bodyToMono(String.class);
                       }
                       
                       @Override
                       public Mono<String> updateTrainerUsername(String oldUsername, String newUsername) {
                           return webClientBuilder.build()
                                   .put()
                                   .uri(servicesUrl.getInfo().getUrl() + "/trainers/AD/{oldUsername}/{newUsername}/updateUsername", oldUsername, newUsername)
                                   .retrieve()
                                   .bodyToMono(String.class);
                       }
                       
                       @Override
                       public Mono<String> updateEmail(String username, String newEmail) {
                           return webClientBuilder.build()
                                   .put()
                                   .uri(servicesUrl.getInfo().getUrl() + "/trainers/AD/{username}/{newEmail}/updateEmail", username, newEmail)
                                   .retrieve()
                                   .bodyToMono(String.class);
                       }
                       
                       @Override
                       public Mono<String> addSpecialtyToTrainer(String username, String newSpecialty) {
                           return webClientBuilder.build()
                                   .post()
                                   .uri(servicesUrl.getInfo().getUrl() + "/trainers/AD/{username}/{newSpecialty}/associateSpecialty", username, newSpecialty)
                                   .retrieve()
                                   .bodyToMono(String.class);
                       }
                       
                       @Override
                       public Mono<String> dessociateSpecialty(String username, String specialty) {
                           return webClientBuilder.build()
                                   .delete()
                                   .uri(servicesUrl.getInfo().getUrl() + "/trainers/AD/{username}/{specialty}/dessociateSpecialty", username, specialty)
                                   .retrieve()
                                   .bodyToMono(String.class);
                       }
                       
                       @Override
                       public Mono<String> addClassToTrainer(String username, String newClass) {
                           return webClientBuilder.build()
                                   .post()
                                   .uri(servicesUrl.getInfo().getUrl() + "/trainers/AD/{username}/{newClass}/addClass", username, newClass)
                                   .retrieve()
                                   .bodyToMono(String.class);
                       }
                       
                       @Override
                       public Mono<String> dessociateClassToTrainer(String username, String className) {
                           return webClientBuilder.build()
                                   .delete()
                                   .uri(servicesUrl.getInfo().getUrl() + "/trainers/AD/{username}/{className}/dessociateClass", username, className)
                                   .retrieve()
                                   .bodyToMono(String.class);
                       }
                       
                       @Override
                       public Mono<String> deleteClassTrainerByUsernameWithPassword(String username, String password) {
                           return webClientBuilder.build()
                                   .delete()
                                   .uri(servicesUrl.getInfo().getUrl() + "/trainers/AD/{username}/{password}/delete", username, password)
                                   .retrieve()
                                   .bodyToMono(String.class);
                       }
}