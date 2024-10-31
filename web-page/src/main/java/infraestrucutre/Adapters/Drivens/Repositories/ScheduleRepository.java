package infraestrucutre.Adapters.Drivens.Repositories;

import java.util.Collections;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;

import application.Ports.Drivers.IServices.ScheduleRepisitoryInterface;
import infraestrucutre.Adapters.Drivens.Entities.Schedule;
import lombok.*;
import reactor.core.publisher.Flux;

@Repository
@AllArgsConstructor
public class ScheduleRepository implements ScheduleRepisitoryInterface {

    private final WebClient.Builder webClientBuilder;

    @Override
    public Flux<Schedule> getScheduleByDayGym(String day) {
        return this.webClientBuilder.build()
                .get()
                // .uri("lb://schedule-service/api/schedules/day/gym/{day}", Collections.singletonMap("day", day))
                .uri("http://localhost:8111/api/schedules/day/gym/{day}", Collections.singletonMap("day", day))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(Schedule.class);
    }

    
}
