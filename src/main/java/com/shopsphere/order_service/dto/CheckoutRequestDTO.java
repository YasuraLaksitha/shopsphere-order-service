package com.shopsphere.order_service.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CheckoutRequestDTO {

    private String paymentMethod;

    private List<OrderItemDTO> orderItems;

}
