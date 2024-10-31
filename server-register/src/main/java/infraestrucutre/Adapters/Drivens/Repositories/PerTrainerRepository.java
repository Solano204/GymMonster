package infraestrucutre.Adapters.Drivens.Repositories;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import infraestrucutre.Adapters.Drivens.DTOS.DtoDetailUserSent;
import infraestrucutre.Adapters.Drivens.Entities.PerTrainer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.data.domain.Pageable;

@Repository
public interface PerTrainerRepository extends ReactiveCrudRepository<PerTrainer, Long> {
    // Additional query methods can be added here if needed

    Mono<PerTrainer> findByUsername(String username);

    Mono<Void> deleteByUsername(String username);

    // Custom query to fetch specialties by 
    // Custom query to join clients with detail_user and select the needed fields
    // Custom query to join clients and detail_user based on trainer ID
    @Query("""
            SELECT d.*
            FROM clients c
            JOIN detail_user d ON c.id_detail = d.id
            WHERE c.id_trainer = :trainerId  LIMIT :size OFFSET :offset """)
    Flux<DtoDetailUserSent> findClientsByTrainerId(@Param("trainerId") Long trainerId, int size, int offset);

    // Custom query to fetch all trainers with pagination
    @Query("SELECT * FROM per_trainer LIMIT :size OFFSET :offset")
    Flux<PerTrainer> findAllTrainers(int size, int offset);
}
