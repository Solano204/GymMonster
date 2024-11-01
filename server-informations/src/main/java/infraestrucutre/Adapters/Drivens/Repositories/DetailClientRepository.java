package infraestrucutre.Adapters.Drivens.Repositories;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import infraestrucutre.Adapters.Drivens.Entities.Client;
import infraestrucutre.Adapters.Drivens.Entities.DetailUser;
import reactor.core.publisher.Mono;

public interface DetailClientRepository extends ReactiveCrudRepository<DetailUser, Long> {
 
     // In your repository interface
@Query("SELECT COUNT(*) FROM detail_user WHERE name = :name AND second_name = :secondName AND last_name_m = :lastNameM AND last_name_p = :lastNameP")
Mono<Integer> countByDetailInfo(String name, String secondName, String lastNameM, String lastNameP);
     

@Query("SELECT d.*	  FROM detail_user AS d WHERE d.name = :name AND d.second_name = :secondName AND d.last_name_m = :lastNameM AND d.last_name_p = :lastNameP")

    Mono<DetailUser> findDetailUserByDetails(
            @Param("name") String name,
            @Param("secondName") String secondName,
            @Param("lastNameM") String lastNameM,
            @Param("lastNameP") String lastNameP);
}
