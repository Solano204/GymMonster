package infraestrucutre.Adapters.Drivens.ImpServices;

import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import application.Ports.Drivens.InterfaceRepositories.SpecialtyRepositoryInterface;
import application.Ports.Drivers.IServices.SpecialtyInterface;
import infraestrucutre.Adapters.Drivens.DTOS.DtoSpecialtyRecived;
import infraestrucutre.Adapters.Drivens.Repositories.SpecialtyRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
@AllArgsConstructor
@Data
public class SpecialtyService implements SpecialtyInterface{

    private final SpecialtyRepositoryInterface specialtyRepository;
        private final RedissonClient redissonClient;

    @Override
public Flux<DtoSpecialtyRecived> getAllSpecialties() {
    String cacheKey = "All:Specialties";

    RBucket<List<DtoSpecialtyRecived>> bucket = redissonClient.getBucket(cacheKey);

    // Check if data is cached in Redis
    List<DtoSpecialtyRecived> cachedSpecialties = bucket.get();
    if (cachedSpecialties != null) {
        System.out.println("Retrieved specialties from Redis cache.");
        // Return the cached specialties as a Flux
        return Flux.fromIterable(cachedSpecialties);
    }

    // If not cached, fetch specialties from the database
    return specialtyRepository.getAllSpecialties()
        .collectList()
        .flatMapMany(specialties -> {
            // Cache the result in Redis with a TTL (e.g., 24 hours)
            bucket.set(specialties, java.time.Duration.ofHours(24));
            return Flux.fromIterable(specialties);
        });
}

    
}
