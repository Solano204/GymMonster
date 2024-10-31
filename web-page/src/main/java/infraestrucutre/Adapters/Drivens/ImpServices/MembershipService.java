package infraestrucutre.Adapters.Drivens.ImpServices;

import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import application.Ports.Drivens.InterfaceRepositories.MembershipRepositoryInterface;
import application.Ports.Drivers.IServices.MembershipInterface;
import infraestrucutre.Adapters.Drivens.DTOS.DtoMembershipReciving;
import infraestrucutre.Adapters.Drivens.Repositories.MembershipRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import reactor.core.publisher.Flux;
import java.util.List;

@Data
@AllArgsConstructor
@Service
public class MembershipService implements MembershipInterface {

    private final MembershipRepositoryInterface membershipRepository;
    private final RedissonClient redissonClient;

    @Override
    public Flux<DtoMembershipReciving> getAllMemberships() {
        // Define the Redis cache key
        String cacheKey = "All:Memberships";

        RBucket<List<DtoMembershipReciving>> bucket = redissonClient.getBucket(cacheKey);

        List<DtoMembershipReciving> cachedMemberships = bucket.get();
        if (cachedMemberships != null) {
            return Flux.fromIterable(cachedMemberships);
        }

        return membershipRepository.getAllMemberships()
                .collectList()
                .flatMapMany(memberships -> {
                    bucket.set(memberships, java.time.Duration.ofMinutes(3)); // Set TTL to 1 hour

                    // Return the original result as a Flux
                    return Flux.fromIterable(memberships);
                });
    }

}
