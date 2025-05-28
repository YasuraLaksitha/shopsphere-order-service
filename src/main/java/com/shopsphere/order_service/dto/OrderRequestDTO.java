package com.shopsphere.order_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OrderRequestDTO {

    @NotEmpty(message = "Invalid order request")
    private String code;

    @NotNull(message = "order should contains one or more items")
    @Valid
    private List<OrderItemDTO> orderItems;

    private String orderStatus;

    @NotNull(message = "Payment method is required")
    private String paymentMethod;

    @NotNull(message = "Order price is required")
    @PositiveOrZero(message = "Order price should be positive or zero")
    private Double totalOrderPrice;
}
