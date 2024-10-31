package application.Ports.Drivens.RepositoriesInterfaces;

import infraestrucutre.Adapters.Drivens.DTOS.DtoMembershipReciving;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MembershipRepositoryInterface {

    Flux<DtoMembershipReciving> getAllMemberships();
    Mono<DtoMembershipReciving> getMembershipByType(String type);
    Mono<DtoMembershipReciving> createMembership(DtoMembershipReciving membership);
    Mono<String> updateMembership(String id, DtoMembershipReciving updatedMembership);
    Mono<String> deleteMembershipByType(String type);
}