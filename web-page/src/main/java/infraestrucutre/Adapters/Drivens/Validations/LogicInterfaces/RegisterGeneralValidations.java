package infraestrucutre.Adapters.Drivens.Validations.LogicInterfaces;

import org.springframework.stereotype.Component;

import application.Ports.Drivens.InterfaceRepositories.ClientRepositoryInterface;
import infraestrucutre.Adapters.Drivens.Entities.AllClient;
import infraestrucutre.Adapters.Drivens.Repositories.ClientRepository;
import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Mono;
import java.util.ArrayList;
import java.util.List;

@Data
@Component
public class RegisterGeneralValidations {

    private final ClientRepositoryInterface clientRepository;

    public Boolean validateFields(AllClient newUser) {
        List<String> errorMessages = new ArrayList<>();

        // Validate basic fields and collect errors
        return validateBasicFields(newUser, errorMessages);
    }

    // Separate method for validating basic fields
    public boolean validateBasicFields(AllClient newUser,
            List<String> errorMessages) {
        // Validate typeMembership
        if (newUser.membershiptype() == null || newUser.membershiptype().isEmpty()) {
            errorMessages.add("The type of membership cannot be empty");
        }

        // Validate trainerName (if applicable)
        if (newUser.trainername() != null && newUser.trainername().isBlank()) {
            errorMessages.add("The trainer's name cannot be blank");
        }

        // Validate name
        if (newUser.name() == null || newUser.name().isEmpty()) {
            errorMessages.add("The first name cannot be empty");
        }

        // Validate lastNameP and lastNameM
        if (newUser.lastnamep() == null || newUser.lastnamep().isEmpty()) {
            errorMessages.add("The paternal last name cannot be empty");
        }
        if (newUser.lastnamem() == null || newUser.lastnamem().isEmpty()) {
            errorMessages.add("The maternal last name cannot be empty");
        }

        // Validate age
        if (newUser.age() == null || !newUser.age().matches("\\d+") || Integer.parseInt(newUser.age()) <= 0) {
            errorMessages.add("The age must be a valid positive number");
        }

        // Validate height
        if (newUser.height() == null || !newUser.height().matches("\\d+(\\.\\d+)?")
                || Double.parseDouble(newUser.height()) <= 0) {
            errorMessages.add("The height must be a positive number");
        }

        // Validate weight
        if (newUser.weight() == null || !newUser.weight().matches("\\d+(\\.\\d+)?")
                || Double.parseDouble(newUser.weight()) <= 0) {
            errorMessages.add("The weight must be a positive number");
        }
        return errorMessages.isEmpty();
    }

}
