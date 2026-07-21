package infraestrucutre.Adapters.Drivens.Repositories;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.r2dbc.core.DatabaseClient;

import com.monster.server_informations.R2dbcContainerTest;

import reactor.test.StepVerifier;

@DataR2dbcTest
class PoolRepositoryIntegrationTest extends R2dbcContainerTest {

    @Autowired
    private PoolRepository poolRepository;

    @Autowired
    private DatabaseClient db;

    private void insertPool(String name) {
        db.sql("INSERT INTO pool (name, description, date_clean, start_date, end_date) "
                        + "VALUES (:name, 'desc', '2026-01-01', '2026-01-01', '2026-12-31')")
                .bind("name", name).fetch().rowsUpdated().block();
    }

    @Test
    void findByName_returnsTheMatchingPool() {
        insertPool("Olympic-P");

        StepVerifier.create(poolRepository.findByName("Olympic-P"))
                .assertNext(pool -> assertThat(pool.getDescription()).isEqualTo("desc"))
                .verifyComplete();
    }

    @Test
    void findById_returnsTheMatchingPool() {
        insertPool("Kids-P");
        Integer id = db.sql("SELECT id FROM pool WHERE name = 'Kids-P'")
                .map(row -> row.get("id", Integer.class)).one().block();

        StepVerifier.create(poolRepository.findById(Long.valueOf(id)))
                .assertNext(pool -> assertThat(pool.getName()).isEqualTo("Kids-P"))
                .verifyComplete();
    }

    @Test
    void deleteByName_removesTheRow() {
        insertPool("Temp-P");

        StepVerifier.create(poolRepository.deleteByName("Temp-P")).verifyComplete();
        StepVerifier.create(poolRepository.findByName("Temp-P")).verifyComplete();
    }
}
