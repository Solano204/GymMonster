package application.Ports.Drivens.InterfaceRepositories;
import infraestrucutre.Adapters.Drivens.DTOS.DtoMembershipReciving;
import reactor.core.publisher.Flux;

public interface MembershipRepositoryInterface {
    Flux<DtoMembershipReciving> getAllMemberships();
}
