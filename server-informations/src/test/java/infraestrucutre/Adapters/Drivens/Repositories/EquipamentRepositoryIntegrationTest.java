package infraestrucutre.Adapters.Drivens.Repositories;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;

import com.monster.server_informations.R2dbcContainerTest;

import infraestrucutre.Adapters.Drivens.Entities.Equipament;

/**
 * EquipamentRepository maps to @Table("equipment") (see the Entities/Equipament.java class),
 * but the real schema (init.sql at the repo root, mirrored into src/test/resources here) only
 * creates a table named "equipament" - not "equipment". Every method on this repository is
 * therefore expected to fail against the real database with a "table doesn't exist" error; this
 * test documents that as the CURRENT (broken) behavior rather than silently working around it by
 * renaming the test schema's table to match the entity.
 */
@DataR2dbcTest
class EquipamentRepositoryIntegrationTest extends R2dbcContainerTest {

    @Autowired
    private EquipamentRepository equipamentRepository;

    @Test
    void findByName_failsBecauseTheEntityTableNameDoesNotMatchTheRealSchema() {
        assertThatThrownBy(() -> equipamentRepository.findByName("Treadmill").block())
                .hasMessageContaining("equipment");
    }

    @Test
    void save_failsBecauseTheEntityTableNameDoesNotMatchTheRealSchema() {
        Equipament equipament = new Equipament(null, "Treadmill", "desc", LocalDate.now(), LocalDate.now().plusYears(5), "NEW");

        assertThatThrownBy(() -> equipamentRepository.save(equipament).block())
                .hasMessageContaining("equipment");
    }
}
