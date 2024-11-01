package application.Ports.Drivers.IServices;

import infraestrucutre.Adapters.Drivens.Entities.Membership;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MembershipServiceInterface {

    Mono<Membership> createMembership(Membership membership);

    Flux<Membership> getAllMemberships();

    Mono<Membership> getMembershipByName(String type);

    Mono<Membership> updateMembership(Long id, Membership membership);

    Mono<Void> deleteMembership(String type);
}
