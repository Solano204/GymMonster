package application.Ports.Drivens.InterfaceRepositories;

import infraestrucutre.Adapters.Drivens.DTOS.DtoSpecialtyRecived;
import reactor.core.publisher.Flux;

public  interface SpecialtyRepositoryInterface {

    public Flux<DtoSpecialtyRecived> getAllSpecialties();
}
