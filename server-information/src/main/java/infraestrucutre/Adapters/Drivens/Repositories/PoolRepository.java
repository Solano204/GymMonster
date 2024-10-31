package infraestrucutre.Adapters.Drivens.Repositories;
import infraestrucutre.Adapters.Drivens.Entities.Pool;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PoolRepository extends ReactiveCrudRepository<Pool, Integer> {
    // You can define custom query methods, e.g., finding pools by name.
    Mono<Pool> findByName(String name);
    Mono<Pool> findById(Long id);

    Mono<Void> deleteByName(String name);
}