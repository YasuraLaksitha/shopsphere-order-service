package com.shopsphere.order_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Data;

import java.util.UUID;

@Data
public class ShippingRequestDTO {

    @Null(message = "This value should be empty")
    private Long orderId;

    @NotEmpty(message = "Tracking number required for tack order")
    private UUID trackingNumber;

    @Valid
    @NotNull(message = "Destination address details required")
    private DestinationAddressDTO customerAddress;

    private boolean isShippingMethodExpress;
}
