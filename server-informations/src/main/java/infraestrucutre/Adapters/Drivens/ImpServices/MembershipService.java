package infraestrucutre.Adapters.Drivens.ImpServices;

import org.springframework.stereotype.Service;

import application.Ports.Drivers.IServices.MembershipServiceInterface;
import infraestrucutre.Adapters.Drivens.Entities.Membership;
import infraestrucutre.Adapters.Drivens.Repositories.MembershipRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
@Data
public class MembershipService implements MembershipServiceInterface {

    private final MembershipRepository membershipRepository;

    @Override
    public Mono<Membership> createMembership(Membership membership) {
        return membershipRepository.save(membership);
    }

    @Override
    public Flux<Membership> getAllMemberships() {
        return membershipRepository.findAll();
    }

    @Override
    public Mono<Membership> getMembershipByName(String type) {
        return membershipRepository.findByMembershipType(type);
    }

    @Override
    public Mono<Membership> updateMembership(Long id, Membership membership) {
        return membershipRepository.findById(id)
                .flatMap(existingMembership -> {
                    // Update fields as needed
                    existingMembership.setMembershipType(membership.getMembershipType());
                    existingMembership.setDescription(membership.getDescription());
                    existingMembership.setHasCardio(membership.isHasCardio());
                    existingMembership.setHasPool(membership.isHasPool());
                    existingMembership.setHasFoodCourt(membership.isHasFoodCourt());
                    return membershipRepository.save(existingMembership);
                });
    }

    @Override
    public Mono<Void> deleteMembership(String type) {
        return membershipRepository.deleteByMembershipType(type);
    }
}
