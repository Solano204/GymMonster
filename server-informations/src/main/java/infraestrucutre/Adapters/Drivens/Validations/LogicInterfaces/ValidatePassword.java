package infraestrucutre.Adapters.Drivens.Validations.LogicInterfaces;

import org.springframework.security.authentication.password.CompromisedPasswordDecision;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiReactivePasswordChecker;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import reactor.core.publisher.Mono;

@Component
@Data
public class ValidatePassword {

    private final HaveIBeenPwnedRestApiReactivePasswordChecker passwordChecker;

    public Mono<Boolean> validatePasswordRegister(String password) {
        // Web: https://haveibeenpwned.com/Passwords
        Mono<CompromisedPasswordDecision> validator = passwordChecker.check(password);

        return validator.flatMap(decition -> {
            if (decition.isCompromised()) {
                return Mono.just(true); // If the password is insecure
            }
            return Mono.just(false);
        });
    }
}
