package infraestrucutre.Adapters.Drivens.Repositories;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.r2dbc.core.DatabaseClient;

import com.monster.server_informations.R2dbcContainerTest;

import reactor.test.StepVerifier;

@DataR2dbcTest
class DetailClientRepositoryIntegrationTest extends R2dbcContainerTest {

    @Autowired
    private DetailClientRepository detailClientRepository;

    @Autowired
    private DatabaseClient db;

    private void insertDetail(String name) {
        db.sql("INSERT INTO detail_user (name, second_name, last_name_m, last_name_p, age, weight, height) "
                        + "VALUES (:name, 'Q', 'Doe', 'Public', '25', '60', '165')")
                .bind("name", name).fetch().rowsUpdated().block();
    }

    @Test
    void countByDetailInfo_countsExactMatchesOnAllFourNameFields() {
        insertDetail("Ana");

        StepVerifier.create(detailClientRepository.countByDetailInfo("Ana", "Q", "Doe", "Public"))
                .expectNext(1)
                .verifyComplete();
    }

    @Test
    void countByDetailInfo_returnsZero_whenAnyFieldDiffers() {
        insertDetail("Ana");

        StepVerifier.create(detailClientRepository.countByDetailInfo("Ana", "Q", "Doe", "Private"))
                .expectNext(0)
                .verifyComplete();
    }

    @Test
    void findDetailUserByDetails_returnsTheMatchingRow() {
        insertDetail("Beto");

        StepVerifier.create(detailClientRepository.findDetailUserByDetails("Beto", "Q", "Doe", "Public"))
                .assertNext(detail -> assertThat(detail.getAge()).isEqualTo("25"))
                .verifyComplete();
    }
}
