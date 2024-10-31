package application.Ports.Drivers.IServices;

import infraestrucutre.Adapters.Drivens.DTOS.DtoMembershipReciving;
import infraestrucutre.Adapters.Drivens.DTOS.DtoMembershipSent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MembershipServiceInterface {

    Flux<DtoMembershipReciving> getAllMemberships();


    Mono<DtoMembershipReciving> createMembership(DtoMembershipReciving membership);

    Mono<String> updateMembership(String id, DtoMembershipReciving updatedMembership);

    Mono<String> deleteMembershipByType(String type);
}

