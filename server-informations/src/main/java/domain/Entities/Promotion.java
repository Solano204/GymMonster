package domain.Entities;

import java.time.LocalDate;

public record Promotion(  String description, String duration, int percentageDiscount, LocalDate startDate, LocalDate endDate, boolean active) {
    
}
