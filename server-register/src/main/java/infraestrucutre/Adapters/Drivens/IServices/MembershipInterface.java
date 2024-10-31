package infraestrucutre.Adapters.Drivens.IServices;

import infraestrucutre.Adapters.Drivens.Entities.Membership;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MembershipInterface {
    Flux<Membership> getAllMemberships();
}
