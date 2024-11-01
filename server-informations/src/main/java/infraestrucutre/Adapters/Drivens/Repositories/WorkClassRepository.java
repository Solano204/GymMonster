package infraestrucutre.Adapters.Drivens.Repositories;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import infraestrucutre.Adapters.Drivens.Entities.DetailUser;
import infraestrucutre.Adapters.Drivens.Entities.WorkClass;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface WorkClassRepository extends ReactiveCrudRepository<WorkClass, Long> {

    // Additional custom query methods if needed

    // Find WorkClasses by name (example)
    Mono<WorkClass> findByName(String name);

    // Find WorkClasses that have a duration greater than a specific value (example)
    Flux<WorkClass> findByDurationGreaterThan(String duration);

    /*
     * OFFSET It helps retrieve different "pages" of data by skipping a certain
     * number of rows before fetching the next set.LIMIT 5 OFFSET 10 (rows 11 to 15
     */
    @Query("""
            SELECT d.*
            FROM clients c
            JOIN detail_user d ON c.id_detail = d.id
            JOIN client_work_class cwc ON c.id = cwc.client_id
            JOIN work_class wc ON cwc.work_class_id = wc.id
            WHERE wc.id = :workClassId
            LIMIT :limit OFFSET :offset
            """)
    Flux<DetailUser> findAllClientsByWorkClass(@Param("workClassId") Long workClassId,
            @Param("limit") int limit,
            @Param("offset") int offset);

    @Query("""
            SELECT d.*
            FROM trainers_class c
            JOIN detail_user d ON c.id_detail = d.id
            JOIN trainer_class_work_class cwc ON c.id = cwc.trainer_class_id
            JOIN work_class wc ON cwc.work_class_id = wc.id
            WHERE wc.id = :workClassId
            LIMIT :limit OFFSET :offset
            """)
    Flux<DetailUser> findAllTrainersByWorkClass(Long workClassId, @Param("limit") int limit,
            @Param("offset") int offset);
}
