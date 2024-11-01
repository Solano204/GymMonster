package infraestrucutre.Adapters.Drivens.Repositories;

import java.time.LocalDate;

import org.springframework.cglib.core.Local;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import infraestrucutre.Adapters.Drivens.Entities.Promotion;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface PromotionRepository extends ReactiveCrudRepository<Promotion, Integer> {
    // You can add additional query methods if needed
    

    Flux<Promotion> findByStartDate(LocalDate startDate);
    Mono<Void> deleteByStartDate(LocalDate startDate);
    Flux<Promotion> findByEndDate(LocalDate endDate);
    Mono<Void> deleteByEndDate(LocalDate endDate);
}
