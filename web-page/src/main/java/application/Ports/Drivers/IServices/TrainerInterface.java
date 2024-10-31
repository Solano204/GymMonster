package application.Ports.Drivers.IServices;

import java.util.List;

import infraestrucutre.Adapters.Drivens.DTOS.DtoSpecialtyRecived;
import infraestrucutre.Adapters.Drivens.DTOS.DtoTrainerData;
import infraestrucutre.Adapters.Drivens.DTOS.DtoTrainerReciving;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TrainerInterface {
      /// METHOD TRAINERS
    Flux<DtoTrainerData> getAllPerTrainers(int page, int size);

    /// Mono<DetailUser> getInfoTrainer(String username);

    Mono<List<DtoSpecialtyRecived>> getAllSpecialtiesFromTrainer(String username);

}
