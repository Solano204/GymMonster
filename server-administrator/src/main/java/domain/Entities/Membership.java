package domain.Entities;

public record Membership(
    String membershipType,
    String description,
    boolean hasCardio,
    boolean hasPool,
    boolean hasFoodCourt
){
    
}
