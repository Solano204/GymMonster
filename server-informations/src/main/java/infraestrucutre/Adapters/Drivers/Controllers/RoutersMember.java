package infraestrucutre.Adapters.Drivers.Controllers;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import org.springframework.web.reactive.function.server.ServerResponse;

import infraestrucutre.Adapters.Drivens.Handlers.ClientHandler;
import infraestrucutre.Adapters.Drivens.Handlers.EquipamentHandler;
import infraestrucutre.Adapters.Drivens.Handlers.MembershipHandler; 

@Configuration
public class RoutersMember {
    @Bean
    public RouterFunction<ServerResponse> membershipRoutes(MembershipHandler membershipHandler) {

        return route(POST("/api/memberships"),      membershipHandler::createMembership) // Create a new membership
                .andRoute(GET("/api/memberships"), membershipHandler::getAllMemberships) // Get all memberships
                .andRoute(GET("/api/memberships/{type}"), membershipHandler::getMembershipByType) // Get membership by
                                                                                                  // type
                .andRoute(PUT("/api/memberships/{id}"), membershipHandler::updateMembership) // Update membership by ID
                .andRoute(DELETE("/api/memberships/{type}"), membershipHandler::deleteMembershipByType); // Delete
    }
}
