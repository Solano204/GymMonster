package infraestrucutre.Adapters.Drivens.ImpServices;

import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import application.Ports.Drivens.InterfaceRepositories.WorkClassRepositoryInterface;
import application.Ports.Drivers.IServices.WorkClassInterface;
import infraestrucutre.Adapters.Drivens.DTOS.DtoDetailUserReciving;
import infraestrucutre.Adapters.Drivens.Entities.Schedule;
import infraestrucutre.Adapters.Drivens.Entities.WorkClass;
import infraestrucutre.Adapters.Drivens.Repositories.WorkClassRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
@AllArgsConstructor
@Data
public class WorkClassService implements WorkClassInterface{

    private final WorkClassRepositoryInterface workClassRepository;
    private final RedissonClient redissonClient;
    @Override
    public Flux<WorkClass> getAllWorkClasses() {
    String cacheKey = "All:Workclasses";

    RBucket<List<WorkClass>> bucket = redissonClient.getBucket(cacheKey);

    // Check if data is cached in Redis
    List<WorkClass> cachedWorkClasses = bucket.get();
    if (cachedWorkClasses != null) {
        // Return the cached work classes as a Flux
        return Flux.fromIterable(cachedWorkClasses);
    }

    // If not cached, fetch work classes from the database
    return workClassRepository.getAllWorkClasses()
        .collectList()
        .flatMapMany(workClasses -> {
            // Cache the result in Redis with a TTL (e.g., 24 hours)
            bucket.set(workClasses, java.time.Duration.ofHours(24));
            return Flux.fromIterable(workClasses);
        });
}


@Override
public Flux<Schedule> getWorkClassSchedules(String name) {
    String cacheKey = "AllSchedules:ByWorkclass:" + name;

    RBucket<List<Schedule>> bucket = redissonClient.getBucket(cacheKey);

    // Check if data is cached in Redis
    List<Schedule> cachedSchedules = bucket.get();
    if (cachedSchedules != null) {
        System.out.println("Retrieved schedules from Redis cache.");
        // Return the cached schedules as a Flux
        return Flux.fromIterable(cachedSchedules);
    }

    // If not cached, fetch schedules from the database
    return workClassRepository.getCSchedulesByWorkClassWithPagination(name)
        .collectList()
        .flatMapMany(schedules -> {
            // Cache the result in Redis with a TTL (e.g., 6 hours)
            bucket.set(schedules, java.time.Duration.ofHours(6));
            return Flux.fromIterable(schedules);
        });
}


@Override
public Flux<DtoDetailUserReciving> getClientsByWorkClassWithPagination(String name, int page, int size) {
    String cacheKey = "AllClients:ByWorkclass:" + name + ":page:" + page + ":size:" + size;

    RBucket<List<DtoDetailUserReciving>> bucket = redissonClient.getBucket(cacheKey);

    // Check if data is cached in Redis
    List<DtoDetailUserReciving> cachedClients = bucket.get();
    if (cachedClients != null) {
        // Return the cached clients as a Flux
        return Flux.fromIterable(cachedClients);
    }

    // If not cached, fetch clients from the database
    return workClassRepository.getClientsByWorkClassWithPagination(name, page, size)
        .collectList()
        .flatMapMany(clients -> {
            // Cache the result in Redis with a TTL (e.g., 1 hour)
            bucket.set(clients, java.time.Duration.ofHours(1));
            return Flux.fromIterable(clients);
        });
}


@Override
public Flux<DtoDetailUserReciving> getTrainersByWorkClassWithPagination(String name, int page, int size) {
    String cacheKey = "AllTrainers:ByWorkclass:" + name + ":page:" + page + ":size:" + size;

    RBucket<List<DtoDetailUserReciving>> bucket = redissonClient.getBucket(cacheKey);

    // Check if data is cached in Redis
    List<DtoDetailUserReciving> cachedTrainers = bucket.get();
    if (cachedTrainers != null) {
        // Return the cached trainers as a Flux
        return Flux.fromIterable(cachedTrainers);
    }

    // If not cached, fetch trainers from the database
    return workClassRepository.getTrainersByWorkClassWithPagination(name, page, size)
        .collectList()
        .flatMapMany(trainers -> {
            // Cache the result in Redis with a TTL (e.g., 1 hour)
            bucket.set(trainers, java.time.Duration.ofHours(1));
            return Flux.fromIterable(trainers);
        });
}


    
}
