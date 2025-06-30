package com.shopsphere.order_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@Schema(
        name = "Order Request",
        description = "Schema to hold order request information"
)
public class OrderRequestDTO {

    @NotEmpty(message = "Invalid order request")
    private String code;

    @Schema(description = "Order items representing product details")
    @NotNull(message = "order should contains one or more items")
    @Valid
    private List<OrderItemDTO> orderItems;

    @Schema(description = "Order status representing the current order state", example = "Pending")
    private String orderStatus;

    @Schema(description = "Payment method", example = "Stripe")
    @NotNull(message = "Payment method is required")
    private String paymentMethod;

    @Schema(description = "Order price to be payed is USD", example = "113.00")
    @NotNull(message = "Order price is required")
    @PositiveOrZero(message = "Order price should be positive or zero")
    private BigDecimal totalOrderPrice;

    @Schema(description = "Destination details to handle shipping")
    @NotNull(message = "Shipping details required")
    @Valid
    private ShippingRequestDTO shippingRequest;
}
