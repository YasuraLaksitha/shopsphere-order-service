package com.shopsphere.order_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@Schema(
        name = "Order Item",
        description = "Schema to hold order item information"
)
public class OrderItemDTO {

    @Schema(description = "Item name representing the product")
    @NotEmpty(message = "Product name is required")
    private String productName;

    @Schema(description = "Item quantity", example = "2")
    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private Integer quantity;

    @Schema(description = "Total item price of one a single product that user favored to purchase")
    @NotNull(message = "Unit price is required")
    @Positive(message = "Unit price must be positive")
    private BigDecimal unitPrice;

    @Schema(description = "Total price for order")
    @NotNull(message = "Total price is required")
    @Positive(message = "Total price must be positive")
    private BigDecimal totalPrice;
}
