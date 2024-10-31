package application.Ports.Drivers.IServices;

import infraestrucutre.Adapters.Drivens.Entities.Pool;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PoolServiceInterface {

    Mono<Pool> createPool(Pool pool);

    Flux<Pool> getAllPools();

    Mono<Pool> getPoolByName(String name);

    Mono<Void> deletePoolByName(String name);

    Mono<Pool> updatePoolById(Long id, Pool pool);
}
