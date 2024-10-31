package infraestrucutre.Adapters.Drivens.DTOS;

import java.time.LocalDate;
public record DtoPoolSent(
    Integer id,
    String name,
    String description,
    LocalDate dateClean,
    LocalDate startDate,
    LocalDate endDate) {

}
