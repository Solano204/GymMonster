package infraestrucutre.Adapters.Drivens.Repositories;

import java.util.Collections;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;

import application.Ports.Drivens.RepositoriesInterfaces.ScheduleRepositoryInterface;
import infraestrucutre.Adapters.Drivens.Entities.Schedule;
import infraestrucutre.Adapters.Drivens.Properties.ServicesUrl;
import lombok.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@AllArgsConstructor
public class ScheduleRepository implements ScheduleRepositoryInterface {

    private final WebClient.Builder webClientBuilder;
           private final ServicesUrl servicesUrl;

           @Override
           public Mono<Schedule> createSchedule(Schedule schedule) {
               return this.webClientBuilder.build()
                       .post()
                       .uri(servicesUrl.getInfo().getUrl() + "/api/schedules")
                       .contentType(MediaType.APPLICATION_JSON)
                       .bodyValue(schedule)
                       .retrieve()
                       .bodyToMono(Schedule.class);
           }
           
           @Override
           public Flux<Schedule> getAllSchedules() {
               return this.webClientBuilder.build()
                       .get()
                       .uri(servicesUrl.getInfo().getUrl() + "/api/schedules")
                       .accept(MediaType.APPLICATION_JSON)
                       .retrieve()
                       .bodyToFlux(Schedule.class);
           }
           
           @Override
           public Flux<Schedule> getScheduleByStartTime(String startTime) {
               return this.webClientBuilder.build()
                       .get()
                       .uri(servicesUrl.getInfo().getUrl() + "/api/schedules/start-time/{startTime}", startTime)
                       .accept(MediaType.APPLICATION_JSON)
                       .retrieve()
                       .bodyToFlux(Schedule.class);
           }
           
           @Override
           public Flux<Schedule> getScheduleByDay(String day) {
               return this.webClientBuilder.build()
                       .get()
                       .uri(servicesUrl.getInfo().getUrl() + "/api/schedules/day/{day}", day)
                       .accept(MediaType.APPLICATION_JSON)
                       .retrieve()
                       .bodyToFlux(Schedule.class);
           }
           
           @Override
           public Flux<Schedule> getScheduleByDayGym(String day) {
               return this.webClientBuilder.build()
                       .get()
                       .uri(servicesUrl.getInfo().getUrl() + "/api/schedules/day/gym/{day}", day)
                       .accept(MediaType.APPLICATION_JSON)
                       .retrieve()
                       .bodyToFlux(Schedule.class);
           }
           
           @Override
           public Mono<Schedule> updateSchedule(Integer id, Schedule updatedSchedule) {
               return this.webClientBuilder.build()
                       .put()
                       .uri(servicesUrl.getInfo().getUrl() + "/api/schedules/{id}", id)
                       .contentType(MediaType.APPLICATION_JSON)
                       .bodyValue(updatedSchedule)
                       .retrieve()
                       .bodyToMono(Schedule.class);
           }
           
           @Override
           public Mono<String> deleteSchedule(Integer id) {
               return this.webClientBuilder.build()
                       .delete()
                       .uri(servicesUrl.getInfo().getUrl() + "/api/schedules/{id}", id)
                       .retrieve()
                       .bodyToMono(String.class);
           }
}
