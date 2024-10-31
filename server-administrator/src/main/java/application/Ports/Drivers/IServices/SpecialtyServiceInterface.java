package application.Ports.Drivers.IServices;

import org.springframework.stereotype.Service;

import infraestrucutre.Adapters.Drivens.DTOS.DtoSpecialtyRecived;
import infraestrucutre.Adapters.Drivens.Repositories.SpecialtyRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import reactor.core.publisher.Flux;

import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SpecialtyServiceInterface {
    Flux<DtoSpecialtyRecived> getAllSpecialties();
    Flux<DtoSpecialtyRecived> getAllTrainers();
    Mono<DtoSpecialtyRecived> createSpecialty(DtoSpecialtyRecived specialty);
    Mono<DtoSpecialtyRecived> getSpecialtyByName(String name);
    Mono<String> updateSpecialty(String name, DtoSpecialtyRecived updatedSpecialty);
    Mono<String> deleteSpecialty(String name);
}

