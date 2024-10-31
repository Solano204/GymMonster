package infraestrucutre.Adapters.Drivens.Entities;

import java.time.LocalDate;

public record AllTrainer(
        Long id, String username,String password,String email,String name,
        String secondName, String lastNameP, String lastNameM, String age, String height, String weight, LocalDate startDate) {
}