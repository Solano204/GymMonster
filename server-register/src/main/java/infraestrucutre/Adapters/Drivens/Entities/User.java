package infraestrucutre.Adapters.Drivens.Entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Persistent;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;

import lombok.Data;

//This class wont be mapped


@Data
public abstract class User{
    
    @Id
    private Long id;
    private String username;
    private String password;
    private String email;
    
}
