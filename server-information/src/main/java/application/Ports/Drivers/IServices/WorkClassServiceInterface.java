package application.Ports.Drivers.IServices;

import infraestrucutre.Adapters.Drivens.Entities.DetailUser;
import infraestrucutre.Adapters.Drivens.Entities.Schedule;
import infraestrucutre.Adapters.Drivens.Entities.WorkClass;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface WorkClassServiceInterface {

    Mono<WorkClass> createWorkClass(WorkClass workClass);

    Flux<WorkClass> getAllWorkClasses();

    Mono<WorkClass> getWorkClassById(Long id);
    Mono<WorkClass> getWorkClassByName(String namme);

    Mono<WorkClass> updateWorkClass(Long id, WorkClass workClass);

    Mono<Void> deleteWorkClass(Long id);

    Flux<Schedule> getWorkClassSchedules(String name);

    Flux<DetailUser> getClientsByWorkClassWithPagination(String name, int page, int size);

    Flux<DetailUser> getTrainersByWorkClassWithPagination(String name, int page, int size);
}
