package application.Ports.Drivens.InterfaceRepositories;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;

import infraestrucutre.Adapters.Drivens.DTOS.DtoSpecialtyRecived;
import infraestrucutre.Adapters.Drivens.DTOS.DtoTrainerData;

public interface PerTrainerRepositoryInteface {
    Flux<DtoTrainerData> getAllTrainers(int page, int size);
    Mono<List<DtoSpecialtyRecived>> getAllSpecialtiesFromTrainer(String username);
}
