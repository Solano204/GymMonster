package application.Ports.Drivers.IServices;

import infraestrucutre.Adapters.Drivens.Entities.Schedule;
import reactor.core.publisher.Flux;

public interface ScheduleRepisitoryInterface {
    public Flux<Schedule> getScheduleByDayGym(String day);
}
