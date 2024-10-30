package infraestrucutre.Adapters.Drivens.Repositories;

import org.springframework.boot.autoconfigure.mustache.MustacheProperties.Reactive;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import infraestrucutre.Adapters.Drivens.Entities.Inscription;
import reactor.core.publisher.Mono;

public interface InscriptionRepository extends ReactiveCrudRepository<Inscription, Long> {
    


    @Query("DELETE FROM Inscription i WHERE i.id_client = :idClient")
    Mono<Void> deleteByClientId(@Param("idClient") Long idClient);
}
