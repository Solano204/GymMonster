package infraestrucutre.Adapters.Drivens.Entities;

import java.time.LocalDate;

import lombok.Data;

public record Equipament(
        Long id,
        String name,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        String ageStatus) {

}
