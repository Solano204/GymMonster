package infraestrucutre.Adapters.Drivens.Entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Transient;

import lombok.Data;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("trainers_class")
public class ClassTrainer extends User {

        @Id
        @Column("id")
        private Long id;


    // Many-to-many relationship between ClassTrainer and Specialty
   /* @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
        name = "classtrainer_specialty",
        joinColumns = @JoinColumn(name = "classtrainer_id"),
        inverseJoinColumns = @JoinColumn(name = "specialty_id")
    )*/

    @Transient
    private List<Specialty> specialties;


    @Column("start_date")
    private LocalDate startDate;

    @Column("id_detail")
    private Long id_detail;

}
