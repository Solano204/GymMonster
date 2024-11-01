package application.Ports.Drivers.IServices;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

import infraestrucutre.Adapters.Drivens.Entities.Promotion;

public interface PromotionServiceInterface {

    Mono<Promotion> createPromotion(Promotion promotion);

    Flux<Promotion> getAllPromotions();

    Flux<Promotion> getPromotionByStartDate(LocalDate startDate);

    Flux<Promotion> getPromotionByEndDate(LocalDate endDate);

    Mono<Void> deletePromotionByStartDate(LocalDate endDate);

    Mono<Void> deletePromotionByEndDate(LocalDate endDate);

    Mono<Promotion> updatePromotion(Integer id, Promotion promotion);

    Mono<Void> deletePromotion(Integer id);
}
