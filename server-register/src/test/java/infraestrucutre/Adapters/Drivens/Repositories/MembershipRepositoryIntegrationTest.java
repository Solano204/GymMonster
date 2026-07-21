package infraestrucutre.Adapters.Drivens.Repositories;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.r2dbc.core.DatabaseClient;

import com.monster.server_register.R2dbcContainerTest;

import reactor.test.StepVerifier;

@DataR2dbcTest
class MembershipRepositoryIntegrationTest extends R2dbcContainerTest {

    @Autowired
    private MembershipRepository membershipRepository;

    @Autowired
    private DatabaseClient db;

    private void insertMembership(String type) {
        db.sql("INSERT INTO membership (membership_type, description, has_cardio, has_pool, has_food_court) "
                        + "VALUES (:type, 'desc', true, false, true)")
                .bind("type", type).fetch().rowsUpdated().block();
    }

    @Test
    void findByMembershipType_returnsTheMatchingRow() {
        insertMembership("premium");

        StepVerifier.create(membershipRepository.findByMembershipType("premium"))
                .assertNext(m -> assertThat(m.isHasFoodCourt()).isTrue())
                .verifyComplete();
    }

    @Test
    void existsByMembershipType_reflectsWhetherTheRowExists() {
        insertMembership("basic");

        StepVerifier.create(membershipRepository.existsByMembershipType("basic")).expectNext(true).verifyComplete();
        StepVerifier.create(membershipRepository.existsByMembershipType("ghost")).expectNext(false).verifyComplete();
    }

    @Test
    void deleteByMembershipType_removesTheRow() {
        insertMembership("trial");

        StepVerifier.create(membershipRepository.deleteByMembershipType("trial")).verifyComplete();
        StepVerifier.create(membershipRepository.existsByMembershipType("trial")).expectNext(false).verifyComplete();
    }
}
