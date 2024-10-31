package infraestrucutre.Adapters.Drivens.IServices;

import java.util.List;

import infraestrucutre.Adapters.Drivens.DTOS.DtoDetailUserSent;
import infraestrucutre.Adapters.Drivens.Entities.AllClient;
import infraestrucutre.Adapters.Drivens.Entities.WorkClass;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ClientServiceInterface {

    Mono<List<String>> createClient(AllClient client);  
}
