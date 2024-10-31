package infraestrucutre.Adapters.Drivens.Entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Persistent;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;

import lombok.Data;
import lombok.NoArgsConstructor;

//This class wont be mapped


@Data
@NoArgsConstructor
public abstract class User{
    
   
    private String username;
    private String password;
    private String email;
    
}
