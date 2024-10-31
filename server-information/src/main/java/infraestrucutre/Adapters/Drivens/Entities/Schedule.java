package infraestrucutre.Adapters.Drivens.Entities;

import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import lombok.*;

@Data
@Table("schedules")
@AllArgsConstructor
@NoArgsConstructor
public class  Schedule {

  @Id
  // @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  private String day;

  private String startTime;

  private String endTime;


}
