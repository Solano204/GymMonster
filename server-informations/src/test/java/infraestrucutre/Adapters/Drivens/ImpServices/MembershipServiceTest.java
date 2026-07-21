package infraestrucutre.Adapters.Drivens.ImpServices;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import infraestrucutre.Adapters.Drivens.Entities.Membership;
import infraestrucutre.Adapters.Drivens.Repositories.MembershipRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class MembershipServiceTest {

    @Mock
    private MembershipRepository membershipRepository;

    private MembershipService service;

    @BeforeEach
    void setUp() {
        service = new MembershipService(membershipRepository);
    }

    private Membership sample() {
        return new Membership(1L, "GOLD", "desc", true, true, true, "49.99");
    }

    @Test
    void createMembership_delegatesToSave() {
        Membership membership = sample();
        when(membershipRepository.save(membership)).thenReturn(Mono.just(membership));

        StepVerifier.create(service.createMembership(membership)).expectNext(membership).verifyComplete();
    }

    @Test
    void getAllMemberships_delegatesToFindAll() {
        when(membershipRepository.findAll()).thenReturn(Flux.just(sample()));

        StepVerifier.create(service.getAllMemberships()).expectNextCount(1).verifyComplete();
    }

    @Test
    void getMembershipByName_delegatesToFindByMembershipType() {
        Membership membership = sample();
        when(membershipRepository.findByMembershipType("GOLD")).thenReturn(Mono.just(membership));

        StepVerifier.create(service.getMembershipByName("GOLD")).expectNext(membership).verifyComplete();
    }

    @Test
    void updateMembership_mutatesTheExistingRowFieldsThenSaves() {
        Membership existing = new Membership(1L, "OLD", "old-desc", false, false, false, "10");
        Membership incoming = new Membership(null, "NEW", "new-desc", true, true, true, null);
        when(membershipRepository.findById(1L)).thenReturn(Mono.just(existing));
        when(membershipRepository.save(existing)).thenReturn(Mono.just(existing));

        StepVerifier.create(service.updateMembership(1L, incoming))
                .assertNext(saved -> {
                    assertThat(saved.getMembershipType()).isEqualTo("NEW");
                    assertThat(saved.isHasCardio()).isTrue();
                })
                .verifyComplete();
    }

    @Test
    void deleteMembership_delegatesToDeleteByMembershipType() {
        when(membershipRepository.deleteByMembershipType("GOLD")).thenReturn(Mono.empty());

        StepVerifier.create(service.deleteMembership("GOLD")).verifyComplete();
    }
}
