package domain.Entities;

import java.time.LocalDate;

public record Pool( String name, String description, LocalDate dateClean, LocalDate startDate, LocalDate endDate) {
    
}
