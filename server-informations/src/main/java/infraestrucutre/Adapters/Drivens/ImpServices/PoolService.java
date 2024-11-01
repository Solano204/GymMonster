                        package infraestrucutre.Adapters.Drivens.ImpServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import application.Ports.Drivers.IServices.PoolServiceInterface;
import infraestrucutre.Adapters.Drivens.Entities.Pool;
import infraestrucutre.Adapters.Drivens.Entities.Promotion;
import infraestrucutre.Adapters.Drivens.Repositories.PoolRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Data
@AllArgsConstructor
@Service
public class PoolService implements PoolServiceInterface {

    private final PoolRepository poolRepository;

    @Override
    public Mono<Pool> createPool(Pool pool) {
        return poolRepository.save(pool);
    }

    @Override
    public Flux<Pool> getAllPools() {
        return poolRepository.findAll();
    }

    @Override
    public Mono<Pool> getPoolByName(String name) {
        return poolRepository.findByName(name);
    }

    @Override
    public Mono<Void> deletePoolByName(String name) {
        return poolRepository.deleteByName(name);
    }

    @Override
    public Mono<Pool> updatePoolById(Long id, Pool pool) {
        return poolRepository.findById(id)
                .flatMap(existingPool -> {
                    existingPool.setDescription(pool.getDescription());
                    existingPool.setStartDate(pool.getStartDate());
                    existingPool.setDateClean(pool.getDateClean());
                    existingPool.setName(pool.getName());
                    existingPool.setEndDate(pool.getEndDate());
                    return poolRepository.save(existingPool);
                });
    }
}
