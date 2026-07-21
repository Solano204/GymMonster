package infraestrucutre.Adapters.Drivens.Repositories;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.r2dbc.core.DatabaseClient;

import com.monster.server_informations.R2dbcContainerTest;

import reactor.test.StepVerifier;

@DataR2dbcTest
class DetailTrainerClassRepositoryIntegrationTest extends R2dbcContainerTest {

    @Autowired
    private DetailTrainerClassRepository detailTrainerClassRepository;

    @Autowired
    private DatabaseClient db;

    private void insertDetail(String name) {
        db.sql("INSERT INTO detail_class_trainer (name, second_name, last_name_m, last_name_p, age, weight, height) "
                        + "VALUES (:name, 'S', 'M', 'P', '30', '70', '175')")
                .bind("name", name).fetch().rowsUpdated().block();
    }

    @Test
    void countByDetailInfo_countsExactMatchesOnAllFourNameFields() {
        insertDetail("ClassCoach-Ana");

        StepVerifier.create(detailTrainerClassRepository.countByDetailInfo("ClassCoach-Ana", "S", "M", "P"))
                .expectNext(1)
                .verifyComplete();
    }

    @Test
    void findDetailUserByDetails_returnsTheMatchingRow() {
        insertDetail("ClassCoach-Beto");

        StepVerifier.create(detailTrainerClassRepository.findDetailUserByDetails("ClassCoach-Beto", "S", "M", "P"))
                .assertNext(detail -> assertThat(detail.getHeight()).isEqualTo("175"))
                .verifyComplete();
    }
}
