package application.Ports.Drivers.IServices;

import infraestrucutre.Adapters.Drivens.DTOS.DtoPoolSent;
import reactor.core.publisher.Flux;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PoolServiceInterface {
    Flux<DtoPoolSent> getAllPools();
    Mono<DtoPoolSent> createPool(DtoPoolSent pool);
    Mono<String> updatePool(String name, DtoPoolSent updatedPool);
    Mono<String> deletePoolByName(String name);
}
