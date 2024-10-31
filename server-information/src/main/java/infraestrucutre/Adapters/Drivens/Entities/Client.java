package infraestrucutre.Adapters.Drivens.Entities;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("clients")
public class Client extends User {
    
    @Id
    @Column("id")
    
    private Long id;
    private Long id_detail;
    private Long id_trainer;
    private Long id_membership;
// Several client will has a membership (one a membership)
    //@ManyToOne
   // @JoinColumn(name = "id_membership", nullable = false)
        
}
