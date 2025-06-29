package com.shopsphere.order_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Data;

import java.util.UUID;

@Data
@Schema(
        name = "Shipping Request",
        description = "Schema to hold order destination information"
)
public class ShippingRequestDTO {

    @Null(message = "This value should be empty")
    private Long orderId;

    @Schema(description = "Order tracking number", example = "12345689")
    @NotNull(message = "Tracking number required for track order")
    private UUID trackingNumber;

    @Schema(description = "Destination address information")
    @Valid
    @NotNull(message = "Destination address details required")
    private DestinationAddressDTO destinationAddress;

    @Schema(description = "Shipping method confirmation", example = "true")
    private boolean isShippingMethodExpress;
}
