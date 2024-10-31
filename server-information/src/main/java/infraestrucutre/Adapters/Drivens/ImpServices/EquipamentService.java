package infraestrucutre.Adapters.Drivens.ImpServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import application.Ports.Drivers.IServices.EquipamentServiceInterface;
import infraestrucutre.Adapters.Drivens.Entities.Client;
import infraestrucutre.Adapters.Drivens.Entities.Equipament;
import infraestrucutre.Adapters.Drivens.Repositories.ClientRepository;
import infraestrucutre.Adapters.Drivens.Repositories.EquipamentRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
@Service
@AllArgsConstructor
public class EquipamentService implements EquipamentServiceInterface {

    private final EquipamentRepository equipamentRepository;

    @Override
    public Mono<Equipament> createEquipament(Equipament equipament) {
        return equipamentRepository.save(equipament);
    }

    @Override
    public Flux<Equipament> getAllEquipaments() {
        return equipamentRepository.findAll();
    }

    @Override
    public Mono<Equipament> getEquipamentByName(String name) {
        return equipamentRepository.findByName(name);
    }

    @Override
    public Mono<Equipament> updateEquipament(Long id, Equipament equipament) {
        return equipamentRepository.findById(id)
                .flatMap(existingEquipament -> {
                    // Update the fields
                    existingEquipament.setName(equipament.getName());
                    existingEquipament.setDescription(equipament.getDescription());
                    existingEquipament.setStartDate(equipament.getStartDate());
                    existingEquipament.setEndDate(equipament.getEndDate());
                    existingEquipament.setAgeStatus(equipament.getAgeStatus());
                    return equipamentRepository.save(existingEquipament);
                });
    }

    @Override
    public Mono<Void> deleteEquipament(String name) {
        return equipamentRepository.deleteByName(name);
    }
}