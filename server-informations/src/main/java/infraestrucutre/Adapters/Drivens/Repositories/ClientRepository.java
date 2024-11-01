package infraestrucutre.Adapters.Drivens.Repositories;

import infraestrucutre.Adapters.Drivens.DTOS.DtoInfoGeneralClient;
import infraestrucutre.Adapters.Drivens.Entities.AllClient;
import infraestrucutre.Adapters.Drivens.Entities.ClassTrainer;
import infraestrucutre.Adapters.Drivens.Entities.Client;
import infraestrucutre.Adapters.Drivens.Entities.WorkClass;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;

public interface ClientRepository extends ReactiveCrudRepository<Client, Long> {

    @Query("""
        SELECT c.username, c.email,
        du.name AS name, du.second_name AS secondname, du.last_name_p AS lastnamep, du.last_name_m AS lastnamem, du.age, du.height, du.weight,
        m.membership_type AS membershiptype, pt.username AS trainername,
        i.date_Inscription AS startdate, i.start_month AS startinscription, i.end_month AS endinscription, i.price AS price
        FROM clients c  
        JOIN detail_user du ON c.id_detail = du.id
        JOIN membership m ON c.id_membership = m.id
        JOIN Inscription i ON c.id = i.id_client
        JOIN per_trainer pt ON c.id_trainer = pt.id
        WHERE c.id = :clientId
            """)
    Mono<AllClient> findClientDetailMembershipByClientId(Long clientId);

    @Query("""
       SELECT wc.id AS id, wc.name AS name, wc.description AS description, wc.duration AS duration
       FROM clients c
       JOIN client_work_class cwc ON c.id = cwc.client_id
       JOIN work_class wc ON cwc.work_class_id = wc.id
       WHERE c.id = :clientId
       """)
     Flux<WorkClass> findWorkClassesByClientId(Long clientId);
    
    Mono<Client> findByUsername(String username);

    Mono<Void>  deleteByUsername(String username);       


    @Query("""
        SELECT c.username, c.email,
        du.name AS name, du.second_name AS secondname, du.last_name_p AS lastnamep, du.last_name_m AS lastnamem, du.age, du.height, du.weight,
        m.membership_type AS membershiptype, pt.username AS trainername,
        i.date_Inscription AS startdate, i.start_month AS startinscription, i.end_month AS endinscription, i.price AS price
        FROM clients c  
        JOIN detail_user du ON c.id_detail = du.id
        JOIN membership m ON c.id_membership = m.id
        JOIN Inscription i ON c.id = i.id_client
        JOIN per_trainer pt ON c.id_trainer = pt.id
        LIMIT :size OFFSET :offset
        """)
Flux<AllClient> findAllClientsAD(@Param("size") int size, @Param("offset") int offset);



    Mono<Client> save(Client client); 

    Mono<Boolean> existsByUsername(String username);

    Mono<Boolean> existsByEmail(String email);

    Mono<Boolean> existsByPassword(String Password);
  }
