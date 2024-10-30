package infraestrucutre.Adapters.Drivens.Entities;


import java.time.LocalDate;


import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import lombok.*;


@Data
@Table("equipment")
@AllArgsConstructor
@NoArgsConstructor
public class Equipament {

    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description; 
    @Column("start_date")
    private LocalDate startDate;
    @Column("end_date")
    private LocalDate endDate;
    private String  ageStatus;       
}
