package infraestrucutre.Adapters.Drivens.Repositories;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.r2dbc.core.DatabaseClient;

import com.monster.server_informations.R2dbcContainerTest;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DataR2dbcTest
class ScheduleRepositoryIntegrationTest extends R2dbcContainerTest {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private DatabaseClient db;

    private Mono<Long> insertSchedule(String day, String start, String end) {
        return db.sql("INSERT INTO schedules (day, start_time, end_time) VALUES (:day, :start, :end)")
                .bind("day", day).bind("start", start).bind("end", end)
                .fetch().rowsUpdated()
                .then(db.sql("SELECT id FROM schedules WHERE day = :day AND start_time = :start")
                        .bind("day", day).bind("start", start).map(row -> row.get("id", Long.class)).one());
    }

    private Mono<Void> insertScheduleGym(String day) {
        return db.sql("INSERT INTO schedulesGym (day, start_time, end_time) VALUES (:day, '06:00', '07:00')")
                .bind("day", day).fetch().rowsUpdated().then();
    }

    private Mono<Long> insertWorkClass(String name) {
        return db.sql("INSERT INTO work_class (name, description, duration) VALUES (:name, 'desc', '60min')")
                .bind("name", name).fetch().rowsUpdated()
                .then(db.sql("SELECT id FROM work_class WHERE name = :name")
                        .bind("name", name).map(row -> row.get("id", Long.class)).one());
    }

    @Test
    void findByDay_returnsSchedulesForThatDay() {
        insertSchedule("TUESDAY", "08:00", "09:00").block();

        StepVerifier.create(scheduleRepository.findByDay("TUESDAY"))
                .assertNext(schedule -> assertThat(schedule.getStartTime()).isEqualTo("08:00"))
                .verifyComplete();
    }

    @Test
    void findByStartTime_returnsSchedulesStartingAtThatTime() {
        insertSchedule("WEDNESDAY", "10:00", "11:00").block();

        StepVerifier.create(scheduleRepository.findByStartTime("10:00"))
                .assertNext(schedule -> assertThat(schedule.getDay()).isEqualTo("WEDNESDAY"))
                .verifyComplete();
    }

    @Test
    void findSchedulesByWorkClassId_joinsThroughWorkClassSchedulesLinkTable() {
        Long scheduleId = insertSchedule("THURSDAY", "12:00", "13:00").block();
        Long workClassId = insertWorkClass("Zumba-SC").block();
        db.sql("INSERT INTO work_class_schedules (work_class_id, schedule_id) VALUES (:w, :s)")
                .bind("w", workClassId).bind("s", scheduleId).fetch().rowsUpdated().block();

        StepVerifier.create(scheduleRepository.findSchedulesByWorkClassId(workClassId))
                .assertNext(schedule -> assertThat(schedule.getDay()).isEqualTo("THURSDAY"))
                .verifyComplete();
    }

    @Test
    void findSchedulesGym_queriesTheSeparateGymScheduleTable() {
        insertScheduleGym("FRIDAY").block();

        StepVerifier.create(scheduleRepository.findSchedulesGym("FRIDAY"))
                .assertNext(schedule -> assertThat(schedule.getDay()).isEqualTo("FRIDAY"))
                .verifyComplete();
    }

    @Test
    void findSchedulesGym_returnsEmpty_forADayWithNoGymSchedule() {
        StepVerifier.create(scheduleRepository.findSchedulesGym("SUNDAY")).verifyComplete();
    }
}
