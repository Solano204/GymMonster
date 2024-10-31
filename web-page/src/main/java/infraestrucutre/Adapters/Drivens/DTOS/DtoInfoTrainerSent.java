package infraestrucutre.Adapters.Drivens.DTOS;

public record DtoInfoTrainerSent(
    Long trainerId,
    String username,
    String email,
    String name,
    String secondName,
    String lastNameP,
    String lastNameM,
    String age,
    String height,
    String weight
) {
}
