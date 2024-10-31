package application.Ports.Drivers.IServices;

import infraestrucutre.Adapters.Drivens.Entities.Specialty;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SpecialtyServiceInterface {

    Mono<Specialty> createSpecialty(Specialty specialty);

    Flux<Specialty> getAllSpecialties();

    Mono<Specialty> getSpecialtyById(Integer id);

    Mono<Specialty> getSpecialtyByName(String name);

    Mono<Specialty> updateSpecialty(Integer id, Specialty specialty);
    Mono<Specialty> updateSpecialtyName(String name, Specialty specialty);

    Mono<Void> deleteSpecialty(Integer id);

    Mono<Void> deleteSpecialtyByName(String name);
}
