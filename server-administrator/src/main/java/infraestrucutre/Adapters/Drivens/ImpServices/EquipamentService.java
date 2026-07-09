package infraestrucutre.Adapters.Drivens.ImpServices;
import org.springframework.stereotype.Service;

import application.Ports.Drivens.RepositoriesInterfaces.EquipamentInformationClientInterface;
import application.Ports.Drivers.IServices.EquipamentServiceInterface;
import infraestrucutre.Adapters.Drivens.Entities.Equipament;
import infraestrucutre.Adapters.Drivens.Repositories.EquipamentInformationClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class EquipamentService implements EquipamentServiceInterface {

    private final EquipamentInformationClientInterface equipamentRepository;

    public EquipamentService(EquipamentInformationClient equipamentRepository) {
        this.equipamentRepository = equipamentRepository;
    }

    @Override
    public Mono<Equipament> createEquipament(Equipament equipament) {
        return equipamentRepository.createEquipament(equipament);
    }

    @Override
    public Flux<Equipament> getAllEquipaments() {
        return equipamentRepository.getAllEquipaments();
    }

    @Override
    public Mono<Equipament> getEquipamentByName(String name) {
        return equipamentRepository.getEquipamentByName(name);
    }

    @Override
    public Mono<Equipament> updateEquipament(Long id, Equipament updatedEquipament) {
        return equipamentRepository.updateEquipament(id, updatedEquipament);
    }

    @Override
    public Mono<String> deleteEquipamentByName(String name) {
        return equipamentRepository.deleteEquipamentByName(name);
    }
}