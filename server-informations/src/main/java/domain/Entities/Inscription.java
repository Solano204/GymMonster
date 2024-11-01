package domain.Entities;
import java.time.LocalDate;
public record Inscription( LocalDate startDate, LocalDate startInscription, LocalDate endInscription, Double price ) {}
