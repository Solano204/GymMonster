package infraestrucutre.Adapters.Drivers.Controllers;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import infraestrucutre.Adapters.Drivens.Handlers.ClientHandler;
import infraestrucutre.Adapters.Drivens.Handlers.PromotionHandler;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class PromotionRouter {


    @Bean
    public RouterFunction<ServerResponse> promotionsRouterFunction(PromotionHandler clientHandler) {
        return route(POST("api/promotions"), clientHandler::createPromotion) // Create a new client
                .andRoute(GET("/api/promotions"), clientHandler::getAllPromotions) // Get all clients
                .andRoute(GET("/api/promotions/startDate/{startDate}"), clientHandler::getPromotionStartDate) // Get client by username
                .andRoute(GET("/api/promotions/endDate/{endDate}"), clientHandler::getPromotionEndDate) // Get client by username
                .andRoute(GET("/api/promotions/CurrentDate/{currentDate}"), clientHandler::getPromotionCurrentDate) // Get client by username
                .andRoute(PUT("/api/promotions/{id}"), clientHandler::updatePromotion) // Update client by username
                .andRoute(DELETE("/api/promotions/{id}"), clientHandler::deletePromotion);// Delete client by username  ; // Get work classes by client ID
    }
}
