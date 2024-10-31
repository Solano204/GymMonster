package infraestrucutre.Adapters.Drivens.Repositories;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import infraestrucutre.Adapters.Drivens.DTOS.DtoInfoTrainer;
import infraestrucutre.Adapters.Drivens.Entities.ClassTrainer;
import infraestrucutre.Adapters.Drivens.Entities.PerTrainer;
import infraestrucutre.Adapters.Drivens.Entities.Schedule;
import infraestrucutre.Adapters.Drivens.Entities.Specialty;
import infraestrucutre.Adapters.Drivens.Entities.WorkClass;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface ClassTrainerRepository extends ReactiveCrudRepository<ClassTrainer, Long> {

    // Find by username (as it's extending User)
    Mono<ClassTrainer> findByUsername(String username);

    // Find ClassTrainers by a specific Specialty
    @Query("""
            SELECT s.* FROM specialty s
            JOIN pertrainer_specialty ps ON s.id = ps.specialty_id
            WHERE ps.pertrainer_id = :trainerId""")
    Flux<Specialty> findAllSpecialty(@Param("trainerId") Long trainerId);

    // Custom query to fetch all work classes associated with a specific trainer
    @Query("""
            SELECT wc.* FROM work_class wc 
            JOIN trainer_class_work_class tcwc ON wc.id = tcwc.work_class_id
            JOIN trainers_class tc ON tc.id = tcwc.trainer_class_id 
            WHERE tc.id = :trainerId """)
    Flux<WorkClass> findAllClassByTrainer(@Param("trainerId") Long trainerId);



    @Query("""
    SELECT c.username as mamalon, c.email, du.name AS name, du.second_name, du.last_name_p, du.last_name_m, 
    du.age, du.height, du.weight, c.start_date AS beggining
    FROM trainers_class c
    JOIN detail_class_trainer du ON c.id_detail = du.id
    LEFT JOIN clients cl ON c.id = cl.id_trainer
    LIMIT :size OFFSET :offset
    """)
Flux<DtoInfoTrainer> findAllTrainersAllInformation(
    @Param("size") int limit,
    @Param("offset") int offset
);


    @Query("SELECT * FROM trainers_class LIMIT :size OFFSET :offset")
  Flux<ClassTrainer> findAllTrainers(int size,int offset);

    Mono<Void> deleteByUsername(String username);

    Mono<Boolean> existsByUsername(String username);

    Mono<Boolean> existsByEmail(String email);

    Mono<Boolean> existsByPassword(String Password);


     // Check if a given specialty exists for a trainer
     @Query("""
        SELECT COUNT(*) > 0
        FROM specialty s
        JOIN trainer_class_specialty ps ON s.id = ps.specialty_id
        JOIN trainers_class t ON ps.trainer_class_id = t.id
        WHERE t.id = :trainerId AND s.name = :specialtyName
    """)
    Mono<Integer> existsSpecialtyForTrainer(@Param("trainerId") Long trainerId, @Param("specialtyName") String specialtyName);

    @Query("""
        SELECT COUNT(*)
        FROM work_class s
        JOIN trainer_class_work_class ps ON s.id = ps.work_class_id
        JOIN trainers_class t ON ps.trainer_class_id = t.id
        WHERE t.id = :trainerId AND s.name = :className
    """)
    Mono<Integer> existsClassForTrainer(@Param("trainerId") Long trainerId, @Param("className") String specialtyName);


    @Modifying
    @Transactional
    @Query(value = "INSERT INTO trainer_class_specialty (trainer_class_id, specialty_id) VALUES (:perTrainerId, :specialtyId)")
    Mono<Void> addSpecialtyToTrainer(@Param("perTrainerId") Long perTrainerId, @Param("specialtyId") Long idSpecialty);


    @Modifying
    @Transactional
    @Query(value = "DELETE FROM trainer_class_specialty WHERE trainer_class_id = :perTrainerId AND specialty_id = :specialtyId")
    Mono<Void> removeSpecialtyFromTrainer(@Param("perTrainerId") Long perTrainerId, @Param("specialtyId") Long integer);


    @Modifying
    @Transactional
    @Query(value = "INSERT INTO trainer_class_work_class (trainer_class_id, work_class_id) VALUES (:perTrainerId, :workClassId)")
    Mono<Void> addClassToTrainer(@Param("perTrainerId") Long perTrainerId, @Param("workClassId") Long idClass);


    @Modifying
    @Transactional
    @Query(value = "DELETE FROM trainer_class_work_class WHERE trainer_class_id = :perTrainerId AND work_class_id = :workClassId")
    Mono<Void> removeClassFromTrainer(@Param("perTrainerId") Long perTrainerId, @Param("workClassId") Long idClass);


}