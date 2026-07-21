package infraestrucutre.Adapters.Drivens.DTOS;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class UserNotFoundExceptionTest {

    @Test
    void constructor_preservesMessage() {
        UserNotFoundException exception = new UserNotFoundException("Client jdoe not found");

        assertThat(exception.getMessage()).isEqualTo("Client jdoe not found");
    }

    @Test
    void isARuntimeException() {
        UserNotFoundException exception = new UserNotFoundException("boom");

        assertThat(exception).isInstanceOf(RuntimeException.class);
    }
}
