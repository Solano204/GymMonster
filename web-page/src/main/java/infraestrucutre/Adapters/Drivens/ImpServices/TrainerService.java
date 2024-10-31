package infraestrucutre.Adapters.Drivens.ImpServices;

import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import application.Ports.Drivens.InterfaceRepositories.PerTrainerRepositoryInteface;
import application.Ports.Drivers.IServices.TrainerInterface;
import infraestrucutre.Adapters.Drivens.DTOS.DtoSpecialtyRecived;
import infraestrucutre.Adapters.Drivens.DTOS.DtoTrainerData;
import infraestrucutre.Adapters.Drivens.DTOS.DtoTrainerReciving;
import infraestrucutre.Adapters.Drivens.Repositories.PerTrainerRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@AllArgsConstructor
@Data
public class TrainerService implements TrainerInterface {

    private final PerTrainerRepositoryInteface trainerRepository;
    private final RedissonClient redissonClient;

    @Override
    public Flux<DtoTrainerData> getAllPerTrainers(int page, int size) {
        String redisKey = "AllTrainers:page:" + page + ":size:" + size; // Cache key for the trainer list (depending on page and size)
        
        RBucket<List<DtoTrainerData>> bucket = redissonClient.getBucket(redisKey); // Access Redis cache bucket

        // Check if trainers are cached
        List<DtoTrainerData> cachedTrainers = bucket.get();
        if (cachedTrainers != null && !cachedTrainers.isEmpty()) {
            System.out.println("Retrieved trainers from Redis cache.");
            return Flux.fromIterable(cachedTrainers); // 
        }

        // If not cached, fetch trainers from the database
        return trainerRepository.getAllTrainers(page, size)
                .collectList() //Flux<DttrainerReciving> ->  Mono<List<DtoTrainerReciving>> Here Im converting Flux to Mono<List<DtoTrainerReciving>> because I need to extract the List<DtoTrainerReciving> in saved to Redis cache like LIST 
                .flatMapMany(trainers -> { // Mono<List<DtoTrainerReciving>> -> Flux<List<DtoTrainerReciving>>
                    // Cache the trainers in Redis with an expiration time (LIST)
                    bucket.set(trainers, java.time.Duration.ofMinutes(1)); // Cache for 10 minutes
                    System.out.println("Cached trainers in Redis.");
                    return Flux.fromIterable(trainers); // List<DtoTrainerReciving> -> Flux<DtoTrainerReciving>
                });
    }

    @Override
    public Mono<List<DtoSpecialtyRecived>> getAllSpecialtiesFromTrainer(String username) {
        return trainerRepository.getAllSpecialtiesFromTrainer(username);
    }

}
