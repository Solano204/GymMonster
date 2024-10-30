package infraestrucutre.Adapters.Drivers.Controllers;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import infraestrucutre.Adapters.Drivens.Handlers.MembershipHandler;
import infraestrucutre.Adapters.Drivens.Handlers.PromotionHandler;
import infraestrucutre.Adapters.Drivens.Handlers.RegisterClientHandler;
import reactor.core.publisher.Mono;


@Configuration
// Define the routes
public class RouterPromotion {
    
    @Bean
    public RouterFunction<ServerResponse> promotionRoutes(PromotionHandler promotionHandler) {
        return route(GET("/api/page/promotions/currentPromotions/{currentDate}"), promotionHandler::getAllCurrentPromotions) // Get all current
        // promotions
        .andRoute(GET("/api/page/promotions/specificDate/{date}"), promotionHandler::getAllPromotionsByDate); // Get promotions by date
    }
    
}