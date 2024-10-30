package application.Ports.Drivens.RepositoriesInterfaces;

import infraestrucutre.Adapters.Drivens.DTOS.DtoDetailUserReciving;
import infraestrucutre.Adapters.Drivens.Entities.Schedule;
import infraestrucutre.Adapters.Drivens.Entities.WorkClass;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface WorkClassRepositoryInterface {

    Flux<WorkClass> getAllWorkClasses();

    Mono<WorkClass> createWorkclass(WorkClass workClass);

    Mono<WorkClass> updateWorkClass(WorkClass workClass, String name);

    Mono<String> deleteWorkClass(String name);

    Flux<DtoDetailUserReciving> getClientsByWorkClassWithPagination(String className, int page, int size);

    Flux<DtoDetailUserReciving> getTrainersByWorkClassWithPagination(String name, int page, int size);

    Flux<Schedule> getCSchedulesByWorkClassWithPagination(String className);
}