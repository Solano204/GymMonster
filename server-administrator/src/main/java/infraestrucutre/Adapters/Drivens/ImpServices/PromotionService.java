package infraestrucutre.Adapters.Drivens.ImpServices;

import org.springframework.stereotype.Service;

import application.Ports.Drivens.RepositoriesInterfaces.PromotionRepositoryInterface;
import application.Ports.Drivers.IServices.PromotionInterface;
import infraestrucutre.Adapters.Drivens.Entities.Promotion;
import lombok.AllArgsConstructor;
import lombok.Data;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class PromotionService implements PromotionInterface {

    private final PromotionRepositoryInterface promotionRepository;

    // Fetch all promotions

    @Override
    public Flux<Promotion> getAllPromotions() {
        return promotionRepository.getAllPromotions();
    }

    // Get promotions by current date
    @Override
    public Flux<Promotion> getPromotionsByCurrentDate(LocalDate currentDate) {
        return promotionRepository.getPromotionsByCurrentDate(currentDate);
    }

    // Get promotions by start date
    @Override
    public Flux<Promotion> getPromotionsByStartDate(LocalDate date) {
        return promotionRepository.getPromotionsByStartDate(date);
    }

    // Get promotions by end date
    @Override
    public Flux<Promotion> getPromotionsByEndDate(LocalDate date) {
        return promotionRepository.getPromotionsByEndDate(date);
    }

    // Create a new promotion
    @Override
    public Mono<Promotion> createPromotion(Promotion promotion) {
        return promotionRepository.createPromotion(promotion);
    }

    // Update promotion by ID
    @Override
    public Mono<String> updatePromotion(Integer id, Promotion updatedPromotion) {
        return promotionRepository.updatePromotion(id, updatedPromotion);
    }

    // Delete a promotion by ID
    @Override
    public Mono<String> deletePromotionById(Integer id) {
        return promotionRepository.deletePromotionById(id);
    }
}