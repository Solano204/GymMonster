package infraestrucutre.Adapters.Drivens.DTOS;


// HERE THERE ARE 4 FOUR EXCEPTIONS 
// THE USER NOT FOUND IS THE FIRST ONE
// THE USER ALREADY EXISTS IS THE SECOND ONE
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
