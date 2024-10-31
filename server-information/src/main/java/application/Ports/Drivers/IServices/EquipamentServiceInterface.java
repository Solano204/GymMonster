package application.Ports.Drivers.IServices;

import infraestrucutre.Adapters.Drivens.Entities.Equipament;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EquipamentServiceInterface {

    Mono<Equipament> createEquipament(Equipament equipament);

    Flux<Equipament> getAllEquipaments();

    Mono<Equipament> getEquipamentByName(String name);

    Mono<Equipament> updateEquipament(Long id, Equipament equipament);

    Mono<Void> deleteEquipament(String name);
}
    