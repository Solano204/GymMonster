package infraestrucutre.Adapters.Drivens.Repositories;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import infraestrucutre.Adapters.Drivens.Entities.Membership;
import reactor.core.publisher.Mono;

@Repository
public interface MembershipRepository extends ReactiveCrudRepository<Membership, Long> {
    // You can add custom query methods if needed, but the basic CRUD operations are already provided

    @Query("SELECT * FROM membership WHERE membership_type = :type")
    Mono<Membership> findByMembershipType(String type);
    Mono <Void> deleteByMembershipType(String type);
    Mono<Boolean> existsByMembershipType(String type);
}
    