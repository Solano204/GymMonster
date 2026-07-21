package infraestrucutre.Adapters.Drivens.ImpServices;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import application.Ports.Drivens.RepositoriesInterfaces.MembershipInformationClientInterface;
import infraestrucutre.Adapters.Drivens.DTOS.DtoMembershipReciving;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class MembershipServiceTest {

    @Mock
    private MembershipInformationClientInterface membershipRepository;

    private MembershipService service;

    @BeforeEach
    void setUp() {
        service = new MembershipService(membershipRepository);
    }

    private DtoMembershipReciving sample() {
        return new DtoMembershipReciving("GOLD", "Full access", true, true, true);
    }

    @Test
    void getAllMemberships_delegatesToRepository() {
        DtoMembershipReciving membership = sample();
        when(membershipRepository.getAllMemberships()).thenReturn(Flux.just(membership));

        StepVerifier.create(service.getAllMemberships()).expectNext(membership).verifyComplete();
    }

    @Test
    void createMembership_delegatesToRepository() {
        DtoMembershipReciving membership = sample();
        when(membershipRepository.createMembership(membership)).thenReturn(Mono.just(membership));

        StepVerifier.create(service.createMembership(membership)).expectNext(membership).verifyComplete();
    }

    @Test
    void updateMembership_delegatesToRepositoryWithIdAndPayload() {
        DtoMembershipReciving membership = sample();
        when(membershipRepository.updateMembership("5", membership)).thenReturn(Mono.just("Updated"));

        StepVerifier.create(service.updateMembership("5", membership)).expectNext("Updated").verifyComplete();
        verify(membershipRepository).updateMembership("5", membership);
    }

    @Test
    void deleteMembershipByType_delegatesToRepository() {
        when(membershipRepository.deleteMembershipByType("GOLD")).thenReturn(Mono.just("Deleted"));

        StepVerifier.create(service.deleteMembershipByType("GOLD")).expectNext("Deleted").verifyComplete();
    }
}
