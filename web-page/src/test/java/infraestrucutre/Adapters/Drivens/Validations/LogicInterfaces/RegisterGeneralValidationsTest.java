package infraestrucutre.Adapters.Drivens.Validations.LogicInterfaces;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import application.Ports.Drivens.InterfaceRepositories.ClientRepositoryInterface;
import infraestrucutre.Adapters.Drivens.Entities.AllClient;

@ExtendWith(MockitoExtension.class)
class RegisterGeneralValidationsTest {

    @Mock
    private ClientRepositoryInterface clientRepository;

    private RegisterGeneralValidations validations() {
        return new RegisterGeneralValidations(clientRepository);
    }

    private AllClient validClient() {
        return new AllClient(null, "jdoe", "S3cur3P@ss!", "jdoe@test.com", "coach99", "John", "Q", "Doe", "Public",
                "30", "180", "80", "GOLD", LocalDate.now(), LocalDate.now(), LocalDate.now().plusYears(1), 49.99);
    }

    @Test
    void validateBasicFields_passesForACompletelyValidClient() {
        List<String> errors = new ArrayList<>();

        boolean result = validations().validateBasicFields(validClient(), errors);

        assertThat(result).isTrue();
        assertThat(errors).isEmpty();
    }

    @Test
    void validateBasicFields_rejectsEmptyMembershipType() {
        AllClient client = new AllClient(null, "jdoe", "pw", "jdoe@test.com", "coach99", "John", "Q", "Doe", "Public",
                "30", "180", "80", "", LocalDate.now(), LocalDate.now(), LocalDate.now().plusYears(1), 49.99);
        List<String> errors = new ArrayList<>();

        boolean result = validations().validateBasicFields(client, errors);

        assertThat(result).isFalse();
        assertThat(errors).contains("The type of membership cannot be empty");
    }

    @Test
    void validateBasicFields_rejectsBlankTrainerName_butAllowsNullTrainerName() {
        AllClient blankTrainer = new AllClient(null, "jdoe", "pw", "jdoe@test.com", "  ", "John", "Q", "Doe", "Public",
                "30", "180", "80", "GOLD", LocalDate.now(), LocalDate.now(), LocalDate.now().plusYears(1), 49.99);
        List<String> errors = new ArrayList<>();
        validations().validateBasicFields(blankTrainer, errors);
        assertThat(errors).contains("The trainer's name cannot be blank");

        AllClient nullTrainer = new AllClient(null, "jdoe", "pw", "jdoe@test.com", null, "John", "Q", "Doe", "Public",
                "30", "180", "80", "GOLD", LocalDate.now(), LocalDate.now(), LocalDate.now().plusYears(1), 49.99);
        List<String> noTrainerErrors = new ArrayList<>();
        validations().validateBasicFields(nullTrainer, noTrainerErrors);
        assertThat(noTrainerErrors).doesNotContain("The trainer's name cannot be blank");
    }

    @Test
    void validateBasicFields_rejectsEmptyFirstAndLastNames() {
        AllClient client = new AllClient(null, "jdoe", "pw", "jdoe@test.com", "coach99", "", "Q", "", "",
                "30", "180", "80", "GOLD", LocalDate.now(), LocalDate.now(), LocalDate.now().plusYears(1), 49.99);
        List<String> errors = new ArrayList<>();

        validations().validateBasicFields(client, errors);

        assertThat(errors).contains(
                "The first name cannot be empty",
                "The paternal last name cannot be empty",
                "The maternal last name cannot be empty");
    }

    @Test
    void validateBasicFields_rejectsNonNumericOrZeroAge() {
        AllClient nonNumeric = new AllClient(null, "jdoe", "pw", "jdoe@test.com", "coach99", "John", "Q", "Doe", "Public",
                "abc", "180", "80", "GOLD", LocalDate.now(), LocalDate.now(), LocalDate.now().plusYears(1), 49.99);
        List<String> errors = new ArrayList<>();
        validations().validateBasicFields(nonNumeric, errors);
        assertThat(errors).contains("The age must be a valid positive number");

        AllClient zeroAge = new AllClient(null, "jdoe", "pw", "jdoe@test.com", "coach99", "John", "Q", "Doe", "Public",
                "0", "180", "80", "GOLD", LocalDate.now(), LocalDate.now(), LocalDate.now().plusYears(1), 49.99);
        List<String> zeroErrors = new ArrayList<>();
        validations().validateBasicFields(zeroAge, zeroErrors);
        assertThat(zeroErrors).contains("The age must be a valid positive number");
    }

    @Test
    void validateBasicFields_rejectsInvalidHeightAndWeight() {
        AllClient client = new AllClient(null, "jdoe", "pw", "jdoe@test.com", "coach99", "John", "Q", "Doe", "Public",
                "30", "-5", "0", "GOLD", LocalDate.now(), LocalDate.now(), LocalDate.now().plusYears(1), 49.99);
        List<String> errors = new ArrayList<>();

        validations().validateBasicFields(client, errors);

        assertThat(errors).contains("The height must be a positive number", "The weight must be a positive number");
    }

    @Test
    void validateBasicFields_acceptsDecimalHeightAndWeight() {
        AllClient client = new AllClient(null, "jdoe", "pw", "jdoe@test.com", "coach99", "John", "Q", "Doe", "Public",
                "30", "180.5", "80.25", "GOLD", LocalDate.now(), LocalDate.now(), LocalDate.now().plusYears(1), 49.99);
        List<String> errors = new ArrayList<>();

        boolean result = validations().validateBasicFields(client, errors);

        assertThat(result).isTrue();
    }

    @Test
    void validateFields_delegatesToValidateBasicFieldsWithAFreshErrorList() {
        assertThat(validations().validateFields(validClient())).isTrue();
    }
}
