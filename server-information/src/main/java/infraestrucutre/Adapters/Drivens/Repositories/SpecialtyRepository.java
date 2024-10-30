package infraestrucutre.Adapters.Drivens.Repositories;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import infraestrucutre.Adapters.Drivens.Entities.Specialty;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface SpecialtyRepository extends ReactiveCrudRepository<Specialty, Integer> {
    // Additional query methods can be added if necessary

    Mono<Specialty> findByName(String name);

    Mono<Void> deleteByName(String name);

    
    @Query("""
            SELECT s.* FROM specialty s
            JOIN pertrainer_specialty ps ON s.id = ps.specialty_id
            WHERE ps.pertrainer_id = :trainerId""")
    Flux<Specialty> findAllByTrainerId(@Param("trainerId") Long trainerId);
}
