package infraestrucutre.Adapters.Drivens.Handlers;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import infraestrucutre.Adapters.Drivens.DTOS.DtoMembershipReciving;
import infraestrucutre.Adapters.Drivens.Entities.AllClient;
import infraestrucutre.Adapters.Drivens.Entities.Promotion;
import infraestrucutre.Adapters.Drivens.ImpServices.MembershipService;
import infraestrucutre.Adapters.Drivens.ImpServices.PoolService;
import infraestrucutre.Adapters.Drivens.ImpServices.PromotionService;
import lombok.*;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*; 
@Component
@AllArgsConstructor
@Data
public class PromotionHandler {

    private final PromotionService promotionService;

    // Get all current promotions
    public Mono<ServerResponse> getAllPromotions(ServerRequest request) {
        return promotionService.getAllPromotions()
                .collectList()
                .flatMap(list -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(list))
                .onErrorResume(this::errorHandler);
    }

    public Mono<ServerResponse> getAllCurrentPromotions(ServerRequest request) {
        String currentDate = request.pathVariable("currentDate");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate parsedDate = LocalDate.parse(currentDate, formatter);
        return promotionService.getPromotionsByCurrentDate(parsedDate)
                .collectList()
                .flatMap(list -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(list))
                .onErrorResume(this::errorHandler);
    }

    // Get all promotions by date
    public Mono<ServerResponse> getAllPromotionsByStartDate(ServerRequest request) {
        String date = request.pathVariable("date");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate parsedDate = LocalDate.parse(date, formatter);
        return promotionService.getPromotionsByStartDate(parsedDate)
                .collectList()
                .flatMap(list -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(list))
                .onErrorResume(this::errorHandler);
    }

    public Mono<ServerResponse> getAllPromotionsByEndDate(ServerRequest request) {
        String date = request.pathVariable("date");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate parsedDate = LocalDate.parse(date, formatter);
        return promotionService.getPromotionsByEndDate(parsedDate)
                .collectList()
                .flatMap(list -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(list))
                .onErrorResume(this::errorHandler);
    }

     public Mono<ServerResponse> createPromotion(ServerRequest request) {
        return request.bodyToMono(Promotion.class)
                .flatMap(promotion -> promotionService.createPromotion(promotion))
                .flatMap(savedPromotion -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(savedPromotion));
    }

    public Mono<ServerResponse> updatePromotion(ServerRequest request) {
        Integer id = Integer.valueOf(request.pathVariable("id"));
        return request.bodyToMono(Promotion.class)
                .flatMap(updatedPromotion -> promotionService.updatePromotion(id, updatedPromotion))
                .flatMap(response -> ServerResponse.ok().bodyValue(response));
    }

    public Mono<ServerResponse> deletePromotionById(ServerRequest request) {
        Integer id = Integer.valueOf(request.pathVariable("id"));
        return promotionService.deletePromotionById(id)
                .flatMap(response -> ServerResponse.ok().bodyValue(response));
    }

    // Error handler method
    private Mono<ServerResponse> errorHandler(Throwable error) {
        if (error instanceof WebClientResponseException errorResponse) {
            if (errorResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
                Map<String, Object> body = new HashMap<>();
                body.put("error", "Promotions not found: " + errorResponse.getMessage());
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
    }
}
