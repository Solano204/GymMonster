package infraestrucutre.Adapters.Drivens.Repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.r2dbc.core.DatabaseClient;

import com.monster.server_register.R2dbcContainerTest;

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
        insertDetail("RegAna");

        StepVerifier.create(detailClientRepository.countByDetailInfo("RegAna", "Q", "Doe", "Public"))
                .expectNext(1)
                .verifyComplete();
    }

    @Test
    void countByDetailInfo_returnsZero_whenAnyFieldDiffers() {
        insertDetail("RegAna");

        StepVerifier.create(detailClientRepository.countByDetailInfo("RegAna", "Q", "Doe", "Private"))
                .expectNext(0)
                .verifyComplete();
    }

    @Test
    void findDetailUserByDetails_failsBecauseTheQueryUsesJpqlSyntaxNotSql() {
        // findDetailUserByDetails' @Query ("SELECT d FROM DetailUser d WHERE d.name = ...") is
        // written in JPQL entity-name syntax, not SQL - R2DBC has no JPQL translator, so this
        // throws against a real database rather than returning a row. countByDetailInfo above
        // uses real SQL and works; this method is the broken sibling.
        insertDetail("RegBeto");

        org.assertj.core.api.Assertions.assertThatThrownBy(() ->
                        detailClientRepository.findDetailUserByDetails("RegBeto", "Q", "Doe", "Public").block())
                .isInstanceOf(Exception.class);
    }
}
