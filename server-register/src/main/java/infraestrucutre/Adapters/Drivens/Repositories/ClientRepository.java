package infraestrucutre.Adapters.Drivens.Repositories;

import infraestrucutre.Adapters.Drivens.Entities.Client;
import infraestrucutre.Adapters.Drivens.Entities.WorkClass;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;

public interface ClientRepository extends ReactiveCrudRepository<Client, Long> {

    
    Mono<Client> findByUsername(String username);

    Mono<Void> deleteByUsername(String username);       

    Mono<Client> save(Client client); 

    Mono<Boolean> existsByUsername(String username);

    Mono<Boolean> existsByEmail(String email);

    Mono<Boolean> existsByPassword(String Password);


    
  }
