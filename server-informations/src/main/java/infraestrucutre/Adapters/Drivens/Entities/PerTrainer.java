package infraestrucutre.Adapters.Drivens.Entities;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.repository.query.Param;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Flux;
import lombok.Data;

@Table("per_trainer")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PerTrainer extends User {

    @Column("id")
    private Long id;

   // @OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL, orphanRemoval = true)
   // @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    /*@JoinTable(
        name = "pertrainer_specialty",
        joinColumns = @JoinColumn(name = "pertrainer_id"),
        inverseJoinColumns = @JoinColumn(name = "specialty_id")
    )*/
    @Transient
    private List<Specialty> specialities;

    @Column("start_date")
    private LocalDate startDate;

    @Column("id_detail")
    private Long id_detail;

    
}
