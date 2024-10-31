package application.Ports.Drivens.RepositoriesInterfaces;

import infraestrucutre.Adapters.Drivens.DTOS.DtoSpecialtyRecived;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SpecialtyRepositoryInterface {

    Flux<DtoSpecialtyRecived> getAllSpecialties();

    Flux<DtoSpecialtyRecived> getAllTrainers();

    Mono<DtoSpecialtyRecived> createSpecialty(DtoSpecialtyRecived specialty);

    Mono<DtoSpecialtyRecived> getSpecialtyByName(String name);

    Mono<String> updateSpecialty(String name, DtoSpecialtyRecived updatedSpecialty);

    Mono<String> deleteSpecialty(String name);
}