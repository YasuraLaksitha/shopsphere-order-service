package com.shopsphere.order_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderItemDTO {

    private Long productName;

    private Integer quantity;

    private Double unitPrice;

    private Double totalPrice;

}
