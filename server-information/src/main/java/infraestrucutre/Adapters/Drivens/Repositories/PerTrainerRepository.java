package infraestrucutre.Adapters.Drivens.Repositories;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import infraestrucutre.Adapters.Drivens.DTOS.DtoInfoGeneralClient;
import infraestrucutre.Adapters.Drivens.DTOS.DtoInfoTrainer;
import infraestrucutre.Adapters.Drivens.Entities.DetailUser;
import infraestrucutre.Adapters.Drivens.Entities.PerTrainer;
import infraestrucutre.Adapters.Drivens.Entities.Specialty;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.data.domain.Pageable;

public interface PerTrainerRepository extends ReactiveCrudRepository<PerTrainer, Long> {
    // Additional query methods can be added here if needed

    Mono<PerTrainer>findByUsername(String username);

    Mono<Void> deleteByUsername(String username);

    // Custom query to fetch specialties by trainer ID
    @Query("""
            SELECT s.* FROM specialty s
            JOIN pertrainer_specialty ps ON s.id = ps.specialty_id
            WHERE ps.pertrainer_id =:trainerId
            """)
    Flux<Specialty> findAllSpecialtyByTrainerId(@Param("trainerId") Long trainerId);

    // Custom query to join clients with detail_user and select the needed fields
    // Custom query to join clients and detail_user based on trainer ID
    @Query("""
            SELECT d.*
            FROM clients c
            JOIN detail_user d ON c.id_detail = d.id
            WHERE c.id_trainer = :trainerId  LIMIT :size OFFSET :offset """)
    Flux<DetailUser> findClientsByTrainerId(@Param("trainerId") Long trainerId, int size, int offset);

    // Custom query to fetch all trainers with pagination
    @Query("SELECT * FROM per_trainer LIMIT :size OFFSET :offset")
    Flux<PerTrainer> findAllTrainers(int size, int offset);

    // Custom query to fetch all trainers with pagination
    @Query("""
    SELECT c.username as mamalon, c.email, du.name AS name, du.second_name , du.last_name_p , du.last_name_m, 
    du.age, du.height, du.weight, c.start_date as beggining,
    COUNT(cl.id) as clients
    FROM per_trainer c
    JOIN detail_per_trainer du ON c.id_detail = du.id
    LEFT JOIN clients cl ON c.id = cl.id_trainer
    GROUP BY c.id, c.username, c.email, du.name, du.second_name, du.last_name_p, du.last_name_m, 
    du.age, du.height, du.weight
    LIMIT :size OFFSET :offset
    """)
Flux<DtoInfoTrainer> findAllTrainersAllInformation(
    @Param("size") int limit,
    @Param("offset") int offset
);

    @Query("""
            SELECT c.id AS trainer_id, c.username, c.email,
            du.name AS name, du.secondName, du.last_name_p as LastNameP, du.last_name_m as LastNameM, du.age, du.height, du.weight
            FROM per_trainer c
            JOIN detail_user du ON c.id_detail = du.id
            WHERE c.id = :trainerId """)
    Mono<DtoInfoTrainer> findAllInfoTrainer(Long trainerId);

    @Query("""
            SELECT
            du.name AS name, du.second_name, du.last_name_p, du.last_name_m, du.age, du.height, du.weight
            FROM per_trainer c
            JOIN detail_user du ON c.id_detail = du.id
            WHERE c.id = :trainerId """)
    Mono<DetailUser> findInfoTrainer(Long trainerId);

    Mono<Boolean> existsByUsername(String username);

    Mono<Boolean> existsByEmail(String email);

    Mono<Boolean> existsByPassword(String Password);


     // Check if a given specialty exists for a trainer
     @Query("""
        SELECT COUNT(*) > 0
        FROM specialty s
        JOIN pertrainer_specialty ps ON s.id = ps.specialty_id
        JOIN per_trainer t ON ps.pertrainer_id = t.id
        WHERE t.id = :trainerId AND s.name = :specialtyName
    """)
Mono<Integer> existsSpecialtyForTrainer(@Param("trainerId") Long trainerId, @Param("specialtyName") String specialtyName);


     @Modifying
    @Transactional
    @Query(value = "INSERT INTO pertrainer_specialty (pertrainer_id, specialty_id) VALUES (:perTrainerId, :specialtyId)")
    Mono<Void> addSpecialtyToTrainer(@Param("perTrainerId") Long perTrainerId, @Param("specialtyId") Long newSpecialty);


    @Modifying
    @Transactional
    @Query(value = "DELETE FROM pertrainer_specialty WHERE pertrainer_id = :perTrainerId AND specialty_id = :specialtyId")
    Mono<Void> removeSpecialtyFromTrainer(@Param("perTrainerId") Long perTrainerId, @Param("specialtyId") Long integer);
}
