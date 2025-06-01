package com.shopsphere.order_service.services;

import com.shopsphere.order_service.dto.OrderRequestDTO;

public interface IOrderService {

    /**
     *
     * @param orderRequest - order request
     * @return
     *
     */
    <T> T placeOrder(final OrderRequestDTO orderRequest);
}
