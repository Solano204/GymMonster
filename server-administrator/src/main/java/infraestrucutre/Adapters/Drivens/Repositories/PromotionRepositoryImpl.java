package infraestrucutre.Adapters.Drivens.Repositories;

import java.time.LocalDate;
import java.util.Collections;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;

import application.Ports.Drivens.RepositoriesInterfaces.PromotionRepositoryInterface;
import infraestrucutre.Adapters.Drivens.Entities.Promotion;
import infraestrucutre.Adapters.Drivens.Properties.ServicesUrl;
import lombok.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.MediaType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.LocalDate;

@Repository
@AllArgsConstructor
public class PromotionRepositoryImpl implements PromotionRepositoryInterface {

    private final WebClient.Builder webClientBuilder;
        private final ServicesUrl servicesUrl;


        @Override
        public Flux<Promotion> getAllPromotions() {
            return this.webClientBuilder.build()
                    .get()
                    .uri(servicesUrl.getInfo().getUrl() + "/api/promotions")
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToFlux(Promotion.class);
        }
        
        @Override
        public Flux<Promotion> getPromotionsByCurrentDate(LocalDate currentDate) {
            return this.webClientBuilder.build()
                    .get()
                    .uri(servicesUrl.getInfo().getUrl() + "/api/promotions/currentDate/{currentDate}", currentDate)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToFlux(Promotion.class);
        }
        
        @Override
        public Flux<Promotion> getPromotionsByStartDate(LocalDate date) {
            return this.webClientBuilder.build()
                    .get()
                    .uri(servicesUrl.getInfo().getUrl() + "/api/promotions/startDate/{date}", date)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToFlux(Promotion.class);
        }
        
        @Override
        public Flux<Promotion> getPromotionsByEndDate(LocalDate date) {
            return this.webClientBuilder.build()
                    .get()
                    .uri(servicesUrl.getInfo().getUrl() + "/api/promotions/endDate/{date}", date)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToFlux(Promotion.class);
        }
        
        @Override
        public Mono<Promotion> createPromotion(Promotion promotion) {
            return this.webClientBuilder.build()
                    .post()
                    .uri(servicesUrl.getInfo().getUrl() + "/api/promotions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(promotion)
                    .retrieve()
                    .bodyToMono(Promotion.class);
        }
        
        @Override
        public Mono<String> updatePromotion(Integer id, Promotion updatedPromotion) {
            return this.webClientBuilder.build()
                    .put()
                    .uri(servicesUrl.getInfo().getUrl() + "/api/promotions/{id}", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(updatedPromotion)
                    .retrieve()
                    .bodyToMono(String.class);
        }
        
        @Override
        public Mono<String> deletePromotionById(Integer id) {
            return this.webClientBuilder.build()
                    .delete()
                    .uri(servicesUrl.getInfo().getUrl() + "/api/promotions/{id}", id)
                    .retrieve()
                    .bodyToMono(String.class);
    }
}
