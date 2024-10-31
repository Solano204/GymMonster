package infraestrucutre.Adapters.Drivens.Repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import infraestrucutre.Adapters.Drivens.Entities.Equipament;
import reactor.core.publisher.Mono;

@Repository
public interface EquipamentRepository extends ReactiveCrudRepository<Equipament, Long> {
    // Additional query methods can be added here if needed

    // Example method to find Equipament by name
    Mono<Equipament> findByName(String name);

    Mono<Void> deleteByName(String name);
}
