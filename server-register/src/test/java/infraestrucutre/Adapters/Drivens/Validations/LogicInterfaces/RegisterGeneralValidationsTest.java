package infraestrucutre.Adapters.Drivens.Validations.LogicInterfaces;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import infraestrucutre.Adapters.Drivens.DTOS.DtoDataReciving;

class RegisterGeneralValidationsTest {

    private final RegisterGeneralValidations validations = new RegisterGeneralValidations();

    private DtoDataReciving validUser() {
        return new DtoDataReciving(
                "jdoe", "jdoe@example.com", "Password1!",
                "John", "Michael", "Doe", "Smith",
                "30", "180.5", "75.2");
    }

    @Test
    void validateFields_returnsTrue_whenAllFieldsAreValid() {
        assertThat(validations.validateFields(validUser())).isTrue();
    }

    @Test
    void validateBasicFields_reportsError_whenNameIsBlank() {
        DtoDataReciving user = withName(validUser(), "");
        List<String> errors = new ArrayList<>();

        boolean result = validations.validateBasicFields(user, errors);

        assertThat(result).isFalse();
        assertThat(errors).contains("The first name cannot be empty");
    }

    @Test
    void validateBasicFields_reportsError_whenNameIsNull() {
        DtoDataReciving user = withName(validUser(), null);
        List<String> errors = new ArrayList<>();

        assertThat(validations.validateBasicFields(user, errors)).isFalse();
        assertThat(errors).contains("The first name cannot be empty");
    }

    @Test
    void validateBasicFields_reportsError_whenLastNamePIsBlank() {
        DtoDataReciving user = withLastNameP(validUser(), "");
        List<String> errors = new ArrayList<>();

        assertThat(validations.validateBasicFields(user, errors)).isFalse();
        assertThat(errors).contains("The paternal last name cannot be empty");
    }

    @Test
    void validateBasicFields_reportsError_whenLastNameMIsBlank() {
        DtoDataReciving user = withLastNameM(validUser(), "");
        List<String> errors = new ArrayList<>();

        assertThat(validations.validateBasicFields(user, errors)).isFalse();
        assertThat(errors).contains("The maternal last name cannot be empty");
    }

    @Test
    void validateBasicFields_reportsError_whenAgeIsNotNumeric() {
        DtoDataReciving user = withAge(validUser(), "thirty");
        List<String> errors = new ArrayList<>();

        assertThat(validations.validateBasicFields(user, errors)).isFalse();
        assertThat(errors).contains("The age must be a valid positive number");
    }

    @Test
    void validateBasicFields_reportsError_whenAgeIsZero() {
        DtoDataReciving user = withAge(validUser(), "0");
        List<String> errors = new ArrayList<>();

        assertThat(validations.validateBasicFields(user, errors)).isFalse();
        assertThat(errors).contains("The age must be a valid positive number");
    }

    @Test
    void validateBasicFields_reportsError_whenAgeIsNegative() {
        // NOTE: current regex "\\d+" never matches a leading '-', so this is
        // already rejected by the pattern check before the Integer.parseInt.
        DtoDataReciving user = withAge(validUser(), "-5");
        List<String> errors = new ArrayList<>();

        assertThat(validations.validateBasicFields(user, errors)).isFalse();
        assertThat(errors).contains("The age must be a valid positive number");
    }

    @Test
    void validateBasicFields_reportsError_whenHeightIsNotNumeric() {
        DtoDataReciving user = withHeight(validUser(), "tall");
        List<String> errors = new ArrayList<>();

        assertThat(validations.validateBasicFields(user, errors)).isFalse();
        assertThat(errors).contains("The height must be a positive number");
    }

    @Test
    void validateBasicFields_reportsError_whenHeightIsZero() {
        DtoDataReciving user = withHeight(validUser(), "0");
        List<String> errors = new ArrayList<>();

        assertThat(validations.validateBasicFields(user, errors)).isFalse();
        assertThat(errors).contains("The height must be a positive number");
    }

    @Test
    void validateBasicFields_reportsError_whenWeightIsNotNumeric() {
        DtoDataReciving user = withWeight(validUser(), "heavy");
        List<String> errors = new ArrayList<>();

        assertThat(validations.validateBasicFields(user, errors)).isFalse();
        assertThat(errors).contains("The weight must be a positive number");
    }

    @Test
    void validateBasicFields_reportsError_whenWeightIsZero() {
        DtoDataReciving user = withWeight(validUser(), "0");
        List<String> errors = new ArrayList<>();

        assertThat(validations.validateBasicFields(user, errors)).isFalse();
        assertThat(errors).contains("The weight must be a positive number");
    }

    @Test
    void validateBasicFields_reportsAllErrors_whenEverythingIsInvalid() {
        DtoDataReciving user = new DtoDataReciving(
                "jdoe", "jdoe@example.com", "Password1!",
                "", "Michael", "", "",
                "abc", "abc", "abc");
        List<String> errors = new ArrayList<>();

        assertThat(validations.validateBasicFields(user, errors)).isFalse();
        assertThat(errors).containsExactlyInAnyOrder(
                "The first name cannot be empty",
                "The paternal last name cannot be empty",
                "The maternal last name cannot be empty",
                "The age must be a valid positive number",
                "The height must be a positive number",
                "The weight must be a positive number");
    }

    private static DtoDataReciving withName(DtoDataReciving u, String name) {
        return new DtoDataReciving(u.username(), u.email(), u.password(), name,
                u.secondName(), u.lastNameP(), u.lastNameM(), u.age(), u.height(), u.weight());
    }

    private static DtoDataReciving withLastNameP(DtoDataReciving u, String lastNameP) {
        return new DtoDataReciving(u.username(), u.email(), u.password(), u.name(),
                u.secondName(), lastNameP, u.lastNameM(), u.age(), u.height(), u.weight());
    }

    private static DtoDataReciving withLastNameM(DtoDataReciving u, String lastNameM) {
        return new DtoDataReciving(u.username(), u.email(), u.password(), u.name(),
                u.secondName(), u.lastNameP(), lastNameM, u.age(), u.height(), u.weight());
    }

    private static DtoDataReciving withAge(DtoDataReciving u, String age) {
        return new DtoDataReciving(u.username(), u.email(), u.password(), u.name(),
                u.secondName(), u.lastNameP(), u.lastNameM(), age, u.height(), u.weight());
    }

    private static DtoDataReciving withHeight(DtoDataReciving u, String height) {
        return new DtoDataReciving(u.username(), u.email(), u.password(), u.name(),
                u.secondName(), u.lastNameP(), u.lastNameM(), u.age(), height, u.weight());
    }

    private static DtoDataReciving withWeight(DtoDataReciving u, String weight) {
        return new DtoDataReciving(u.username(), u.email(), u.password(), u.name(),
                u.secondName(), u.lastNameP(), u.lastNameM(), u.age(), u.height(), weight);
    }
}
