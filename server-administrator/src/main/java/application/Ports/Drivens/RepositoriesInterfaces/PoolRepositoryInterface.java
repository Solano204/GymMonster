package application.Ports.Drivens.RepositoriesInterfaces;
import infraestrucutre.Adapters.Drivens.DTOS.DtoPoolSent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PoolRepositoryInterface {

    // Get all pools
    Flux<DtoPoolSent> getAllPools();

    // Create a new pool
    Mono<DtoPoolSent> createPool(DtoPoolSent pool);

    // Update a pool by name
    Mono<String> updatePool(String name, DtoPoolSent updatedPool);

    // Delete a pool by name
    Mono<String> deletePoolByName(String name);
}
