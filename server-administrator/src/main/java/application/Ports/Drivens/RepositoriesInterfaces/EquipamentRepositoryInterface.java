package application.Ports.Drivens.RepositoriesInterfaces;
import infraestrucutre.Adapters.Drivens.Entities.Equipament;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EquipamentRepositoryInterface {

    Mono<Equipament> createEquipament(Equipament equipament);
    Flux<Equipament> getAllEquipaments();
    Mono<Equipament> getEquipamentByName(String name);
    Mono<Equipament> updateEquipament(Long id, Equipament updatedEquipament);
    Mono<String> deleteEquipamentByName(String name);
}
