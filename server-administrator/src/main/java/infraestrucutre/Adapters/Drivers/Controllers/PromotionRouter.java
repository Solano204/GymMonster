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
import lombok.Data;
import reactor.core.publisher.Mono;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@Data
public class PromotionRouter {

   @Bean
public RouterFunction<ServerResponse> promotionRoutes(PromotionHandler promotionHandler) {
    return route(GET("/api/admin/promotions"), promotionHandler::getAllPromotions)
            .andRoute(GET("/api/admin/promotions/current/{currentDate}"), promotionHandler::getAllCurrentPromotions)
            .andRoute(GET("/api/admin/promotions/start/{date}"), promotionHandler::getAllPromotionsByStartDate)
            .andRoute(GET("/api/admin/promotions/end/{date}"), promotionHandler::getAllPromotionsByEndDate)
            .andRoute(POST("/api/admin/promotions"), promotionHandler::createPromotion)
            .andRoute(PUT("/api/admin/promotions/{id}"), promotionHandler::updatePromotion)
            .andRoute(DELETE("/api/admin/promotions/{id}"), promotionHandler::deletePromotionById);
}

}
