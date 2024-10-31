package application.Ports.Drivers.IServices;

import infraestrucutre.Adapters.Drivens.DTOS.DtoPoolReciving;
import reactor.core.publisher.Flux;

public interface PoolServiceInterface {
    Flux<DtoPoolReciving> getAllPools();
}
