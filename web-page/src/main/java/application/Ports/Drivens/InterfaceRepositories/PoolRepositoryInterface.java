package application.Ports.Drivens.InterfaceRepositories;

import infraestrucutre.Adapters.Drivens.DTOS.DtoPoolReciving;
import reactor.core.publisher.Flux;

public interface PoolRepositoryInterface {
    Flux<DtoPoolReciving> getAllPools();
}
