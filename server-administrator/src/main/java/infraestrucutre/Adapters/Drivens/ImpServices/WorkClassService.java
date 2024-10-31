package infraestrucutre.Adapters.Drivens.ImpServices;

import org.springframework.stereotype.Service;

import application.Ports.Drivens.RepositoriesInterfaces.WorkClassRepositoryInterface;
import application.Ports.Drivers.IServices.WorkClassInterface;
import infraestrucutre.Adapters.Drivens.DTOS.DtoDetailUserReciving;
import infraestrucutre.Adapters.Drivens.Entities.Schedule;
import infraestrucutre.Adapters.Drivens.Entities.WorkClass;
import lombok.AllArgsConstructor;
import lombok.Data;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@AllArgsConstructor
@Data
public class WorkClassService implements WorkClassInterface {

    private final WorkClassRepositoryInterface workClassRepository;
    @Override
    public Flux<WorkClass> getAllWorkClasses() {
        return workClassRepository.getAllWorkClasses();
    }


    @Override
    public Mono<WorkClass> updateWorkClass(WorkClass workClass, String name) {
        return workClassRepository.updateWorkClass(workClass,name);
    }

    @Override
    public Mono<String> deleteWorkClass(String name) {
        return workClassRepository.deleteWorkClass(name);
    }



@Override
public Flux<DtoDetailUserReciving> getClientsByWorkClassWithPagination(String name, int page, int size) {

    // If not cached, fetch clients from the database
    return workClassRepository.getClientsByWorkClassWithPagination(name, page, size);
}


@Override
public Flux<DtoDetailUserReciving> getTrainersByWorkClassWithPagination(String name, int page, int size) {
    
    // If not cached, fetch trainers from the database
    return workClassRepository.getTrainersByWorkClassWithPagination(name, page, size);
}

@Override
public Mono<WorkClass> createWorkclass(WorkClass workClass) {
    return workClassRepository.createWorkclass(workClass);
}

@Override
public Flux<Schedule> getCSchedulesByWorkClassWithPagination(String className) {
    return workClassRepository.getCSchedulesByWorkClassWithPagination(className);

}


    
}
