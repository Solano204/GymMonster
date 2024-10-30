package infraestrucutre.Adapters.Drivens.Repositories;

import java.util.Collections;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;

import application.Ports.Drivens.InterfaceRepositories.PromotionRepositoryInterface;
import infraestrucutre.Adapters.Drivens.Entities.Promotion;
import lombok.*;
import reactor.core.publisher.Flux;

@Repository
@AllArgsConstructor
@Data
public class PromotionRepository implements PromotionRepositoryInterface{

    private final WebClient.Builder webClientBuilder;


    @Override
    public Flux<Promotion> getPromotionsForCurrentDate(String currentDate) {
        return this.webClientBuilder.build()
                .get()
                // .uri("lb://promotion-service/api/promotions/CurrentDate")
                .uri("http://localhost:8111/api/promotions/CurrentDate/{currentDate}", Collections.singletonMap("currentDate", currentDate))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(Promotion.class);
    }

    @Override
    public Flux<Promotion> getPromotionsForDate(String date) {
        return this.webClientBuilder.build()
                .get()
                // .uri("lb://promotion-service/api/promotions/{date}",
                // Collections.singletonMap("date", date))
                .uri("http://localhost:8111/api/promotions/startDate/{startDate}", Collections.singletonMap("startDate", date))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(Promotion.class);
    }

}
