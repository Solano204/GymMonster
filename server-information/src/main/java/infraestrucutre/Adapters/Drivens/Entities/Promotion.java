package infraestrucutre.Adapters.Drivens.Entities;

import java.time.LocalDate;

import org.springframework.cglib.core.Local;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import lombok.Data;

@Data
@Table(name = "promotion")
public class Promotion  {
    
    @Id
 //   @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String description;
    private String duration;
    private int percentageDiscount;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean active;

    
    @Transient
	private boolean nuevo;
	

}
