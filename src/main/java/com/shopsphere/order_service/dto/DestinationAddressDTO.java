package com.shopsphere.order_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(
        name = "Order Item",
        description = "Schema to hold order item information"
)
public class DestinationAddressDTO {

    @Schema(description = "No of the user destination", example = "13/10")
    @NotEmpty(message = "Street number is required")
    @Pattern(regexp = "^\\d+[/\\-]?\\d*$", message = "Invalid street number format")
    private String number;

    @Schema(description = "Street name", example = "Main Street")
    @NotEmpty(message = "Street name is required")
    @Pattern(regexp = "^[A-Za-z\\s.'-]{2,50}$", message = "Street name contains invalid characters")
    private String street;

    @Schema(description = "Destination city", example = "Washington")
    @NotEmpty(message = "City name is required")
    @Pattern(regexp = "^[A-Za-z\\s.'-]{2,50}$", message = "City name contains invalid characters")
    private String city;

    @Schema(description = "State of the destination", example = "US")
    @NotEmpty(message = "State is required")
    @Pattern(regexp = "^[A-Za-z]{2,3}$", message = "State should be a valid 2-3 letter code")
    private String state;

    @Schema(description = "Postal code", example = "20000")
    @NotEmpty(message = "Postal code is required")
    @Pattern(regexp = "^\\d{4,10}$", message = "Postal code must be numeric and 4-10 digits long")
    private String postalCode;

    @Schema(description = "Country of the destination", example = "USA")
    @NotEmpty(message = "Country is required")
    @Pattern(regexp = "^[A-Za-z\\s]{2,56}$", message = "Country name must only contain letters and spaces")
    private String country;
}
