package infraestrucutre.Adapters.Drivens.DTOS;

public record DtoInfoGeneralClient(
        Long clientId,
        String username,
        String email,
        String name,
        String secondName,
        String lastNameP,
        String lastNameM,
        String age,
        String height,
        String weight,
        String membershipType,
        String trainerName
        ) {
}
        