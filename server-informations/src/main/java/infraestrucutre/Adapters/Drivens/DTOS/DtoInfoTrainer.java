package infraestrucutre.Adapters.Drivens.DTOS;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

public record DtoInfoTrainer(
    Long trainerId,
    String mamalon,
    String email,
    String name,
    String secondName,
    String lastNameP,
    String lastNameM,
    String age,
    String height,
    String weight,
    LocalDate beggining,  // Use wrapper classes to handle nulls
    Integer clients
) {
}