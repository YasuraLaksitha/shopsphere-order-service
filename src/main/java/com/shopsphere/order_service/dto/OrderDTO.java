package com.shopsphere.order_service.dto;

import com.shopsphere.order_service.utils.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OrderDTO {

    private List<OrderItemDTO> orderItemIds;

    private OrderStatus orderStatus;

    private String totalOrderPrice;
}
