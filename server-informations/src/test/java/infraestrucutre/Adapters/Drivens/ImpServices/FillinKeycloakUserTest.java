package infraestrucutre.Adapters.Drivens.ImpServices;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import infraestrucutre.Adapters.Drivens.DTOS.DtoDataReciving;
import infraestrucutre.Adapters.Drivens.DTOS.DtoKeyCloakUser;
import infraestrucutre.Adapters.Drivens.Entities.AllClient;
import infraestrucutre.Adapters.Drivens.Entities.AllTrainer;

class FillinKeycloakUserTest {

    private final FillinKeycloakUser mapper = new FillinKeycloakUser();

    @Test
    void fillinKeycloakUser_mapsUsernameEmailAndPasswordDirectly() {
        DtoDataReciving data = new DtoDataReciving("jdoe", "jdoe@test.com", "s3cret",
                "John", "Q", "Doe", "Public", "30", "180", "80");

        DtoKeyCloakUser keycloakUser = mapper.fillinKeycloakUser(data);

        assertThat(keycloakUser.getUsername()).isEqualTo("jdoe");
        assertThat(keycloakUser.getEmail()).isEqualTo("jdoe@test.com");
        assertThat(keycloakUser.getPassword()).isEqualTo("s3cret");
    }

    @Test
    void fillinKeycloakUser_mapsFirstNameFromDataNameField() {
        DtoDataReciving data = new DtoDataReciving("jdoe", "jdoe@test.com", "s3cret",
                "John", "Q", "Doe", "Public", "30", "180", "80");

        DtoKeyCloakUser keycloakUser = mapper.fillinKeycloakUser(data);

        assertThat(keycloakUser.getFirstName()).isEqualTo("John");
    }

    @Test
    void fillinKeycloakUser_mapsLastNameFromDataLastNameMField_notLastNameP() {
        // DtoKeyCloakUser only has one "lastName" field - this pins that it comes from
        // lastNameM (madre/mother's surname), not lastNameP (padre/father's surname), which
        // is an easy field-order mixup given both are adjacent String params in the constructor.
        DtoDataReciving data = new DtoDataReciving("jdoe", "jdoe@test.com", "s3cret",
                "John", "Q", "DoeP", "DoeM", "30", "180", "80");

        DtoKeyCloakUser keycloakUser = mapper.fillinKeycloakUser(data);

        assertThat(keycloakUser.getLastName()).isEqualTo("DoeM");
    }

    @Test
    void fillinDataFromClient_mapsEveryFieldFromAllClientRecord() {
        AllClient client = new AllClient(1L, "jdoe", "pw", "jdoe@test.com", "coach99", "John",
                "Q", "Doe", "Public", "30", "180", "80", "GOLD", LocalDate.now(), LocalDate.now(),
                LocalDate.now().plusYears(1), 49.99);

        DtoDataReciving data = mapper.fillinDataFromClient(client);

        assertThat(data.username()).isEqualTo("jdoe");
        assertThat(data.email()).isEqualTo("jdoe@test.com");
        assertThat(data.password()).isEqualTo("pw");
        assertThat(data.name()).isEqualTo("John");
        assertThat(data.secondName()).isEqualTo("Q");
        assertThat(data.lastNameP()).isEqualTo("Doe");
        assertThat(data.lastNameM()).isEqualTo("Public");
        assertThat(data.age()).isEqualTo("30");
        assertThat(data.height()).isEqualTo("180");
        assertThat(data.weight()).isEqualTo("80");
    }

    @Test
    void fillinDataFromTrainer_mapsEveryFieldFromAllTrainerRecord() {
        AllTrainer trainer = new AllTrainer(1L, "coach99", "pw", "coach@test.com", "Ana",
                "M", "Lopez", "Diaz", "35", "170", "65", LocalDate.now());

        DtoDataReciving data = mapper.fillinDataFromTrainer(trainer);

        assertThat(data.username()).isEqualTo("coach99");
        assertThat(data.email()).isEqualTo("coach@test.com");
        assertThat(data.password()).isEqualTo("pw");
        assertThat(data.name()).isEqualTo("Ana");
        assertThat(data.secondName()).isEqualTo("M");
        assertThat(data.lastNameP()).isEqualTo("Lopez");
        assertThat(data.lastNameM()).isEqualTo("Diaz");
    }
}
