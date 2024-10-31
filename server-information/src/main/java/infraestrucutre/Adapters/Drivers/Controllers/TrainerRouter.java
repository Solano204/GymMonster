package infraestrucutre.Adapters.Drivers.Controllers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import infraestrucutre.Adapters.Drivens.Handlers.TrainerHandler;

import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.RequestPredicates;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class TrainerRouter {

    @Bean
public RouterFunction<ServerResponse> trainerRoutes(TrainerHandler handler) {
    return RouterFunctions
            .nest(RequestPredicates.path("/trainers"),
                    route(RequestPredicates.POST("/AD/create"), handler::createPerTrainer)
                    .andRoute(RequestPredicates.GET("/allInformation"), handler::getAllClassTrainers)
                    .andRoute(RequestPredicates.PUT("/AD/{username}/updateBasicInformation"), handler::updateTrainerAllDetailInformation)
                    .andRoute(RequestPredicates.GET("/classes/{username}"), handler::getWorkClassesByTrainer)
                    .andRoute(RequestPredicates.GET("/specialties/{username}"), handler::getAllSpecialtyOfTrainer)
                    .andRoute(RequestPredicates.PUT("/AD/{username}/{oldPassword}/{newPassword}/updatePassword"), handler::updateTrainerPassword)
                    .andRoute(RequestPredicates.PUT("/AD/{oldUsername}/{newUsername}/{password}/updateUsername"), handler::updateTrainerUsername)
                    .andRoute(RequestPredicates.PUT("/AD/{username}/{newEmail}/updateEmail"), handler::updateClientEmail)
                    .andRoute(RequestPredicates.POST("/AD/{username}/{newSpecialty}/associateSpecialty"), handler::addSpecialtyToTrainer)
                    .andRoute(RequestPredicates.DELETE("/AD/{username}/{specialty}/dessociateSpecialty"), handler::dessociateSpecialty)
                    .andRoute(RequestPredicates.POST("/AD/{username}/{newClass}/addClass"), handler::addClassToTrainer)
                    .andRoute(RequestPredicates.DELETE("/AD/{username}/{className}/dessociateClass"), handler::dessociateClassToTrainer)
                    .andRoute(RequestPredicates.DELETE("/AD/{username}/{password}/delete"), handler::deleteClassTrainerByUsernameWithPassword)
            );
}

}
