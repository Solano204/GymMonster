package application.Ports.Drivers.IServices;

import infraestrucutre.Adapters.Drivens.DTOS.DtoMembershipReciving;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MembershipInterface {

    
        // METHOD MEMBERSHIP

    Flux<DtoMembershipReciving> getAllMemberships();

    // Mono<Membership> getMembershipByName(String type);
}
