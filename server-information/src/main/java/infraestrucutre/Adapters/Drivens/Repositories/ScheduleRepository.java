package infraestrucutre.Adapters.Drivens.Repositories;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import infraestrucutre.Adapters.Drivens.Entities.Schedule;
import reactor.core.publisher.Flux;

@Repository
public interface ScheduleRepository extends ReactiveCrudRepository<Schedule, Integer> {

    // Find Schedules by name (example)

    // Find Schedules by day (example)
    Flux<Schedule> findByDay(String day);

    // Find Schedules by start time (example)
    Flux<Schedule> findByStartTime(String startTime);

    // Custom query to get Schedules for a specific WorkClass
    @Query("SELECT s.* FROM schedules s " +
            "JOIN work_class_schedules wcs ON s.id = wcs.schedule_id " +
            "WHERE wcs.work_class_id = :workClassId")
    Flux<Schedule> findSchedulesByWorkClassId(@Param("workClassId") Long workClassId);

    @Query("""
            SELECT s.* FROM schedulesGym s 
            WHERE s.day=:Day""")
    Flux<Schedule> findSchedulesGym(@Param("Day") String day);

}
