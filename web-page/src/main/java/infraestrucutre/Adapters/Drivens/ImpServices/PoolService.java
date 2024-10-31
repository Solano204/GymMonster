package infraestrucutre.Adapters.Drivens.ImpServices;

import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import application.Ports.Drivens.InterfaceRepositories.PoolRepositoryInterface;
import application.Ports.Drivers.IServices.PoolServiceInterface;
import infraestrucutre.Adapters.Drivens.DTOS.DtoPoolReciving;
import infraestrucutre.Adapters.Drivens.Repositories.PoolRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
@AllArgsConstructor
@Data
public class PoolService implements PoolServiceInterface {

    private final PoolRepositoryInterface poolRepository;
    private final RedissonClient redissonClient;

    @Override
    public Flux<DtoPoolReciving> getAllPools() {
        String cacheKey = "All:Pools";

        RBucket<List<DtoPoolReciving>> bucket = redissonClient.getBucket(cacheKey);
        List<DtoPoolReciving> cachedPools = bucket.get();
        if (cachedPools != null) {
            System.out.println("Retrieved pools from Redis cache.");
            return Flux.fromIterable(cachedPools);
        }

        return poolRepository.getAllPools()
                .collectList()
                .flatMapMany(pools -> {
                    bucket.set(pools, java.time.Duration.ofHours(1));
                    return Flux.fromIterable(pools);
                });
    }

}
