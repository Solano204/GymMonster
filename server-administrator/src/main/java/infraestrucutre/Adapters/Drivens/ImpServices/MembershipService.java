package infraestrucutre.Adapters.Drivens.ImpServices;

 import org.springframework.stereotype.Service;

import application.Ports.Drivens.RepositoriesInterfaces.MembershipRepositoryInterface;
import application.Ports.Drivers.IServices.MembershipServiceInterface;
import infraestrucutre.Adapters.Drivens.DTOS.DtoMembershipReciving;
import infraestrucutre.Adapters.Drivens.DTOS.DtoMembershipSent;
import infraestrucutre.Adapters.Drivens.Repositories.MembershipRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import reactor.core.publisher.Flux;
import java.util.List;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Data
public class MembershipService implements MembershipServiceInterface {

    private final MembershipRepositoryInterface membershipRepository; // Inject your repository here


    @Override
    public Flux<DtoMembershipReciving> getAllMemberships() {
        return membershipRepository.getAllMemberships(); // Assuming this method exists in your repository
    }

   

    @Override
    public Mono<DtoMembershipReciving> createMembership(DtoMembershipReciving membership) {
        return membershipRepository.createMembership(membership); // Implement save in your repository
    }

    @Override
    public Mono<String> updateMembership(String id, DtoMembershipReciving updatedMembership) {
        return membershipRepository.updateMembership(id, updatedMembership); // Implement update in your repository
    }

    @Override
    public Mono<String> deleteMembershipByType(String type) {
        return membershipRepository.deleteMembershipByType(type); // Implement delete in your repository
    }
}
