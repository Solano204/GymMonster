package infraestrucutre.Adapters.Drivens.Entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;

import java.util.*;

import lombok.Data;

    @Data
    @Table(name = "detail_user")
    public class DetailUser {
        
        @Id
        private Long id;

        @Column("name")
        private String name;
        @Column("second_name")
        private String secondName;
        @Column("last_name_m")
        private String lastNameM;
        @Column("last_name_p")
        private String lastNameP;
        private String age;
        private String weight;
        private String height;
        
        // This will be the father of the relationshio becase in the other table is the property mapped
        //@OneToOne
        //@JoinColumn(name = "id_user", nullable = false)           
        
    }
