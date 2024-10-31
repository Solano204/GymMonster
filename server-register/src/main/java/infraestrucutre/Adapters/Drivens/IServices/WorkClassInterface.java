package infraestrucutre.Adapters.Drivens.IServices;

import infraestrucutre.Adapters.Drivens.Entities.WorkClass;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface WorkClassInterface {
    
    // METHOD WORK CLASSES

    Flux<WorkClass> getAllWorkClasses();

    // Mono<WorkClass> getWorkClassByName(String namme);


    Flux<WorkClass> getClientsByWorkClassWithPagination(String name, int page, int size);
    Flux<WorkClass> getTrainersByWorkClassWithPagination(String name, int page, int size);

}
    
