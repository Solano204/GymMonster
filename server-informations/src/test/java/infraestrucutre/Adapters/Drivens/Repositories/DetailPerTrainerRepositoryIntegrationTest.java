package infraestrucutre.Adapters.Drivens.Repositories;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.r2dbc.core.DatabaseClient;

import com.monster.server_informations.R2dbcContainerTest;

import reactor.test.StepVerifier;

@DataR2dbcTest
class DetailPerTrainerRepositoryIntegrationTest extends R2dbcContainerTest {

    @Autowired
    private DetailPerTrainerRepository detailPerTrainerRepository;

    @Autowired
    private DatabaseClient db;

    private void insertDetail(String name) {
        db.sql("INSERT INTO detail_per_trainer (name, second_name, last_name_m, last_name_p, age, weight, height) "
                        + "VALUES (:name, 'S', 'M', 'P', '30', '70', '175')")
                .bind("name", name).fetch().rowsUpdated().block();
    }

    @Test
    void countByDetailInfo_countsExactMatchesOnAllFourNameFields() {
        insertDetail("Coach-Ana");

        StepVerifier.create(detailPerTrainerRepository.countByDetailInfo("Coach-Ana", "S", "M", "P"))
                .expectNext(1)
                .verifyComplete();
    }

    @Test
    void findDetailUserByDetails_returnsTheMatchingRow() {
        insertDetail("Coach-Beto");

        StepVerifier.create(detailPerTrainerRepository.findDetailUserByDetails("Coach-Beto", "S", "M", "P"))
                .assertNext(detail -> assertThat(detail.getWeight()).isEqualTo("70"))
                .verifyComplete();
    }
}
