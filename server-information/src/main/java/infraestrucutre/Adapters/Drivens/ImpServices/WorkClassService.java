package infraestrucutre.Adapters.Drivens.ImpServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import application.Ports.Drivers.IServices.WorkClassServiceInterface;
import infraestrucutre.Adapters.Drivens.Entities.DetailUser;
import infraestrucutre.Adapters.Drivens.Entities.Pool;
import infraestrucutre.Adapters.Drivens.Entities.Schedule;
import infraestrucutre.Adapters.Drivens.Entities.WorkClass;
import infraestrucutre.Adapters.Drivens.Repositories.PoolRepository;
import infraestrucutre.Adapters.Drivens.Repositories.ScheduleRepository;
import infraestrucutre.Adapters.Drivens.Repositories.WorkClassRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Service
public class WorkClassService implements WorkClassServiceInterface {

    private final WorkClassRepository workClassRepository;
    private final ScheduleRepository scheduleRepository;

    @Override
    public Mono<WorkClass> createWorkClass(WorkClass workClass) {
        return workClassRepository.save(workClass);
    }

    @Override
    public Flux<WorkClass> getAllWorkClasses() {
        return workClassRepository.findAll();
    }

    @Override
    public Mono<WorkClass> getWorkClassById(Long id) {
        return workClassRepository.findById(id);
    }

    @Override
    public Mono<WorkClass> getWorkClassByName(String name) {
        return workClassRepository.findByName(name);
    }

    @Override
    public Mono<WorkClass> updateWorkClass(Long id, WorkClass workClass) {
        workClass.setId(id); // Ensure the id is set for the update
        return workClassRepository.save(workClass);
    }


    @Override
    public Mono<Void> deleteWorkClass(Long id) {
        return workClassRepository.deleteById(id);
    }

    @Override
    public Flux<Schedule> getWorkClassSchedules(String name) {
        return workClassRepository.findByName(name).flatMapMany( workClass -> scheduleRepository.findSchedulesByWorkClassId(workClass.getId()));
    }

    @Override
    public Flux<DetailUser> getClientsByWorkClassWithPagination(String name, int page, int size) {
        int offset = page * size;
        return workClassRepository.findByName(name)
                .flatMapMany(workClass -> workClassRepository.findAllClientsByWorkClass(workClass.getId(), size, offset));
    }

    @Override
    public Flux<DetailUser> getTrainersByWorkClassWithPagination(String name, int page, int size) {
        int offset = page * size;
        return workClassRepository.findByName(name)
                .flatMapMany(workClass -> workClassRepository.findAllTrainersByWorkClass(workClass.getId(), size, offset));
    }
}