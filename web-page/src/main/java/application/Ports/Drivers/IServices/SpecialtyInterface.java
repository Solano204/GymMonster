package application.Ports.Drivers.IServices;

import infraestrucutre.Adapters.Drivens.DTOS.DtoSpecialtyRecived;
import reactor.core.publisher.Flux;

public interface SpecialtyInterface {
    
    Flux<DtoSpecialtyRecived> getAllSpecialties();
}
