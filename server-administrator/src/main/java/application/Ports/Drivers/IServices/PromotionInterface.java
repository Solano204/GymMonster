package application.Ports.Drivers.IServices;

import infraestrucutre.Adapters.Drivens.DTOS.DtoSpecialtyRecived;
import infraestrucutre.Adapters.Drivens.Entities.Promotion;
import infraestrucutre.Adapters.Drivens.Entities.Schedule;
import infraestrucutre.Adapters.Drivens.Entities.WorkClass;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.LocalDate;

public interface PromotionInterface {
    Flux<Promotion> getAllPromotions();
    Flux<Promotion> getPromotionsByCurrentDate(LocalDate currentDate);
    Flux<Promotion> getPromotionsByStartDate(LocalDate date);
    Flux<Promotion> getPromotionsByEndDate(LocalDate date);
    Mono<Promotion> createPromotion(Promotion promotion);
    Mono<String> updatePromotion(Integer id, Promotion updatedPromotion);
    Mono<String> deletePromotionById(Integer id);
}
