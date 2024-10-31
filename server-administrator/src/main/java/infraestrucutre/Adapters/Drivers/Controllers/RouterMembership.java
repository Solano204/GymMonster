package infraestrucutre.Adapters.Drivers.Controllers;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import infraestrucutre.Adapters.Drivens.Handlers.MembershipHandler;

@Configuration
public class RouterMembership {
    @Bean
public RouterFunction<ServerResponse> membershipRoutes(MembershipHandler membershipHandler) {
    return RouterFunctions.route()
            .GET("/api/admin/memberships", membershipHandler::getAllMemberships) // Get all memberships
            .POST("/api/admin/memberships", membershipHandler::createMembership) // Create a new membership
            .PUT("/api/admin/memberships/{id}", membershipHandler::updateMembership) // Update membership by ID
            .DELETE("/api/admin/memberships/{type}", membershipHandler::deleteMembershipByType) // Delete membership by type
            .build();
}

    
} 