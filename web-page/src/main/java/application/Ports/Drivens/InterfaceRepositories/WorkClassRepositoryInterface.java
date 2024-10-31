package application.Ports.Drivens.InterfaceRepositories;

import infraestrucutre.Adapters.Drivens.DTOS.DtoDetailUserReciving;
import infraestrucutre.Adapters.Drivens.Entities.Schedule;
import infraestrucutre.Adapters.Drivens.Entities.WorkClass;
import reactor.core.publisher.Flux;

public interface WorkClassRepositoryInterface {
    Flux<WorkClass> getAllWorkClasses();
    Flux<DtoDetailUserReciving> getClientsByWorkClassWithPagination(String className, int page, int size);
    Flux<DtoDetailUserReciving> getTrainersByWorkClassWithPagination(String name, int page, int size);
    Flux<Schedule> getCSchedulesByWorkClassWithPagination(String className);
}