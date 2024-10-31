package application.Ports.Drivens.InterfaceRepositories;

import infraestrucutre.Adapters.Drivens.Entities.Promotion;
import reactor.core.publisher.Flux;

public interface PromotionRepositoryInterface    {
    Flux<Promotion> getPromotionsForCurrentDate(String currentDate);
    Flux<Promotion> getPromotionsForDate(String date);
}
