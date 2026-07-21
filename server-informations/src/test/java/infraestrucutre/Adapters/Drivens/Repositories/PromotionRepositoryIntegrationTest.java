package infraestrucutre.Adapters.Drivens.Repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.r2dbc.core.DatabaseClient;

import com.monster.server_informations.R2dbcContainerTest;

import reactor.test.StepVerifier;

@DataR2dbcTest
class PromotionRepositoryIntegrationTest extends R2dbcContainerTest {

    @Autowired
    private PromotionRepository promotionRepository;

    @Autowired
    private DatabaseClient db;

    private void insertPromotion(String description, LocalDate start, LocalDate end) {
        db.sql("INSERT INTO promotion (description, duration, percentage_discount, start_date, end_date, active) "
                        + "VALUES (:desc, '1 month', 20, :start, :end, true)")
                .bind("desc", description).bind("start", start).bind("end", end)
                .fetch().rowsUpdated().block();
    }

    @Test
    void findByStartDate_returnsPromotionsStartingOnThatDate() {
        LocalDate start = LocalDate.of(2026, 6, 1);
        insertPromotion("Summer-P", start, start.plusMonths(1));

        StepVerifier.create(promotionRepository.findByStartDate(start))
                .assertNext(promo -> assertThat(promo.getDescription()).isEqualTo("Summer-P"))
                .verifyComplete();
    }

    @Test
    void findByEndDate_returnsPromotionsEndingOnThatDate() {
        LocalDate start = LocalDate.of(2026, 7, 1);
        LocalDate end = start.plusMonths(1);
        insertPromotion("Winter-P", start, end);

        StepVerifier.create(promotionRepository.findByEndDate(end))
                .assertNext(promo -> assertThat(promo.getDescription()).isEqualTo("Winter-P"))
                .verifyComplete();
    }

    @Test
    void deleteByStartDate_removesMatchingRows() {
        LocalDate start = LocalDate.of(2026, 8, 1);
        insertPromotion("Autumn-P", start, start.plusMonths(1));

        StepVerifier.create(promotionRepository.deleteByStartDate(start)).verifyComplete();
        StepVerifier.create(promotionRepository.findByStartDate(start)).verifyComplete();
    }

    @Test
    void deleteByEndDate_removesMatchingRows() {
        LocalDate start = LocalDate.of(2026, 9, 1);
        LocalDate end = start.plusMonths(1);
        insertPromotion("Spring-P", start, end);

        StepVerifier.create(promotionRepository.deleteByEndDate(end)).verifyComplete();
        StepVerifier.create(promotionRepository.findByEndDate(end)).verifyComplete();
    }
}
