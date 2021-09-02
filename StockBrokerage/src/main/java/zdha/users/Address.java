package zdha.users;

public record Address(
        String streetAddress,
        String city,
        String state,
        String zipCode,
        String country
) {
}