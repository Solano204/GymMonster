package infraestrucutre.Adapters.Drivens.ImpServices;

import org.springframework.stereotype.Service;

import application.Ports.Drivens.RepositoriesInterfaces.SpecialtyRepositoryInterface;
import application.Ports.Drivers.IServices.SpecialtyServiceInterface;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import infraestrucutre.Adapters.Drivens.DTOS.DtoSpecialtyRecived;
import infraestrucutre.Adapters.Drivens.Repositories.SpecialtyRepository;
import lombok.Data;

@Service
@Data
public class SpecialtyService implements SpecialtyServiceInterface {

    private final SpecialtyRepositoryInterface specialtyRepository; // Inject your repository here



    @Override
    public Flux<DtoSpecialtyRecived> getAllSpecialties() {
        return specialtyRepository.getAllSpecialties(); // Call repository method
    }

    @Override
    public Flux<DtoSpecialtyRecived> getAllTrainers() {
        return specialtyRepository.getAllTrainers(); // Call repository method
    }

    @Override
    public Mono<DtoSpecialtyRecived> createSpecialty(DtoSpecialtyRecived specialty) {
        return specialtyRepository.createSpecialty(specialty); // Call repository method
    }

    @Override
    public Mono<DtoSpecialtyRecived> getSpecialtyByName(String name) {
        return specialtyRepository.getSpecialtyByName(name); // Call repository method
    }

    @Override
    public Mono<String> updateSpecialty(String name, DtoSpecialtyRecived updatedSpecialty) {
        return specialtyRepository.updateSpecialty(name, updatedSpecialty); // Call repository method
    }

    @Override
    public Mono<String> deleteSpecialty(String name) {
        return specialtyRepository.deleteSpecialty(name); // Call repository method
    }
}
