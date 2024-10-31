package infraestrucutre.Adapters.Drivens.DTOS;

public record DtoMembershipSent(
        Integer id,
        String membershipType,
        String description,
        boolean hasCardio,
        boolean hasPool,
        boolean hasFoodCourt) {
}