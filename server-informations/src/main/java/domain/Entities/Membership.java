package domain.Entities;

public record Membership(
    String name,
    String description,
    String price,
    String status,
    boolean hasFoodCourt,
    boolean hasPool,
    boolean hasCardio
){
    
}
