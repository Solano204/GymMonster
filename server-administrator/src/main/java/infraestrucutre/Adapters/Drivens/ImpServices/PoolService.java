package infraestrucutre.Adapters.Drivens.ImpServices;
import org.springframework.stereotype.Service;

import application.Ports.Drivens.RepositoriesInterfaces.PoolInformationClientInterface;
import application.Ports.Drivers.IServices.PoolServiceInterface;
import infraestrucutre.Adapters.Drivens.DTOS.DtoPoolSent;
import infraestrucutre.Adapters.Drivens.Repositories.PoolInformationClient;
import lombok.AllArgsConstructor;
import lombok.Data;
import reactor.core.publisher.Flux;

import java.util.List;

import reactor.core.publisher.Mono;

@Service
public class PoolService implements PoolServiceInterface {

    private final PoolInformationClientInterface poolRepository; // Inject your repository here

    public PoolService(PoolInformationClient poolRepository) {
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
