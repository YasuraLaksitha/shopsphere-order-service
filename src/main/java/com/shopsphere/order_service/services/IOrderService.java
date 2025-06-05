package com.shopsphere.order_service.services;

import com.shopsphere.order_service.dto.OrderRequestDTO;
import com.shopsphere.order_service.dto.ShippingResponseDTO;

public interface IOrderService {

    /**
     *
     * @param orderRequest - order request
     * @return - response from payment provider
     *
     */
    <T> T placeOrder(final OrderRequestDTO orderRequest);

    /**
     *
     * @param orderId - orderId
     */
    ShippingResponseDTO handleShippingRequest(final Long orderId);

    /**
     *
     * @param orderId - orderID
     * @param orderStatus - order status
     */
    void updateOrderStatus(Long orderId, String orderStatus);
}
