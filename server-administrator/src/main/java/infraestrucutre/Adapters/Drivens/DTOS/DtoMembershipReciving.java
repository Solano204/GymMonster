package infraestrucutre.Adapters.Drivens.DTOS;

public record DtoMembershipReciving(
        String membershipType,
        String description,
        boolean hasCardio,
        boolean hasPool,
        boolean hasFoodCourt) {
}