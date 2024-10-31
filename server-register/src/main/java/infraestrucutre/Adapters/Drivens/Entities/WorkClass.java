package infraestrucutre.Adapters.Drivens.Entities;

import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public record WorkClass(
        Long id,
        String name,
        String description,
        String duration) {
}
