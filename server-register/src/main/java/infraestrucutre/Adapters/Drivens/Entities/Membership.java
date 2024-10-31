package infraestrucutre.Adapters.Drivens.Entities;

import lombok.*;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;

    @Table(name = "membership")
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class Membership {

        @Id
        private Long id;

        @Column(value = "membership_type")
        private String membershipType;

        private String description;
        private boolean hasCardio;
        private boolean hasPool;
        
        private boolean hasFoodCourt;

        
    }