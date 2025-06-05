package com.shopsphere.order_service.services.client;

import com.shopsphere.order_service.dto.ShippingRequestDTO;

public interface ICacheService {

    /**
     *
     * @param shippingRequestDTO - shipping request
     * @param orderId - orderID
     */
    void saveIntoCache(final ShippingRequestDTO shippingRequestDTO, final Long orderId);

    /**
     *
     * @param orderId - orderId
     * @return - shippingRequest object
     */
    ShippingRequestDTO retrieveByOrderId(final Long orderId);
}
