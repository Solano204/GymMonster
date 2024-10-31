package infraestrucutre.Adapters.Drivens.Entities;

import java.time.LocalDate;

import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.*;

@Table("Inscription")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Inscription {
    

    private Integer id;
    @Column("id_client")
    private Integer id_client;
    @Column("date_inscription")
    private LocalDate startDate;
    @Column("start_month")
    private LocalDate startInscription;
    @Column("end_month")
    private LocalDate endInscription;
    @Column("price")
    private Double price;
}
