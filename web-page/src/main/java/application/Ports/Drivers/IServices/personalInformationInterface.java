package application.Ports.Drivers.IServices;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.stereotype.Service;

import infraestrucutre.Adapters.Drivens.DTOS.DtoInfoGeneralClient;
import infraestrucutre.Adapters.Drivens.Entities.Promotion;
import infraestrucutre.Adapters.Drivens.Entities.WorkClass;

public interface personalInformationInterface {
    Mono<DtoInfoGeneralClient> getClientAllInformation(String username);
}
