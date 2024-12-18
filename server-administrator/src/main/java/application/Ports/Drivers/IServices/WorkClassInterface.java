package application.Ports.Drivers.IServices;


import infraestrucutre.Adapters.Drivens.DTOS.DtoDetailUserReciving;
import infraestrucutre.Adapters.Drivens.Entities.Schedule;
import infraestrucutre.Adapters.Drivens.Entities.WorkClass;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface WorkClassInterface {

    Flux<WorkClass> getAllWorkClasses();

    Mono<WorkClass> updateWorkClass(WorkClass workClass, String name);

    Mono<String> deleteWorkClass(String name);

    Flux<DtoDetailUserReciving> getClientsByWorkClassWithPagination(String name, int page, int size);

    Flux<DtoDetailUserReciving> getTrainersByWorkClassWithPagination(String name, int page, int size);

    Mono<WorkClass> createWorkclass(WorkClass workClass);

    Flux<Schedule> getCSchedulesByWorkClassWithPagination(String className);
}
