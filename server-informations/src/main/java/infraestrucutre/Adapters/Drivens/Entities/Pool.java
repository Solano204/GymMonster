package infraestrucutre.Adapters.Drivens.Entities;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import lombok.Data;

@Data
@Table(name = "pool")
public class Pool  {

  @Id
  // @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  private String name;
  private String description;
  private LocalDate dateClean;
  private LocalDate startDate;
  private LocalDate endDate;
}
