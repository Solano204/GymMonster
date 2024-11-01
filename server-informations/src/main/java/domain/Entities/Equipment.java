package domain.Entities;
import java.time.LocalDate;

public record Equipment( 
    String name,
    String description,LocalDate startDate,LocalDate endDate,
    String ageStatus) {
    
}
