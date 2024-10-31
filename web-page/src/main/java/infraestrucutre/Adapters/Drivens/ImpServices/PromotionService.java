package infraestrucutre.Adapters.Drivens.ImpServices;

import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import application.Ports.Drivers.IServices.PromotionInterface;
import infraestrucutre.Adapters.Drivens.Entities.Promotion;
import infraestrucutre.Adapters.Drivens.Repositories.PromotionRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
@AllArgsConstructor
@Data
public class PromotionService implements PromotionInterface {

    private final PromotionRepository promotionRepository;
    private final RedissonClient redissonClient;

    @Override
    public Flux<Promotion> getAllCurrentPromotions(String currentDate) {
        // Define Redis cache key for current promotions
        String cacheKey = "AllPromotions:Current:" + currentDate;

        // Get the Redis bucket for current promotions
        RBucket<List<Promotion>> bucket = redissonClient.getBucket(cacheKey);

        // Check if data is cached in Redis
        List<Promotion> cachedPromotions = bucket.get();
        if (cachedPromotions != null) {
            System.out.println("Retrieved promotions from Redis cache.");
            // Return the cached promotions as a Flux
            return Flux.fromIterable(cachedPromotions);
        }

        // If not cached, fetch promotions from the database
        return promotionRepository.getPromotionsForCurrentDate(currentDate)
                // Collect the Flux into a List to cache it
                .collectList()
                .flatMapMany(promotions -> {
                    // Cache the result in Redis with a TTL (e.g., 1 hour)
                    bucket.set(promotions, java.time.Duration.ofHours(1));

                    // Return the original result as a Flux
                    return Flux.fromIterable(promotions);
                });
    }

    @Override
    public Flux<Promotion> getAllPromotionsByDate(String date) {
        // Define Redis cache key for promotions by date
        String cacheKey = "AllPromotions:ByDate:" + date;

        // Get the Redis bucket for promotions on a specific date
        RBucket<List<Promotion>> bucket = redissonClient.getBucket(cacheKey);

        // Check if data is cached in Redis
        List<Promotion> cachedPromotions = bucket.get();
        if (cachedPromotions != null) {
            // Return the cached promotions as a Flux
            return Flux.fromIterable(cachedPromotions);
        }

        // If not cached, fetch promotions from the database
        return promotionRepository.getPromotionsForDate(date)
                // Collect the Flux into a List to cache it
                .collectList()
                .flatMapMany(promotions -> {
                    // Cache the result in Redis with a TTL (e.g., 1 hour)
                    bucket.set(promotions, java.time.Duration.ofHours(1));

                    // Return the original result as a Flux
                    return Flux.fromIterable(promotions);
                });
    }

}
