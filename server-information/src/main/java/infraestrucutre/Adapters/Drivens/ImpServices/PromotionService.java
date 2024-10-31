package infraestrucutre.Adapters.Drivens.ImpServices;
import java.time.LocalDate;

import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import application.Ports.Drivers.IServices.PromotionServiceInterface;
import infraestrucutre.Adapters.Drivens.Entities.Membership;
import infraestrucutre.Adapters.Drivens.Entities.Promotion;
import infraestrucutre.Adapters.Drivens.Repositories.MembershipRepository;
import infraestrucutre.Adapters.Drivens.Repositories.PromotionRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@AllArgsConstructor
@Service
public class PromotionService implements PromotionServiceInterface {

    private final PromotionRepository promotionRepository;

    @Override
    public Mono<Promotion> createPromotion(Promotion promotion) {
        return promotionRepository.save(promotion);
    }

    @Override
    public Flux<Promotion> getAllPromotions() {
        return promotionRepository.findAll();
    }

    @Override
    public Flux<Promotion> getPromotionByStartDate(LocalDate startDate) {
        return promotionRepository.findByStartDate(startDate);
    }

    @Override
    public Flux<Promotion> getPromotionByEndDate(LocalDate endDate) {
        return promotionRepository.findByEndDate(endDate);
    }

    @Override
    public Mono<Void> deletePromotionByStartDate(LocalDate endDate) {
        return promotionRepository.deleteByStartDate(endDate);
    }

    @Override
    public Mono<Void> deletePromotionByEndDate(LocalDate endDate) {
        return promotionRepository.deleteByEndDate(endDate);
    }

    @Override
    public Mono<Promotion> updatePromotion(Integer id, Promotion promotion) {
        return promotionRepository.findById(id)
                .flatMap(existingPromotion -> {
                    existingPromotion.setDescription(promotion.getDescription());
                    existingPromotion.setDuration(promotion.getDuration());
                    existingPromotion.setPercentageDiscount(promotion.getPercentageDiscount());
                    existingPromotion.setStartDate(promotion.getStartDate());
                    existingPromotion.setEndDate(promotion.getEndDate());
                    existingPromotion.setActive(promotion.isActive());
                    return promotionRepository.save(existingPromotion);
                });
    }

    @Override
    public Mono<Void> deletePromotion(Integer id) {
        return promotionRepository.deleteById(id);
    }
}
