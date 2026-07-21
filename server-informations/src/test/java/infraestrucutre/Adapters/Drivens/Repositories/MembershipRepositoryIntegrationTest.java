package infraestrucutre.Adapters.Drivens.Repositories;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.r2dbc.core.DatabaseClient;

import com.monster.server_informations.R2dbcContainerTest;

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
        insertMembership("GOLD-M");

        StepVerifier.create(membershipRepository.findByMembershipType("GOLD-M"))
                .assertNext(m -> {
                    assertThat(m.isHasCardio()).isTrue();
                    assertThat(m.isHasPool()).isFalse();
                })
                .verifyComplete();
    }

    @Test
    void existsByMembershipType_reflectsWhetherTheRowExists() {
        insertMembership("SILVER-M");

        StepVerifier.create(membershipRepository.existsByMembershipType("SILVER-M")).expectNext(true).verifyComplete();
        StepVerifier.create(membershipRepository.existsByMembershipType("GHOST-M")).expectNext(false).verifyComplete();
    }

    @Test
    void deleteByMembershipType_removesTheRow() {
        insertMembership("BRONZE-M");

        StepVerifier.create(membershipRepository.deleteByMembershipType("BRONZE-M")).verifyComplete();
        StepVerifier.create(membershipRepository.existsByMembershipType("BRONZE-M")).expectNext(false).verifyComplete();
    }
}
