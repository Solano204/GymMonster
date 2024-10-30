package infraestrucutre.Adapters.Drivens.Handlers;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import application.Ports.Drivers.IServices.PromotionServiceInterface;
import infraestrucutre.Adapters.Drivens.Entities.Membership;
import infraestrucutre.Adapters.Drivens.Entities.Promotion;
import infraestrucutre.Adapters.Drivens.ImpServices.PromotionService;
import lombok.AllArgsConstructor;
import lombok.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
@AllArgsConstructor
public class PromotionHandler {

    private final PromotionServiceInterface promotionService;

    // Create a new Promotion
    public Mono<ServerResponse> createPromotion(ServerRequest request) {
        Mono<Promotion> promotionMono = request.bodyToMono(Promotion.class);
        return errorHandler(
                promotionMono.flatMap(promotion -> promotionService.createPromotion(promotion)
                        .flatMap(savedPromotion -> ServerResponse.status(HttpStatus.CREATED)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(savedPromotion)))
        );
    }

    // Get all Promotions
    public Mono<ServerResponse> getAllPromotions(ServerRequest request) {
        return errorHandler(
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(promotionService.getAllPromotions(), Promotion.class)
        );
    }

    // Get a Promotion by End Date
    public Mono<ServerResponse> getPromotionStartDate(ServerRequest request) {
        String dateString = request.pathVariable("startDate");
        LocalDate date = LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE);
    
        System.out.println("Date: " + date);
        return errorHandler(promotionService.getPromotionByStartDate(date)
                .collectList() // Convert Flux<Promotion> to Mono<List<Promotion>>
                .flatMap(promotions -> {
                    if (promotions.isEmpty()) {
                        return ServerResponse.notFound().build(); // If no promotions found, return 404
                    }
                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(promotions); // Return the list of promotions in the response
                }));
    }

    public Mono<ServerResponse> getPromotionEndDate(ServerRequest request) {
        String dateString = request.pathVariable("endDate");
        LocalDate date = LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE);
        return errorHandler(promotionService.getPromotionByEndDate(date)
                .collectList() // Convert Flux<Promotion> to Mono<List<Promotion>>
                .flatMap(promotions -> {
                    if (promotions.isEmpty()) {
                        return ServerResponse.notFound().build(); // If no promotions found, return 404
                    }
                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(promotions); // Return the list of promotions in the response
                }));
    }

    public Mono<ServerResponse> getPromotionCurrentDate(ServerRequest request) {
        String dateString = request.pathVariable("currentDate");
        LocalDate date = LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE);
        return errorHandler(promotionService.getPromotionByStartDate(date)
                .collectList() // Convert Flux<Promotion> to Mono<List<Promotion>>
                .flatMap(promotions -> {
                    if (promotions.isEmpty()) {
                        return ServerResponse.notFound().build(); // If no promotions found, return 404
                    }
                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(promotions); // Return the list of promotions in the response
                }));
    }
    

    // Update a Promotion by ID
    public Mono<ServerResponse> updatePromotion(ServerRequest request) {
        Integer id = Integer.parseInt(request.pathVariable("id"));
        Mono<Promotion> promotionMono = request.bodyToMono(Promotion.class);
        return errorHandler(
                promotionMono.flatMap(promotion -> promotionService.updatePromotion(id, promotion))
                        .flatMap(updatedPromotion -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(updatedPromotion))
                        .switchIfEmpty(ServerResponse.notFound().build())
        );
    }

    // Delete a Promotion by ID
    public Mono<ServerResponse> deletePromotion(ServerRequest request) {
        Integer id = Integer.parseInt(request.pathVariable("id"));
        return errorHandler(
                promotionService.deletePromotion(id)
                        .then(ServerResponse.noContent().build())
        );
    }

    // Error handler method
    private Mono<ServerResponse> errorHandler(Mono<ServerResponse> response) {
        return response.onErrorResume(error -> {
            if (error instanceof WebClientResponseException errorResponse) {
                if (errorResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
                    Map<String, Object> body = new HashMap<>();
                    body.put("error", "Promotion not found: ".concat(errorResponse.getMessage()));
                    body.put("timestamp", new Date());
                    body.put("status", errorResponse.getStatusCode().value());
                    return ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue(body);
                }
                return ServerResponse.status(errorResponse.getStatusCode())
                        .bodyValue(errorResponse.getResponseBodyAsString());
            }
            Map<String, Object> body = new HashMap<>();
            body.put("error", "An unexpected error occurred");
            body.put("timestamp", new Date());
            return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue(body);
        });
    }
}