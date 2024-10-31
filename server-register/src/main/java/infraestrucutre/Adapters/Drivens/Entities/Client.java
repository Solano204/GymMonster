package infraestrucutre.Adapters.Drivens.Entities;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.annotation.Transient;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;
import java.util.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("clients")
public class Client extends User {
    
    @Column("id_detail")
    private Long id_detail;
    
    @Column("id_trainer")
    private Long id_trainer;
    @Column("id_membership")
    private Long id_membership;
    
}
