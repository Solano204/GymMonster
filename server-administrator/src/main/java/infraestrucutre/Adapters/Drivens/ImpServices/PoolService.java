package infraestrucutre.Adapters.Drivens.ImpServices;
import org.springframework.stereotype.Service;

import application.Ports.Drivens.RepositoriesInterfaces.PoolRepositoryInterface;
import application.Ports.Drivers.IServices.PoolServiceInterface;
import infraestrucutre.Adapters.Drivens.DTOS.DtoPoolSent;
import infraestrucutre.Adapters.Drivens.Repositories.PoolRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import reactor.core.publisher.Flux;

import java.util.List;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PoolService implements PoolServiceInterface {

    private final PoolRepositoryInterface poolRepository; // Inject your repository here

    public PoolService(PoolRepository poolRepository) {
        this.poolRepository = poolRepository;
    }

    @Override
    public Flux<DtoPoolSent> getAllPools() {
        return poolRepository.getAllPools(); // Assuming this method exists in your repository
    }

    @Override
    public Mono<DtoPoolSent> createPool(DtoPoolSent pool) {
        return poolRepository.createPool(pool); // Implement save in your repository
    }

    @Override
    public Mono<String> updatePool(String name, DtoPoolSent updatedPool) {
        return poolRepository.updatePool(name, updatedPool); // Implement update in your repository
    }

    @Override
    public Mono<String> deletePoolByName(String name) {
        return poolRepository.deletePoolByName(name); // Implement delete in your repository
    }
}
