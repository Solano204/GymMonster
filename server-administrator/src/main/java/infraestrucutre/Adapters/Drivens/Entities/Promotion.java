package infraestrucutre.Adapters.Drivens.Entities;

import java.time.LocalDate;

import org.springframework.cglib.core.Local;

import lombok.Data;

public record Promotion(
        Integer id,
        String description,
        String duration,
        int percentageDiscount,
        LocalDate startDate,
        LocalDate endDate,
        boolean active) {

}
