package infraestrucutre.Adapters.Drivens.Entities;

import java.util.List;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table("work_class")
public class WorkClass {
    @Id
   //  @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private String duration;
   // @ManyToMany
   /*  @JoinTable(name = "workclass_schedule", // Intermediate table
            joinColumns = @JoinColumn(name = "workclass_id"), // FK to workclass
            inverseJoinColumns = @JoinColumn(name = "schedule_id") // FK to schedule
    )*/
    
       
    
}
