package application.Ports.Drivens.RepositoriesInterfaces;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.LocalDate;

import infraestrucutre.Adapters.Drivens.Entities.Promotion;


public interface PromotionRepositoryInterface {

    // Get all promotions
    Flux<Promotion> getAllPromotions();

    // Get promotions by current date
    Flux<Promotion> getPromotionsByCurrentDate(LocalDate currentDate);

    // Get promotions by specific start date
    Flux<Promotion> getPromotionsByStartDate(LocalDate date);

    // Get promotions by specific end date
    Flux<Promotion> getPromotionsByEndDate(LocalDate date);

    // Create a new promotion
    Mono<Promotion> createPromotion(Promotion promotion);

    // Update promotion by ID
    Mono<String> updatePromotion(Integer id, Promotion updatedPromotion);

    // Delete a promotion by ID
    Mono<String> deletePromotionById(Integer id);
}
