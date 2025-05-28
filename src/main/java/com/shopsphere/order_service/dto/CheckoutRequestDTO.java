package com.shopsphere.order_service.dto;

import com.shopsphere.order_service.utils.PaymentMethod;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CheckoutRequestDTO {

    private PaymentMethod paymentMethod;

    private List<OrderItemDTO> orderItems;

}
