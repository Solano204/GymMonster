package infraestrucutre.Adapters.Drivens.DTOS;
import lombok.*;

import java.io.Serializable;
import java.util.Set;


@Data
public class DtoKeyCloakUser implements Serializable {

    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private Set<String> roles;
}
