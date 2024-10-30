package application.Ports.Drivers.IServices;

import infraestrucutre.Adapters.Drivens.DTOS.DtoSpecialtyRecived;
import infraestrucutre.Adapters.Drivens.Entities.Promotion;
import infraestrucutre.Adapters.Drivens.Entities.Schedule;
import infraestrucutre.Adapters.Drivens.Entities.WorkClass;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PromotionInterface {

    // METHOD PROMOTION
    Flux<Promotion> getAllCurrentPromotions(String currentDate);
    Flux<Promotion> getAllPromotionsByDate(String date);

   
    // Mono<DtoSpecialtyRecived> getSpecialtyByName(String name);


}
