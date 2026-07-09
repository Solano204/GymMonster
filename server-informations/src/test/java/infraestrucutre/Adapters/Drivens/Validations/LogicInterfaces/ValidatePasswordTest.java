package infraestrucutre.Adapters.Drivens.Validations.LogicInterfaces;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.password.CompromisedPasswordDecision;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiReactivePasswordChecker;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class ValidatePasswordTest {

    @Mock
    private HaveIBeenPwnedRestApiReactivePasswordChecker passwordChecker;

    @Mock
    private CompromisedPasswordDecision decision;

    @Test
    void validatePasswordRegister_returnsTrue_whenPasswordIsCompromised() {
        when(passwordChecker.check("password123")).thenReturn(Mono.just(decision));
        when(decision.isCompromised()).thenReturn(true);

        ValidatePassword validatePassword = new ValidatePassword(passwordChecker);

        StepVerifier.create(validatePassword.validatePasswordRegister("password123"))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void validatePasswordRegister_returnsFalse_whenPasswordIsNotCompromised() {
        when(passwordChecker.check("Xk9#mQ2vL7pZ")).thenReturn(Mono.just(decision));
        when(decision.isCompromised()).thenReturn(false);

        ValidatePassword validatePassword = new ValidatePassword(passwordChecker);

        StepVerifier.create(validatePassword.validatePasswordRegister("Xk9#mQ2vL7pZ"))
                .expectNext(false)
                .verifyComplete();
    }
}
