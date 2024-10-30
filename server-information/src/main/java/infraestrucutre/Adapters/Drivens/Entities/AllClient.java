package infraestrucutre.Adapters.Drivens.Entities;



import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.*;

public record AllClient(
        Long id, String username,String password,String email, String trainername, String name,
        String secondname, String lastnamep, String lastnamem, String age, String height, String weight,
        String membershiptype, LocalDate startdate, LocalDate startinscription, LocalDate endinscription, Double price) {
}
